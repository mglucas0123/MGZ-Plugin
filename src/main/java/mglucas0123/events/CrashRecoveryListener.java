package mglucas0123.events;

import mglucas0123.Principal;
import mglucas0123.inventory.InventoryBackup;
import mglucas0123.inventory.InventoryBackupManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class CrashRecoveryListener implements Listener {
    
    private final Principal plugin;
    private final InventoryBackupManager backupManager;
    
    public CrashRecoveryListener(Principal plugin, InventoryBackupManager backupManager) {
        this.plugin = plugin;
        this.backupManager = backupManager;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinAfterCrash(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;
            
            if (isPlayerDataReset(player)) {
                handleCrashedPlayer(player);
            }
        }, 1L);
    }
    
    private boolean isPlayerDataReset(Player player) {
        List<InventoryBackup> history = backupManager.getBackupHistory(player.getUniqueId());
        if (history.isEmpty()) {
            return false;
        }
        
        InventoryBackup lastBackup = history.get(history.size() - 1);
        
        int resetScore = 0;
        
        if (isInventoryEmpty(player) && lastBackup.getItemCount() > 0) {
            resetScore += 3;
        }
        
        if (isEnderChestEmpty(player) && lastBackup.getEnderChestItemCount() > 0) {
            resetScore += 2;
        }
        
        if (player.getLevel() == 0 && player.getTotalExperience() == 0 && lastBackup.getLevel() > 0) {
            resetScore += 2;
        }
        
        if (player.getFoodLevel() == 0 && lastBackup.getFoodLevel() > 10) {
            resetScore += 2;
        }
        
        if (player.getHealth() == 20.0 && player.getFoodLevel() == 0) {
            resetScore += 1;
        }
        
        return resetScore >= 5;
    }
    
    private void handleCrashedPlayer(Player player) {
        List<InventoryBackup> history = backupManager.getBackupHistory(player.getUniqueId());
        
        if (history.isEmpty()) {
            return;
        }
        
        InventoryBackup lastBackup = history.get(history.size() - 1);
        
        long minutesAgo = (System.currentTimeMillis() - lastBackup.getTimestamp()) / 60000;
        
        if (minutesAgo <= 15) {
            boolean success = backupManager.restoreBackup(player, history.size() - 1);
            
            if (success) {
                player.sendMessage("");
                player.sendMessage("§c§l⚠ DETECÇÃO DE CRASH ⚠");
                player.sendMessage("§7Detectamos que você perdeu seus dados após um crash do servidor!");
                player.sendMessage("");
                player.sendMessage("§a§l✔ SEUS DADOS FORAM RESTAURADOS AUTOMATICAMENTE!");
                player.sendMessage("§7Backup restaurado: §f" + minutesAgo + " minuto(s) atrás");
                player.sendMessage("");
                player.sendMessage("§7Dados restaurados:");
                player.sendMessage("  §7• Inventário: §f" + lastBackup.getItemCount() + " itens");
                player.sendMessage("  §7• Ender Chest: §f" + lastBackup.getEnderChestItemCount() + " itens");
                player.sendMessage("  §7• Nível: §f" + lastBackup.getLevel());
                player.sendMessage("  §7• Vida: §f" + String.format("%.1f", lastBackup.getHealth()) + " ❤");
                player.sendMessage("  §7• Game Mode: §f" + lastBackup.getGameMode().name());
                player.sendMessage("");
                player.sendMessage("§eDesculpe pelo inconveniente! §7A equipe já foi notificada.");
                player.sendMessage("");
                
                Bukkit.getConsoleSender().sendMessage("");
                Bukkit.getConsoleSender().sendMessage("§c§l========================================");
                Bukkit.getConsoleSender().sendMessage("§c§l⚠ CRASH RECOVERY ATIVADO ⚠");
                Bukkit.getConsoleSender().sendMessage("§c§l========================================");
                Bukkit.getConsoleSender().sendMessage("§7Jogador: §f" + player.getName());
                Bukkit.getConsoleSender().sendMessage("§7Situação: §cPlayer data resetada após crash");
                Bukkit.getConsoleSender().sendMessage("§7Ação: §aRestauração automática realizada");
                Bukkit.getConsoleSender().sendMessage("§7Backup: §f" + minutesAgo + " minuto(s) atrás");
                Bukkit.getConsoleSender().sendMessage("§7Itens restaurados: §f" + lastBackup.getItemCount());
                Bukkit.getConsoleSender().sendMessage("§7XP restaurado: §f" + lastBackup.getLevel() + " níveis");
                Bukkit.getConsoleSender().sendMessage("§c§l========================================");
                Bukkit.getConsoleSender().sendMessage("");
                
                if (plugin.getConfig().getBoolean("InventoryBackup.NotifyStaffOnCrashRecovery", true)) {
                    notifyStaff(player, minutesAgo);
                }
                
            } else {
                player.sendMessage("§c§lERRO: Falha ao restaurar seus dados!");
                player.sendMessage("§cPor favor, contate um administrador imediatamente!");
                
                Bukkit.getConsoleSender().sendMessage("§c§l[ERRO CRÍTICO] Falha ao restaurar player data de " + player.getName());
            }
            
        } else {
            player.sendMessage("§e§l⚠ AVISO!");
            player.sendMessage("§7Detectamos perda de dados, mas o último backup é de §f" + minutesAgo + " min atrás.");
            player.sendMessage("§7Contate um administrador para verificar!");
            
            Bukkit.getConsoleSender().sendMessage(
                String.format("§e[MGZ-CrashRecovery] §cPlayer data de %s foi resetada! Backup disponível: %d min atrás (muito antigo para auto-restore)",
                    player.getName(), minutesAgo)
            );
            
            notifyStaff(player, minutesAgo);
        }
    }

    private void notifyStaff(Player affected, long minutesAgo) {
        String message = String.format("§c[CrashRecovery] §fJogador §e%s §fperdeu dados no crash! §7(backup: %d min atrás)",
            affected.getName(), minutesAgo);
        
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("mgz.playerdata") || staff.isOp()) {
                staff.sendMessage(message);
            }
        }
    }

    private boolean isInventoryEmpty(Player player) {
        for (org.bukkit.inventory.ItemStack item : player.getInventory().getContents()) {
            if (item != null) return false;
        }
        
        for (org.bukkit.inventory.ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null) return false;
        }
        
        if (player.getInventory().getItemInOffHand() != null) {
            return false;
        }
        
        return true;
    }
    
    private boolean isEnderChestEmpty(Player player) {
        for (org.bukkit.inventory.ItemStack item : player.getEnderChest().getContents()) {
            if (item != null) return false;
        }
        return true;
    }
}
