package mglucas0123;

import mglucas0123.autosave.AutoRestartManager;
import mglucas0123.autosave.AutoSaveManager;
import mglucas0123.config.ConfigEditorListener;
import mglucas0123.events.*;
import mglucas0123.inventory.InventoryBackupManager;
import mglucas0123.region.RegionManager;
import mglucas0123.region.SelectionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import mglucas0123.commands.*;

public class Principal extends JavaPlugin {

    public static Principal plugin;
    private RegionManager regionManager;
    private SelectionManager selectionManager;
    private InventoryBackupManager inventoryBackupManager;
    private AutoSaveManager autoSaveManager;
    private AutoRestartManager autoRestartManager;

    ConsoleCommandSender cmd = Bukkit.getConsoleSender();
    
    public void onEnable() {
        cmd.sendMessage("§a--------<MGZ>--------");
        cmd.sendMessage("§aPlugin Ativado");
        cmd.sendMessage("§aVersion: §f1.0.0");
        cmd.sendMessage("§a--------<MGZ>--------");
        plugin = this;
        saveDefaultConfig();
        
        regionManager = new RegionManager(getDataFolder());
        selectionManager = new SelectionManager();
        cmd.sendMessage("§aSistema de Regiões carregado: §f" + regionManager.getAllRegions().size() + " regiões");
        
        ServerControl serverControl = new ServerControl(this);
        Bukkit.getPluginManager().registerEvents(serverControl, this);
        serverControl.applyGameRulesToAllWorlds();
        cmd.sendMessage("§aConfigurações globais aplicadas em todos os mundos");
        
        inventoryBackupManager = new InventoryBackupManager(this);
        Bukkit.getPluginManager().registerEvents(new InventoryBackupListener(this, inventoryBackupManager), this);
        Bukkit.getPluginManager().registerEvents(new CrashRecoveryListener(this, inventoryBackupManager), this);
        
        inventoryBackupManager.startMemoryCacheUpdateTask();
        inventoryBackupManager.startDirtySaveTask();
        
        cmd.sendMessage("§aSistema de Backup de Player Data ativado (Cache + Debounce + CrashRecovery)");
        
        autoSaveManager = new AutoSaveManager(this);
        autoSaveManager.start();
        
        autoRestartManager = new AutoRestartManager(this);
        autoRestartManager.start();
        
        getCommand("Anuncio").setExecutor(new Anuncio());
        getCommand("LimparChat").setExecutor(new LimparChat());
        getCommand("Luz").setExecutor(new Luz());
        getCommand("Mgz").setExecutor(new MGZ());
        getCommand("Repair").setExecutor(new Repair());
        getCommand("Rtp").setExecutor(new Rtp(this));
        getCommand("Region").setExecutor(new RegionCommand());
        getCommand("Region").setTabCompleter(new RegionTabCompleter());
        getCommand("MgzReload").setExecutor(new ReloadConfig(this));
        getCommand("PlayerData").setExecutor(new PlayerDataCommand(this, inventoryBackupManager));
        getCommand("PlayerData").setTabCompleter(new PlayerDataTabCompleter());
        getCommand("AutoSave").setExecutor(new AutoSaveCommand(this));
        getCommand("AutoSave").setTabCompleter(new AutoSaveTabCompleter());
        getCommand("AutoRestart").setExecutor(new AutoRestartCommand(this));
        getCommand("AutoRestart").setTabCompleter(new AutoRestartTabCompleter());
        getCommand("MgzConfig").setExecutor(new ConfigEditorCommand(this));
        
        Bukkit.getPluginManager().registerEvents(new CancelarChuva(), this);
        Bukkit.getPluginManager().registerEvents(new EntradaSaida(), this);
        Bukkit.getPluginManager().registerEvents(new MotdExterno(), this);
        Bukkit.getPluginManager().registerEvents(new PlacaBloquear(), this);
        Bukkit.getPluginManager().registerEvents(new RegionProtection(), this);
        Bukkit.getPluginManager().registerEvents(new ConfigEditorListener(this), this);
        
        startBlockedItemCheckTask();
    }
    
