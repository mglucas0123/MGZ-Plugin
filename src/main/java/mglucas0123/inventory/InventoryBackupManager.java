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


public class InventoryBackupManager {
    
    private final Principal plugin;
    private final File backupsFolder;
    
    
    private final Map<UUID, InventoryBackup> memoryCache;
    
    
    private final Map<UUID, List<InventoryBackup>> backupHistory;
    
    
    private final Set<UUID> dirtyPlayers;
    
    
    private final Map<UUID, Long> lastChangeTime;
    
    
    private final Map<UUID, Object> fileLocks;
    
    private int maxBackupsPerPlayer;
    private int debounceTime; 
    
    public InventoryBackupManager(Principal plugin) {
        this.plugin = plugin;
        this.backupsFolder = new File(plugin.getDataFolder(), "inventory-backups");
        this.memoryCache = new ConcurrentHashMap<>();
        this.backupHistory = new ConcurrentHashMap<>();
        this.dirtyPlayers = new CopyOnWriteArraySet<>();
        this.lastChangeTime = new ConcurrentHashMap<>();
    this.fileLocks = new ConcurrentHashMap<>();
        
        
        if (!backupsFolder.exists()) {
            backupsFolder.mkdirs();
        }
        
        
        this.maxBackupsPerPlayer = plugin.getConfig().getInt("InventoryBackup.MaxBackupsPerPlayer", 5);
        this.debounceTime = plugin.getConfig().getInt("InventoryBackup.DebounceTime", 10); 
        
        
        loadAllBackups();
        
        
        startAutoBackupTask();
        startDirtySaveTask();
        startMemoryCacheUpdateTask();
    }
    
    
    public void updateCache(Player player) {
        InventoryBackup backup = new InventoryBackup(player);
        
        UUID uuid = player.getUniqueId();
        
        
        InventoryBackup current = memoryCache.get(uuid);
        if (inventoriesEqual(current, backup)) {
            return; 
        }
        
        
        memoryCache.put(uuid, backup);
        
        
        dirtyPlayers.add(uuid);
        lastChangeTime.put(uuid, System.currentTimeMillis());
    }
    
    
    public InventoryBackup createBackup(Player player, String reason) {
        InventoryBackup backup = new InventoryBackup(player);
        
        UUID uuid = player.getUniqueId();
        
        
        memoryCache.put(uuid, backup);
        
        
        backupHistory.computeIfAbsent(uuid, k -> new ArrayList<>()).add(backup);
        
        
        List<InventoryBackup> history = backupHistory.get(uuid);
        while (history.size() > maxBackupsPerPlayer) {
            history.remove(0); 
        }
        
        
        saveBackupAsync(uuid, backup);
        
        
        dirtyPlayers.remove(uuid);
        
        
        if (plugin.getConfig().getBoolean("InventoryBackup.LogBackups", false)) {
            Bukkit.getConsoleSender().sendMessage(
                String.format("§7[§6MGZ-Backup§7] §fBackup criado para §e%s §7(%s) - %d itens",
                    player.getName(), reason, backup.getItemCount())
            );
        }
        
        return backup;
    }
    
    
    private boolean inventoriesEqual(InventoryBackup backup1, InventoryBackup backup2) {
        
        if (backup1 == null && backup2 == null) {
            return true;
        }
        
        
        if (backup1 == null || backup2 == null) {
            return false;
        }
        
        
        return backup1.hasSameState(backup2);
    }
    
    
    public boolean checkAndRestoreIfEmpty(Player player) {
        
        if (!isInventoryEmpty(player)) {
            return false;
        }
        
        UUID uuid = player.getUniqueId();
        
        
        InventoryBackup backup = memoryCache.get(uuid);
        
        if (backup == null || backup.isEmpty()) {
            
            List<InventoryBackup> history = backupHistory.get(uuid);
            if (history == null || history.isEmpty()) {
                return false;
            }
            backup = history.get(history.size() - 1); 
        }
        
        
        backup.restore(player);
        player.updateInventory();
        
        
        player.sendMessage("§a§l⚠ PLAYER DATA RESTAURADA!");
        player.sendMessage("§7Detectamos que seu inventário estava vazio.");
        player.sendMessage("§7Todos os seus dados foram restaurados do último backup.");
        player.sendMessage("§7Backup de: §f" + getFormattedTime(backup.getTimestamp()));
        
        
        Bukkit.getConsoleSender().sendMessage(
            String.format("§c[§6MGZ-Backup§c] §e⚠ RESTAURAÇÃO AUTOMÁTICA: §f%s §7- %d itens restaurados",
                player.getName(), backup.getItemCount())
        );
        
        return true;
    }
    
    
    public boolean restoreBackup(Player player, int backupIndex) {
        List<InventoryBackup> history = backupHistory.get(player.getUniqueId());
        
        if (history == null || history.isEmpty()) {
            return false;
        }
        
        if (backupIndex < 0 || backupIndex >= history.size()) {
            backupIndex = history.size() - 1; 
        }
        
        InventoryBackup backup = history.get(backupIndex);
        backup.restore(player); 
        player.updateInventory();
        
        return true;
    }
    
    
    private boolean isInventoryEmpty(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] armor = player.getInventory().getArmorContents();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        
        
