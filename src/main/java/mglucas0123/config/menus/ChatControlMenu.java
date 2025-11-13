package mglucas0123.config.menus;

import mglucas0123.Principal;
import mglucas0123.config.ConfigEditorGUI;
import mglucas0123.config.GUITemplate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChatControlMenu extends BaseMenu {
    
    private ConfigEditorGUI editorGUI;
    
    public ChatControlMenu(Principal plugin, ConfigEditorGUI editorGUI) {
        super(plugin);
        this.editorGUI = editorGUI;
    }
    
    @Override
    public void open(Player player) {
        GUITemplate template = loadTemplate("ChatControl", 54);
        Inventory inv = Bukkit.createInventory(null, 54, "§8§l▬▬▬▬▬ §b§l💬 Chat Control 💬 §8§l▬▬▬▬▬");
        
        boolean showDeath = plugin.getConfig().getBoolean("ChatControl.MostrarMorte");
        
        
        ItemStack headerBorder = createItem(template.getMaterial("header_border"), "§8", "");
        ItemStack footerBorder = createItem(template.getMaterial("footer_border"), "§8", "");
        ItemStack sideBorder = createItem(template.getMaterial("side_border"), "§8", "");
        ItemStack empty = createItem(template.getMaterial("filler"), "§7", "");
        
        
        for (int i = 0; i < 9; i++) inv.setItem(i, headerBorder);
        for (int i = 45; i < 54; i++) inv.setItem(i, footerBorder);
        
        
        inv.setItem(9, sideBorder);
        inv.setItem(18, sideBorder);
        inv.setItem(27, sideBorder);
        inv.setItem(36, sideBorder);
        inv.setItem(17, sideBorder);
        inv.setItem(26, sideBorder);
        inv.setItem(35, sideBorder);
        inv.setItem(44, sideBorder);
        
        
        inv.setItem(10, createItem(Material.WRITABLE_BOOK, "§b§l📝 CONTROLE DE MENSAGENS",
            "§8§m─────────────────────"));
        
        inv.setItem(11, createToggleItem(
            showDeath ? Material.SKELETON_SKULL : Material.BONE,
            "§c§l💀 Mensagens de Morte",
            showDeath,
            "§8§m───────────────────────",
            showDeath ? "§a§l✔ VISÍVEIS" : "§c§l✖ OCULTAS",
            "§7",
            "§7Controla a exibição de",
            "§7mensagens de morte no chat",
            "§7",
            "§f§lExemplos:",
            "§8▸ §f\"Player morreu para Zombie\"",
            "§8▸ §f\"Player caiu de muito alto\"",
            "§8▸ §f\"Player foi explodido por Creeper\"",
            "§8§m───────────────────────",
            "§e§l➤ Clique para alternar"));
        
        
        inv.setItem(19, createItem(template.getMaterial("info_button"), "§9§l📖 INFORMAÇÕES",
            "§8§m─────────────────────"));
        
        inv.setItem(20, createItem(Material.ITEM_FRAME, "§7§lⓘ Como Funciona",
            "§8§m───────────────────────",
            "§7O Chat Control gerencia",
            "§7mensagens exibidas no chat",
            "§7do servidor.",
            "§7",
            "§f§lFuncionalidades:",
            "§8▸ §fControle de mensagens de morte",
            "§8▸ §fRedução de spam no chat",
            "§8▸ §fMelhor experiência visual",
            "§8§m───────────────────────",
            "§aEstá funcionando normalmente"));
        
        inv.setItem(21, createItem(Material.WRITABLE_BOOK, "§9§l📝 Status",
            "§8§m───────────────────────",
            "§7Sistema de controle de chat",
            "§7",
            "§f§lConfigurações atuais:",
            "§8▸ §fMensagens de Morte: " + (showDeath ? "§aVisíveis" : "§cOcultas"),
            "§8▸ §fSistema: §aAtivo",
            "§8§m───────────────────────",
            "§7Configurações aplicadas com sucesso"));
        
        
        inv.setItem(49, createItem(template.getMaterial("back_button"), "§7§l« Voltar",
            "§8§m───────────────────────",
            "§7Retornar ao menu principal",
            "§8§m───────────────────────",
            "§e§l➤ Clique para voltar"));
        
        
        for (int i = 1; i < 8; i++) inv.setItem(i, headerBorder);
        for (int i = 14; i < 17; i++) inv.setItem(i, empty);
        for (int i = 22; i < 26; i++) inv.setItem(i, empty);
        for (int i = 28; i < 35; i++) inv.setItem(i, empty);
        for (int i = 37; i < 44; i++) inv.setItem(i, empty);
        for (int i = 46; i < 53; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, footerBorder);
        }
        
        player.openInventory(inv);
    }
    
    public void handleClick(Player player, ItemStack clicked, InventoryClickEvent event) {
        String displayName = clicked.getItemMeta().getDisplayName();
        
        if (displayName.contains("Mensagens de Morte")) {
            boolean current = plugin.getConfig().getBoolean("ChatControl.MostrarMorte");
            plugin.getConfig().set("ChatControl.MostrarMorte", !current);
            plugin.saveConfig();
            player.sendMessage("§b§l💬 §e[Chat Control] §7Mensagens de Morte: " + (!current ? "§a§lVISÍVEIS" : "§c§lOCULTAS"));
            open(player);
        } else if (displayName.contains("Voltar")) {
            editorGUI.openMainMenu(player);
        }
    }
}
