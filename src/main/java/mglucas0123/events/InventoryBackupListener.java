package mglucas0123.events;

import mglucas0123.Principal;
import mglucas0123.inventory.InventoryBackupManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryBackupListener implements Listener {
    
    private final Principal plugin;
    private final InventoryBackupManager backupManager;
    
    public InventoryBackupListener(Principal plugin, InventoryBackupManager backupManager) {
        this.plugin = plugin;
        this.backupManager = backupManager;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    boolean restored = backupManager.checkAndRestoreIfEmpty(player);
                    
                    if (!restored && plugin.getConfig().getBoolean("InventoryBackup.BackupOnJoin", true)) {
                        backupManager.createBackup(player, "Login");
                    }
                }
            }
        }.runTaskLater(plugin, 40L);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        if (plugin.getConfig().getBoolean("InventoryBackup.BackupOnQuit", true)) {
            backupManager.forceSave(player);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        backupManager.updateCache(player);
                    }
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        backupManager.updateCache(player);
                    }
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    backupManager.updateCache(player);
                }
            }
        }.runTaskLater(plugin, 1L);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        backupManager.updateCache(player);
                    }
                }
            }.runTaskLater(plugin, 1L);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        if (plugin.getConfig().getBoolean("InventoryBackup.BackupOnDeath", true)) {
            backupManager.forceSave(player);
        }
    }
}
