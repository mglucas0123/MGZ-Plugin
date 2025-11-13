package mglucas0123.config;

import mglucas0123.Principal;
import mglucas0123.config.menus.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;


public class ConfigEditorListener implements Listener {
    
    private Principal plugin;
    private ConfigEditorGUI editorGUI;
    private ChatInputManager chatInputManager;
    
    
    private AutoSaveMenu autoSaveMenu;
    private AutoRestartMenu autoRestartMenu;
    private InventoryBackupMenu inventoryBackupMenu;
    private RandomTPMenu randomTPMenu;
    private ClimaMenu climaMenu;
    private ChatControlMenu chatControlMenu;
    private PlacasMenu placasMenu;
    private ArmorStandMenu armorStandMenu;
    private EntradaSaidaMenu entradaSaidaMenu;
    private GameRulesMenu gameRulesMenu;
    
    public ConfigEditorListener(Principal plugin) {
        this.plugin = plugin;
        this.editorGUI = new ConfigEditorGUI(plugin);
        this.chatInputManager = new ChatInputManager(plugin, editorGUI);
        
        this.autoSaveMenu = new AutoSaveMenu(plugin, editorGUI);
        this.autoRestartMenu = new AutoRestartMenu(plugin, editorGUI, chatInputManager);
        this.inventoryBackupMenu = new InventoryBackupMenu(plugin, editorGUI);
        this.randomTPMenu = new RandomTPMenu(plugin, editorGUI);
        this.climaMenu = new ClimaMenu(plugin, editorGUI);
        this.chatControlMenu = new ChatControlMenu(plugin, editorGUI);
        this.placasMenu = new PlacasMenu(plugin, editorGUI);
        this.armorStandMenu = new ArmorStandMenu(plugin, editorGUI);
        this.entradaSaidaMenu = new EntradaSaidaMenu(plugin, editorGUI);
        this.gameRulesMenu = new GameRulesMenu(plugin, editorGUI);
        
        org.bukkit.Bukkit.getPluginManager().registerEvents(chatInputManager, plugin);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (!title.contains("MGZ") && !title.contains("Config >") && 
            !title.contains("AutoSave") && !title.contains("AutoRestart") && 
            !title.contains("GameRules") && !title.contains("ChatControl") &&
            !title.contains("Chat Control") && !title.contains("Random TP") && 
            !title.contains("Inventory Backup") && !title.contains("Chuva") && 
            !title.contains("Clima") && !title.contains("Mundos") && 
            !title.contains("Placas") && !title.contains("ArmorStand") && 
            !title.contains("Armor Stand") && !title.contains("Join/Quit") && 
            !title.contains("Entrada/Saída")) return;
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (clicked.getType() == Material.GRAY_STAINED_GLASS_PANE || 
            clicked.getType() == Material.BLACK_STAINED_GLASS_PANE ||
            clicked.getType() == Material.YELLOW_STAINED_GLASS_PANE ||
            clicked.getType() == Material.ORANGE_STAINED_GLASS_PANE) return;
        
        
        if (title.contains("MGZ")) {
            handleMainMenu(player, clicked);
        } else if (title.contains("AutoSave")) {
            autoSaveMenu.handleClick(player, clicked, event);
        } else if (title.contains("AutoRestart")) {
            autoRestartMenu.handleClick(player, clicked, event);
        } else if (title.contains("GameRules")) {
            gameRulesMenu.handleClick(player, clicked, event);
        } else if (title.contains("Mundos")) {
            gameRulesMenu.handleWorldListClick(player, clicked, title);
        } else if (title.contains("ChatControl") || title.contains("Chat Control")) {
            chatControlMenu.handleClick(player, clicked, event);
        } else if (title.contains("Random TP")) {
            randomTPMenu.handleClick(player, clicked, event);
        } else if (title.contains("Inventory Backup")) {
            inventoryBackupMenu.handleClick(player, clicked, event);
        } else if (title.contains("Clima") || title.contains("Chuva")) {
            climaMenu.handleClick(player, clicked, event);
        } else if (title.contains("Placas")) {
            placasMenu.handleClick(player, clicked, event);
        } else if (title.contains("ArmorStand") || title.contains("Armor Stand")) {
            armorStandMenu.handleClick(player, clicked, event);
        } else if (title.contains("Entrada/Saída") || title.contains("Join/Quit")) {
            entradaSaidaMenu.handleClick(player, clicked, event);
        }
    }
    
    private void handleMainMenu(Player player, ItemStack clicked) {
        String displayName = clicked.getItemMeta().getDisplayName();
        
        if (displayName.contains("AutoSave")) {
            autoSaveMenu.open(player);
        } else if (displayName.contains("AutoRestart")) {
            autoRestartMenu.open(player);
        } else if (displayName.contains("GameRules")) {
            gameRulesMenu.open(player);
        } else if (displayName.contains("Chat Control")) {
            chatControlMenu.open(player);
        } else if (displayName.contains("Random Teleport") || displayName.contains("Random TP")) {
            randomTPMenu.open(player);
        } else if (displayName.contains("Backup")) {
            inventoryBackupMenu.open(player);
        } else if (displayName.contains("Clima")) {
            climaMenu.open(player);
        } else if (displayName.contains("Placas")) {
            placasMenu.open(player);
        } else if (displayName.contains("Armor Stand")) {
            armorStandMenu.open(player);
        } else if (displayName.contains("Join/Quit")) {
            entradaSaidaMenu.open(player);
        } else if (displayName.contains("Recarregar Config")) {
            plugin.reloadConfig();
            player.sendMessage("§a§l✔ §e[MGZ] §fConfiguração recarregada com sucesso!");
            player.closeInventory();
        } else if (displayName.contains("Fechar")) {
            player.closeInventory();
            player.sendMessage("§8§l◆ §6§lMGZ §8§l◆ §fEditor fechado!");
        }
    }
    
}