    private void startBlockedItemCheckTask() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("mgz.region.bypass")) {
                    continue;
                }
                
                org.bukkit.Location location = player.getLocation();
                mglucas0123.region.Region region = regionManager.getHighestPriorityRegion(location);
                
                if (region != null) {
                    org.bukkit.inventory.PlayerInventory inv = player.getInventory();
                    
                    for (int i = 0; i < 9; i++) {
                        org.bukkit.inventory.ItemStack item = inv.getItem(i);
                        if (item != null && item.getType() != org.bukkit.Material.AIR) {
                            String itemType = item.getType().name();
                            if (region.isItemTotalBlocked(itemType)) {
                                boolean moved = false;
                                for (int j = 9; j < 36; j++) {
                                    org.bukkit.inventory.ItemStack slotItem = inv.getItem(j);
                                    if (slotItem == null || slotItem.getType() == org.bukkit.Material.AIR) {
                                        inv.setItem(j, item);
                                        inv.setItem(i, new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR));
                                        moved = true;
                                        break;
                                    }
                                }
                                
                                if (!moved) {
                                    for (int j = 9; j < 36; j++) {
                                        org.bukkit.inventory.ItemStack invItem = inv.getItem(j);
                                        if (invItem != null && invItem.getType() != org.bukkit.Material.AIR) {
                                            String invItemType = invItem.getType().name();
                                            if (!region.isItemTotalBlocked(invItemType)) {
                                                inv.setItem(j, item);
                                                inv.setItem(i, invItem);
                                                moved = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                
                                if (moved) {
                                    player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
                                        new net.md_5.bungee.api.chat.TextComponent("§4§l⛔ §cEste item não pode ficar na hotbar nesta região! §4§l⛔"));
                                }
                            }
                        }
                    }
                    
                    org.bukkit.inventory.ItemStack offhand = inv.getItemInOffHand();
                    if (offhand != null && offhand.getType() != org.bukkit.Material.AIR) {
                        String itemType = offhand.getType().name();
                        if (region.isItemTotalBlocked(itemType)) {
                            boolean moved = false;
                            for (int j = 9; j < 36; j++) {
                                org.bukkit.inventory.ItemStack slotItem = inv.getItem(j);
                                if (slotItem == null || slotItem.getType() == org.bukkit.Material.AIR) {
                                    inv.setItem(j, offhand);
                                    inv.setItemInOffHand(new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR));
                                    moved = true;
                                    break;
                                }
                            }
                            
                            if (!moved) {
                                for (int j = 9; j < 36; j++) {
                                    org.bukkit.inventory.ItemStack invItem = inv.getItem(j);
                                    if (invItem != null && invItem.getType() != org.bukkit.Material.AIR) {
                                        String invItemType = invItem.getType().name();
                                        if (!region.isItemTotalBlocked(invItemType)) {
                                            inv.setItem(j, offhand);
                                            inv.setItemInOffHand(invItem);
                                            moved = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            
                            if (moved) {
                                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, 
                                    new net.md_5.bungee.api.chat.TextComponent("§4§l⛔ §cEste item não pode ficar na offhand nesta região! §4§l⛔"));
                            }
                        }
                    }
                }
            }
        }, 10L, 10L);
    }

    public void onDisable() {
        if (autoSaveManager != null) {
            autoSaveManager.stop();
        }
        
        if (autoRestartManager != null) {
            autoRestartManager.stop();
        }
        
        if (inventoryBackupManager != null) {
            cmd.sendMessage("§aSalvando inventários pendentes...");
            inventoryBackupManager.saveAllDirty();
            cmd.sendMessage("§aSistema de Backup de Inventário salvo!");
        }
        
        if (regionManager != null) {
            regionManager.saveRegions();
            regionManager.shutdown();
            cmd.sendMessage("§aSistema de Regiões salvo!");
        }
        cmd.sendMessage("§c--------<MGZ>--------");
        cmd.sendMessage("§cPlugin Desativado");
        cmd.sendMessage("§cVersion: §f1.0.0");
        cmd.sendMessage("§c--------<MGZ>--------");
    }
    
    public RegionManager getRegionManager() {
        return regionManager;
    }
    
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
    
    public InventoryBackupManager getInventoryBackupManager() {
        return inventoryBackupManager;
    }
    
    public AutoSaveManager getAutoSaveManager() {
        return autoSaveManager;
    }
    
    public AutoRestartManager getAutoRestartManager() {
        return autoRestartManager;
    }
}
