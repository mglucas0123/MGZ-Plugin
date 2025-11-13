package mglucas0123.inventory;

import mglucas0123.Principal;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Gerenciador de backups de inventário com cache em memória
 */
public class InventoryBackupManager {
    
    private final Principal plugin;
    private final File backupsFolder;
    
    // Cache em memória - sempre atualizado (performance)
    private final Map<UUID, InventoryBackup> memoryCache;
    
    // Histórico de backups no disco
    private final Map<UUID, List<InventoryBackup>> backupHistory;
    
    // Jogadores com mudanças não salvas (dirty flag)
    private final Set<UUID> dirtyPlayers;
    
    // Última mudança de cada jogador (para debounce)
    private final Map<UUID, Long> lastChangeTime;
    
    // Locks por jogador para escrita em disco
    private final Map<UUID, Object> fileLocks;
    
    private int maxBackupsPerPlayer;
    private int debounceTime; // Tempo em segundos antes de salvar
    
    public InventoryBackupManager(Principal plugin) {
        this.plugin = plugin;
        this.backupsFolder = new File(plugin.getDataFolder(), "inventory-backups");
        this.memoryCache = new ConcurrentHashMap<>();
        this.backupHistory = new ConcurrentHashMap<>();
        this.dirtyPlayers = new CopyOnWriteArraySet<>();
        this.lastChangeTime = new ConcurrentHashMap<>();
    this.fileLocks = new ConcurrentHashMap<>();
        
        // Criar pasta de backups se não existir
        if (!backupsFolder.exists()) {
            backupsFolder.mkdirs();
        }
        
        // Carregar configuração
        this.maxBackupsPerPlayer = plugin.getConfig().getInt("InventoryBackup.MaxBackupsPerPlayer", 5);
        this.debounceTime = plugin.getConfig().getInt("InventoryBackup.DebounceTime", 10); // 10 segundos padrão
        
        // Carregar backups existentes
        loadAllBackups();
        
        // Iniciar tasks
        startAutoBackupTask();
        startDirtySaveTask();
        startMemoryCacheUpdateTask();
    }
    
    /**
     * Atualiza o cache em memória (instantâneo, sem salvar no disco)
     * Agora captura TODA a player data
     */
    public void updateCache(Player player) {
        InventoryBackup backup = new InventoryBackup(player);
        
        UUID uuid = player.getUniqueId();
        
        // Verificar se houve mudança real
        InventoryBackup current = memoryCache.get(uuid);
        if (inventoriesEqual(current, backup)) {
            return; // Sem mudanças, não fazer nada
        }
        
        // Atualizar cache em memória
        memoryCache.put(uuid, backup);
        
        // Marcar como dirty (precisa salvar no disco)
        dirtyPlayers.add(uuid);
        lastChangeTime.put(uuid, System.currentTimeMillis());
    }
    
    /**
     * Cria um backup COMPLETO da player data (salva no disco)
     */
    public InventoryBackup createBackup(Player player, String reason) {
        InventoryBackup backup = new InventoryBackup(player);
        
        UUID uuid = player.getUniqueId();
        
        // Atualizar cache
        memoryCache.put(uuid, backup);
        
        // Adicionar ao histórico
        backupHistory.computeIfAbsent(uuid, k -> new ArrayList<>()).add(backup);
        
        // Limitar quantidade de backups por jogador
        List<InventoryBackup> history = backupHistory.get(uuid);
        while (history.size() > maxBackupsPerPlayer) {
            history.remove(0); // Remove o mais antigo
        }
        
        // Salvar no disco (assíncrono)
        saveBackupAsync(uuid, backup);
        
        // Remover flag dirty
        dirtyPlayers.remove(uuid);
        
        // Log
        if (plugin.getConfig().getBoolean("InventoryBackup.LogBackups", false)) {
            Bukkit.getConsoleSender().sendMessage(
                String.format("§7[§6MGZ-Backup§7] §fBackup criado para §e%s §7(%s) - %d itens",
                    player.getName(), reason, backup.getItemCount())
            );
        }
        
        return backup;
    }
    
    /**
     * Verifica se dois inventários são iguais (para evitar salvamentos desnecessários)
     */
    private boolean inventoriesEqual(InventoryBackup backup1, InventoryBackup backup2) {
        // Se ambos são null, são iguais
        if (backup1 == null && backup2 == null) {
            return true;
        }
        
        // Se apenas um é null, são diferentes
        if (backup1 == null || backup2 == null) {
            return false;
        }
        
        // Ambos não-null, comparar estado
        return backup1.hasSameState(backup2);
    }
    
