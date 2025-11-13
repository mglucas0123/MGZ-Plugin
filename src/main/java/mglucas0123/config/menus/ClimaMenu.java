package mglucas0123.config.menus;

import mglucas0123.Principal;
import mglucas0123.config.ConfigEditorGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ClimaMenu extends BaseMenu {
    
    private ConfigEditorGUI editorGUI;
    
    public ClimaMenu(Principal plugin, ConfigEditorGUI editorGUI) {
        super(plugin);
        this.editorGUI = editorGUI;
    }
    
    @Override
    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8§l▬▬▬▬▬ §b§l☔ Controle de Clima ☔ §8§l▬▬▬▬▬");
        
        boolean desativar = plugin.getConfig().getBoolean("Chuva.Desativar");
        
        
        ItemStack border = createItem(Material.BLACK_STAINED_GLASS_PANE, "§8", "");
        ItemStack empty = createItem(Material.GRAY_STAINED_GLASS_PANE, "§7", "");
        
        
        for (int i = 0; i < 9; i++) inv.setItem(i, border);
        for (int i = 45; i < 54; i++) inv.setItem(i, border);
        
        
        inv.setItem(9, border);
        inv.setItem(18, border);
        inv.setItem(27, border);
        inv.setItem(36, border);
        inv.setItem(17, border);
        inv.setItem(26, border);
        inv.setItem(35, border);
        inv.setItem(44, border);
        
        
        inv.setItem(10, createItem(Material.WATER_BUCKET, "§b§l☔ CONTROLE DE CHUVA",
            "§8§m─────────────────────"));
        
        inv.setItem(11, createToggleItem(
            desativar ? Material.GRAY_DYE : Material.LIME_DYE,
            "§b§l💧 Sistema de Chuva",
            !desativar,
            "§8§m───────────────────────",
            desativar ? "§c§l✖ DESATIVADO" : "§a§l✔ ATIVADO",
            "§7",
            desativar ? 
                "§7Chuva está §cCancelada automaticamente" :
                "§7Chuva está §aFuncionando normalmente",
            "§7",
            "§f§lFuncionalidades:",
            "§8▸ §fCancela precipitação",
            "§8▸ §fCancela tempestades",
            "§8▸ §fAplicado globalmente",
            "§8§m───────────────────────",
            "§e§l➤ Clique para alternar"));
        
        
        inv.setItem(19, createItem(Material.KNOWLEDGE_BOOK, "§9§l📖 INFORMAÇÕES",
            "§8§m─────────────────────"));
        
        inv.setItem(20, createItem(Material.ITEM_FRAME, "§7§lⓘ Como Funciona",
            "§8§m───────────────────────",
            "§7Este sistema controla o clima",
            "§7do servidor de forma automática.",
            "§7",
            "§f§lFuncionalidades:",
            "§8▸ §fCancela chuva automática",
            "§8▸ §fCancela raios/tempestades",
            "§8▸ §fAplicado a todos os mundos",
            "§8▸ §fSem eventos climáticos",
            "§8§m───────────────────────",
            "§aEstá funcionando normalmente"));
        
        inv.setItem(21, createItem(Material.WRITABLE_BOOK, "§9§l📝 Status",
            "§8§m───────────────────────",
            "§7Sistema de controle de clima",
            "§7",
            "§f§lConfigurações atuais:",
            "§8▸ §fChuva: " + (desativar ? "§cCancelada" : "§aNormal"),
            "§8▸ §fTempestades: " + (desativar ? "§cCanceladas" : "§aNormais"),
            "§8▸ §fEstado Global: " + (desativar ? "§cDesativado" : "§aAtivado"),
            "§8§m───────────────────────",
            desativar ? "§7Chuva e tempestades estão bloqueadas" : "§7Clima funcionando normalmente"));
        
        
        inv.setItem(49, createItem(Material.ARROW, "§7§l« Voltar",
            "§8§m───────────────────────",
            "§7Retornar ao menu principal",
            "§8§m───────────────────────",
            "§e§l➤ Clique para voltar"));
        
        
        for (int i = 1; i < 8; i++) inv.setItem(i, border);
        for (int i = 14; i < 17; i++) inv.setItem(i, empty);
        for (int i = 23; i < 26; i++) inv.setItem(i, empty);
        for (int i = 28; i < 35; i++) inv.setItem(i, empty);
        for (int i = 37; i < 44; i++) inv.setItem(i, empty);
        for (int i = 46; i < 53; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, border);
        }
        
        player.openInventory(inv);
    }
    
    public void handleClick(Player player, ItemStack clicked, InventoryClickEvent event) {
        String displayName = clicked.getItemMeta().getDisplayName();
        
        if (displayName.contains("Sistema de Chuva")) {
            boolean current = plugin.getConfig().getBoolean("Chuva.Desativar");
            plugin.getConfig().set("Chuva.Desativar", !current);
            plugin.saveConfig();
            player.sendMessage("§b§l☔ §e[Controle de Clima] §7Sistema: " + (!current ? "§c§lCANCELADO" : "§a§lNORMAL"));
            open(player);
        } else if (displayName.contains("Voltar")) {
            editorGUI.openMainMenu(player);
        }
    }
}
