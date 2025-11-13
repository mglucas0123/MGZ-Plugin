package mglucas0123.commands;

import mglucas0123.Principal;
import mglucas0123.inventory.InventoryBackup;
import mglucas0123.inventory.InventoryBackupManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class InventoryBackupCommand implements CommandExecutor {
    
    private final Principal plugin;
    private final InventoryBackupManager backupManager;
    
    public InventoryBackupCommand(Principal plugin, InventoryBackupManager backupManager) {
        this.plugin = plugin;
        this.backupManager = backupManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "restore":
            case "restaurar":
                if (!player.hasPermission("mgz.inventory.restore")) {
                    player.sendMessage("§cVocê não tem permissão para restaurar inventário!");
                    return true;
                }
                
                int backupIndex = -1;
                if (args.length >= 2) {
                    try {
                        backupIndex = Integer.parseInt(args[1]) - 1; 
                    } catch (NumberFormatException e) {
                        player.sendMessage("§cNúmero inválido!");
                        return true;
                    }
                }
                
                restoreBackup(player, backupIndex);
                break;
                
            case "list":
            case "listar":
                listBackups(player);
                break;
                
            case "info":
                showInfo(player);
                break;
                
            case "backup":
            case "salvar":
                if (!player.hasPermission("mgz.inventory.backup")) {
                    player.sendMessage("§cVocê não tem permissão para criar backup manual!");
                    return true;
                }
                createManualBackup(player);
                break;
                
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void restoreBackup(Player player, int backupIndex) {
        List<InventoryBackup> history = backupManager.getBackupHistory(player.getUniqueId());
        
        if (history.isEmpty()) {
            player.sendMessage("§cVocê não tem nenhum backup disponível!");
            return;
        }
        
        if (backupIndex < 0 || backupIndex >= history.size()) {
            backupIndex = history.size() - 1; 
        }
        
        InventoryBackup backup = history.get(backupIndex);
        
        player.sendMessage("§e§l⚠ ATENÇÃO!");
        player.sendMessage("§7Você está prestes a restaurar §lTODA SUA PLAYER DATA§7!");
        player.sendMessage("§7Backup de: §f" + getFormattedTime(backup.getTimestamp()));
        player.sendMessage("");
        player.sendMessage("§7O que será restaurado:");
        player.sendMessage("  §7• Inventário: §f" + backup.getItemCount() + " itens");
        player.sendMessage("  §7• Ender Chest: §f" + backup.getEnderChestItemCount() + " itens");
        player.sendMessage("  §7• Nível: §f" + backup.getLevel());
        player.sendMessage("  §7• Vida: §f" + String.format("%.1f", backup.getHealth()) + " ❤");
        player.sendMessage("  §7• Fome: §f" + backup.getFoodLevel() + " 🍖");
        player.sendMessage("  §7• Game Mode: §f" + backup.getGameMode().name());
        player.sendMessage("  §7• Efeitos: §f" + backup.getPotionEffectCount() + " ativos");
        player.sendMessage("  §7• Local: §f" + backup.getLocation());
        player.sendMessage("");
        player.sendMessage("§cTODOS seus dados atuais serão §lSOBRESCRITOS§c!");
        player.sendMessage("§aDigite §f/invbackup confirm §apara confirmar");
        player.sendMessage("§7Ou espere 30 segundos para cancelar");
        
        
        String confirmKey = player.getUniqueId().toString() + "_restore";
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            
            player.removeMetadata(confirmKey, plugin);
        }, 600L);
        
        
        player.setMetadata(confirmKey, new org.bukkit.metadata.FixedMetadataValue(plugin, backupIndex));
    }
    
    private void listBackups(Player player) {
        List<InventoryBackup> history = backupManager.getBackupHistory(player.getUniqueId());
        
        if (history.isEmpty()) {
            player.sendMessage("§cVocê não tem nenhum backup disponível!");
            return;
        }
        
        player.sendMessage("§6§l========== Seus Backups ==========");
        player.sendMessage("");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        for (int i = 0; i < history.size(); i++) {
            InventoryBackup backup = history.get(i);
            String time = sdf.format(new Date(backup.getTimestamp()));
            long minutesAgo = (System.currentTimeMillis() - backup.getTimestamp()) / 60000;
            
            player.sendMessage(String.format("§e#%d §7- §f%s §7(%d min atrás)",
                i + 1, time, minutesAgo));
            player.sendMessage(String.format("    §7Inv: §f%d §7| EnderChest: §f%d §7| Lvl: §f%d §7| HP: §f%.1f",
                backup.getItemCount(), 
                backup.getEnderChestItemCount(),
                backup.getLevel(),
                backup.getHealth()));
        }
        
        player.sendMessage("");
        player.sendMessage("§7Use §f/invbackup restore <número> §7para restaurar");
        player.sendMessage("§6§l==================================");
    }
    
    private void showInfo(Player player) {
        List<InventoryBackup> history = backupManager.getBackupHistory(player.getUniqueId());
        
        player.sendMessage("§6§l========== Sistema de Backup ==========");
        player.sendMessage("§7Status: §aAtivo");
        player.sendMessage("§7Backups salvos: §f" + history.size() + "/" + 
                          plugin.getConfig().getInt("InventoryBackup.MaxBackupsPerPlayer", 5));
        
        if (!history.isEmpty()) {
            InventoryBackup last = history.get(history.size() - 1);
            long minutesAgo = (System.currentTimeMillis() - last.getTimestamp()) / 60000;
            player.sendMessage("§7Último backup: §f" + minutesAgo + " minutos atrás");
        }
        
        int autoInterval = plugin.getConfig().getInt("InventoryBackup.AutoBackupInterval", 300);
        player.sendMessage("§7Backup automático: §fa cada " + (autoInterval / 60) + " minutos");
        player.sendMessage("");
        player.sendMessage("§aO sistema protege seu inventário automaticamente!");
        player.sendMessage("§7Use §f/invbackup list §7para ver seus backups");
        player.sendMessage("§6§l======================================");
    }
    
    private void createManualBackup(Player player) {
        backupManager.createBackup(player, "Manual");
        player.sendMessage("§a§l✔ Backup criado com sucesso!");
        player.sendMessage("§7Seu inventário foi salvo manualmente.");
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6§l========== Backup de Inventário ==========");
        player.sendMessage("§e/invbackup list §7- Lista seus backups");
        player.sendMessage("§e/invbackup restore [número] §7- Restaura backup");
        player.sendMessage("§e/invbackup info §7- Informações do sistema");
        player.sendMessage("§e/invbackup backup §7- Cria backup manual");
        player.sendMessage("§6§l=========================================");
    }
    
    private String getFormattedTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }
}