    /**
     * Verifica se o inventário do jogador está vazio e restaura se necessário
     */
    public boolean checkAndRestoreIfEmpty(Player player) {
        // Verificar se o inventário está realmente vazio
        if (!isInventoryEmpty(player)) {
            return false;
        }
        
        UUID uuid = player.getUniqueId();
        
        // Primeiro tentar do cache em memória (mais rápido)
        InventoryBackup backup = memoryCache.get(uuid);
        
        if (backup == null || backup.isEmpty()) {
            // Tentar pegar do histórico no disco
            List<InventoryBackup> history = backupHistory.get(uuid);
            if (history == null || history.isEmpty()) {
                return false;
            }
            backup = history.get(history.size() - 1); // Pegar o mais recente
        }
        
        // Restaurar TODA a player data
        backup.restore(player);
        player.updateInventory();
        
        // Notificar jogador
        player.sendMessage("§a§l⚠ PLAYER DATA RESTAURADA!");
        player.sendMessage("§7Detectamos que seu inventário estava vazio.");
        player.sendMessage("§7Todos os seus dados foram restaurados do último backup.");
        player.sendMessage("§7Backup de: §f" + getFormattedTime(backup.getTimestamp()));
        
        // Log
        Bukkit.getConsoleSender().sendMessage(
            String.format("§c[§6MGZ-Backup§c] §e⚠ RESTAURAÇÃO AUTOMÁTICA: §f%s §7- %d itens restaurados",
                player.getName(), backup.getItemCount())
        );
        
        return true;
    }
    
    /**
     * Restaura o último backup de um jogador
     */
    public boolean restoreBackup(Player player, int backupIndex) {
        List<InventoryBackup> history = backupHistory.get(player.getUniqueId());
        
        if (history == null || history.isEmpty()) {
            return false;
        }
        
        if (backupIndex < 0 || backupIndex >= history.size()) {
            backupIndex = history.size() - 1; // Último backup
        }
        
        InventoryBackup backup = history.get(backupIndex);
        backup.restore(player); // Restaura TUDO
        player.updateInventory();
        
        return true;
    }
    
    /**
     * Verifica se o inventário está vazio
     */
    private boolean isInventoryEmpty(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] armor = player.getInventory().getArmorContents();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        
        // Verificar itens do inventário
        for (ItemStack item : contents) {
            if (item != null && item.getType() != Material.AIR) return false;
        }
        
        // Verificar armadura
        for (ItemStack item : armor) {
            if (item != null && item.getType() != Material.AIR) return false;
        }
        