        for (ItemStack item : contents) {
            if (item != null && item.getType() != Material.AIR) return false;
        }
        
        
        for (ItemStack item : armor) {
            if (item != null && item.getType() != Material.AIR) return false;
        }
        
        
        return offHand == null || offHand.getType() == Material.AIR;
    }
    
    
    private void saveBackupAsync(UUID playerUUID, InventoryBackup backup) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> writeBackupToDisk(playerUUID, backup));
    }
    
    
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
                
                
                Arrays.sort(backupFiles, Comparator.comparingLong(f -> {
                    String name = f.getName().replace(".backup", "");
                    try {
                        return Long.parseLong(name);
                    } catch (NumberFormatException e) {
                        return 0L;
                    }
                }));
                
                List<InventoryBackup> history = new ArrayList<>();
                
                
                int start = Math.max(0, backupFiles.length - maxBackupsPerPlayer);
                for (int i = start; i < backupFiles.length; i++) {
                    try (ObjectInputStream ois = new BukkitObjectInputStream(new FileInputStream(backupFiles[i]))) {
                        InventoryBackup backup = (InventoryBackup) ois.readObject();
                        history.add(backup);
                        totalLoaded++;
                    } catch (Exception e) {
                        
                    }
                }
                
                if (!history.isEmpty()) {
                    backupHistory.put(playerUUID, history);
                    memoryCache.put(playerUUID, history.get(history.size() - 1));
                }
                
                
                for (int i = 0; i < start; i++) {
                    backupFiles[i].delete();
                }
                
            } catch (IllegalArgumentException e) {
                
            }
        }
        
        if (totalLoaded > 0) {
            Bukkit.getConsoleSender().sendMessage(
                String.format("§a[MGZ-Backup] %d backups carregados para %d jogadores",
                    totalLoaded, backupHistory.size())
            );
        }
    }
    
    
    public void startMemoryCacheUpdateTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!isInventoryEmpty(player)) {
                    updateCache(player);
                }
            }
        }, 20L, 20L); 
    }
    
    
    public void startDirtySaveTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            
            for (UUID uuid : new HashSet<>(dirtyPlayers)) {
                Long lastChange = lastChangeTime.get(uuid);
                
                if (lastChange == null) {
                    dirtyPlayers.remove(uuid);
                    continue;
                }
                
                
                if (now - lastChange >= (debounceTime * 1000L)) {
                    Player player = Bukkit.getPlayer(uuid);
                    
                    if (player != null && player.isOnline()) {
                        createBackup(player, "AutoSave");
                    } else {
                        
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
        }, 20L, 20L); 
    }
    
    
    private void startAutoBackupTask() {
        int interval = plugin.getConfig().getInt("InventoryBackup.AutoBackupInterval", 300); 
        
        if (interval <= 0) return; 
        
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!isInventoryEmpty(player)) {
                    createBackup(player, "Periódico");
                }
            }
        }, interval * 20L, interval * 20L);
    }
    
    
    public void forceSave(Player player) {
        if (!isInventoryEmpty(player)) {
            createBackup(player, "Forçado");
        }
    }
    
    
    public List<InventoryBackup> getBackupHistory(UUID playerUUID) {
        return new ArrayList<>(backupHistory.getOrDefault(playerUUID, new ArrayList<>()));
    }
    
    
    private String getFormattedTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }
    
    
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