        // Verificar mão secundária
        return offHand == null || offHand.getType() == Material.AIR;
    }
    
    /**
     * Salva backup no disco (assíncrono)
     */
    private void saveBackupAsync(UUID playerUUID, InventoryBackup backup) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> writeBackupToDisk(playerUUID, backup));
    }
    
    /**
     * Carrega todos os backups do disco
     */
    private void loadAllBackups() {
        if (!backupsFolder.exists()) return;
        
        File[] playerFolders = backupsFolder.listFiles();
        if (playerFolders == null) return;
        
        int totalLoaded = 0;
        
        for (File playerFolder : playerFolders) {
            if (!playerFolder.isDirectory()) continue;
            
            try {
                UUID playerUUID = UUID.fromString(playerFolder.getName());
                File[] backupFiles = playerFolder.listFiles((dir, name) -> name.endsWith(".backup"));
                
                if (backupFiles == null) continue;
                
                // Ordenar por timestamp (nome do arquivo)
                Arrays.sort(backupFiles, Comparator.comparingLong(f -> {
                    String name = f.getName().replace(".backup", "");
                    try {
                        return Long.parseLong(name);
                    } catch (NumberFormatException e) {
                        return 0L;
                    }
                }));
                
                List<InventoryBackup> history = new ArrayList<>();
                
                // Carregar backups (manter apenas os mais recentes)
                int start = Math.max(0, backupFiles.length - maxBackupsPerPlayer);
                for (int i = start; i < backupFiles.length; i++) {
                    try (ObjectInputStream ois = new BukkitObjectInputStream(new FileInputStream(backupFiles[i]))) {
                        InventoryBackup backup = (InventoryBackup) ois.readObject();
                        history.add(backup);
                        totalLoaded++;
                    } catch (Exception e) {
                        // Ignorar backups corrompidos
                    }
                }
                
                if (!history.isEmpty()) {
                    backupHistory.put(playerUUID, history);
                    memoryCache.put(playerUUID, history.get(history.size() - 1));
                }
                
                // Deletar backups antigos
                for (int i = 0; i < start; i++) {
                    backupFiles[i].delete();
                }
                
            } catch (IllegalArgumentException e) {
                // UUID inválido, ignorar
            }
        }
        
        if (totalLoaded > 0) {
            Bukkit.getConsoleSender().sendMessage(
                String.format("§a[MGZ-Backup] %d backups carregados para %d jogadores",
                    totalLoaded, backupHistory.size())
            );
        }
    }
    
    /**
     * Task de atualização do cache em memória (a cada 1 segundo)
     */
    public void startMemoryCacheUpdateTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!isInventoryEmpty(player)) {
                    updateCache(player);
                }
            }
        }, 20L, 20L); // A cada 1 segundo
    }
    
    /**
     * Task de salvamento de jogadores dirty (mudanças pendentes)
     */
    public void startDirtySaveTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            
            for (UUID uuid : new HashSet<>(dirtyPlayers)) {
                Long lastChange = lastChangeTime.get(uuid);
                
                if (lastChange == null) {
                    dirtyPlayers.remove(uuid);
                    continue;
                }
                
                // Verificar se passou tempo suficiente desde a última mudança (debounce)
                if (now - lastChange >= (debounceTime * 1000L)) {
                    Player player = Bukkit.getPlayer(uuid);
                    
                    if (player != null && player.isOnline()) {
                        createBackup(player, "AutoSave");
                    } else {
                        // Jogador offline, salvar do cache
                        InventoryBackup cachedBackup = memoryCache.get(uuid);
                        if (cachedBackup != null) {
                            List<InventoryBackup> history = backupHistory.computeIfAbsent(uuid, k -> new ArrayList<>());
                            history.add(cachedBackup);
                            trimHistory(history);
                            writeBackupToDisk(uuid, cachedBackup);
                        }
                        dirtyPlayers.remove(uuid);
                    }
                }
            }
        }, 20L, 20L); // Verificar a cada 1 segundo
    }
    
    /**
     * Inicia task de backup automático periódico no disco
     */
    private void startAutoBackupTask() {
        int interval = plugin.getConfig().getInt("InventoryBackup.AutoBackupInterval", 300); // 5 minutos padrão
        
        if (interval <= 0) return; // Desabilitado
        
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!isInventoryEmpty(player)) {
                    createBackup(player, "Periódico");
                }
            }
        }, interval * 20L, interval * 20L);
    }
    
    /**
     * Força salvamento imediato de um jogador
     */
    public void forceSave(Player player) {
        if (!isInventoryEmpty(player)) {
            createBackup(player, "Forçado");
        }
    }
    
    /**
     * Lista os backups de um jogador
     */
    public List<InventoryBackup> getBackupHistory(UUID playerUUID) {
        return new ArrayList<>(backupHistory.getOrDefault(playerUUID, new ArrayList<>()));
    }
    
    /**
     * Formata timestamp para exibição
     */
    private String getFormattedTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * Limpa backups de um jogador
     */
    public void clearBackups(UUID playerUUID) {
        memoryCache.remove(playerUUID);
        backupHistory.remove(playerUUID);
        dirtyPlayers.remove(playerUUID);
        lastChangeTime.remove(playerUUID);
        
        File playerFolder = new File(backupsFolder, playerUUID.toString());
        if (playerFolder.exists()) {
            File[] files = playerFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            playerFolder.delete();
        }
    }
    
    /**
     * Salva todos os jogadores dirty (ao desligar o servidor)
     */
    public void saveAllDirty() {
        int savedCount = 0;
        for (UUID uuid : new HashSet<>(dirtyPlayers)) {
            InventoryBackup backup = memoryCache.get(uuid);
            if (backup != null) {
                List<InventoryBackup> history = backupHistory.computeIfAbsent(uuid, k -> new ArrayList<>());
                history.add(backup);
                trimHistory(history);
                writeBackupToDisk(uuid, backup);
                savedCount++;
            }
            dirtyPlayers.remove(uuid);
        }

        Bukkit.getConsoleSender().sendMessage("§a[MGZ-Backup] " + savedCount + " backups pendentes salvos!");
    }

    private void writeBackupToDisk(UUID playerUUID, InventoryBackup backup) {
        Object lock = fileLocks.computeIfAbsent(playerUUID, id -> new Object());

        synchronized (lock) {
            File playerFolder = new File(backupsFolder, playerUUID.toString());
            if (!playerFolder.exists()) {
                playerFolder.mkdirs();
            }

            Path tempFile;
            try {
                tempFile = Files.createTempFile(playerFolder.toPath(), "backup-", ".tmp");
            } catch (IOException e) {
                Bukkit.getConsoleSender().sendMessage("§c[MGZ-Backup] Erro ao preparar arquivo temporário: " + e.getMessage());
                return;
            }

            try (ObjectOutputStream oos = new BukkitObjectOutputStream(Files.newOutputStream(tempFile))) {
                oos.writeObject(backup);
            } catch (IOException e) {
                Bukkit.getConsoleSender().sendMessage("§c[MGZ-Backup] Erro ao salvar backup: " + e.getMessage());
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                }
                return;
            }

            Path targetFile = new File(playerFolder, backup.getTimestamp() + ".backup").toPath();
            try {
                Files.move(tempFile, targetFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                try {
                    Files.move(tempFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    Bukkit.getConsoleSender().sendMessage("§c[MGZ-Backup] Erro ao mover backup: " + ex.getMessage());
                }
            } catch (IOException e) {
                Bukkit.getConsoleSender().sendMessage("§c[MGZ-Backup] Erro ao mover backup: " + e.getMessage());
            }
        }
    }

    private void trimHistory(List<InventoryBackup> history) {
        while (history.size() > maxBackupsPerPlayer) {
            history.remove(0);
        }
    }
}
