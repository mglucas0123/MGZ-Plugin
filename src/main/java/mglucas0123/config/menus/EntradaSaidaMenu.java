package mglucas0123.config.menus;

import mglucas0123.Principal;
import mglucas0123.config.ConfigEditorGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EntradaSaidaMenu extends BaseMenu {
    
    private ConfigEditorGUI editorGUI;
    
    public EntradaSaidaMenu(Principal plugin, ConfigEditorGUI editorGUI) {
        super(plugin);
        this.editorGUI = editorGUI;
    }
    
    @Override
    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8§l▬▬▬▬▬ §a§l👋 Join/Quit 👋 §8§l▬▬▬▬▬");
        
        String entrada = plugin.getConfig().getString("EntradaSaida.Entrada");
        String saida = plugin.getConfig().getString("EntradaSaida.Saida");
        
        
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
        
        
        inv.setItem(10, createItem(Material.GREEN_BANNER, "§a§l👋 MENSAGEM DE ENTRADA",
            "§8§m─────────────────────"));
        
        inv.setItem(11, createItem(Material.EMERALD, "§a§l✓ Join - Entrada no Servidor",
            "§8§m───────────────────────",
            "§7Mensagem exibida quando",
            "§7jogador entra no servidor",
            "§7",
            "§fMensagem atual:",
            "§e" + entrada,
            "§7",
            "§f§lVariáveis disponíveis:",
            "§8▸ §f{player} §7- Nome do jogador",
            "§8▸ §f{total} §7- Total de players",
            "§8§m───────────────────────",
            "§c⚠ §7Edite em §econfig.yml",
            "§7Seção: §6EntradaSaida.Entrada"));
        
        
        inv.setItem(19, createItem(Material.RED_BANNER, "§c§l👋 MENSAGEM DE SAÍDA",
            "§8§m─────────────────────"));
        
        inv.setItem(20, createItem(Material.REDSTONE, "§c§l✖ Quit - Saída do Servidor",
            "§8§m───────────────────────",
            "§7Mensagem exibida quando",
            "§7jogador sai do servidor",
            "§7",
            "§fMensagem atual:",
            "§e" + saida,
            "§7",
            "§f§lVariáveis disponíveis:",
            "§8▸ §f{player} §7- Nome do jogador",
            "§8▸ §f{total} §7- Total de players",
            "§8§m───────────────────────",
            "§c⚠ §7Edite em §econfig.yml",
            "§7Seção: §6EntradaSaida.Saida"));
        
        
        inv.setItem(28, createItem(Material.KNOWLEDGE_BOOK, "§b§l📖 INFORMAÇÕES",
            "§8§m─────────────────────"));
        
        inv.setItem(29, createItem(Material.ITEM_FRAME, "§7§lⓘ Como Funciona",
            "§8§m───────────────────────",
            "§7O sistema de Join/Quit",
            "§7personaliza as mensagens",
            "§7de entrada e saída.",
            "§7",
            "§f§lFuncionalidades:",
            "§8▸ §fMensagens personalizadas",
            "§8▸ §fVariáveis dinâmicas",
            "§8▸ §fSuporta cores",
            "§8▸ §fFácil personalização",
            "§8§m───────────────────────",
            "§aEstá funcionando normalmente"));
        
        inv.setItem(30, createItem(Material.WRITABLE_BOOK, "§9§l📝 Status",
            "§8§m───────────────────────",
            "§7Sistema de mensagens Join/Quit",
            "§7",
            "§f§lConfigurações atuais:",
            "§8▸ §fEntrada: §aConfigurada",
            "§8▸ §fSaída: §aConfigurada",
            "§8▸ §fSistema: §aAtivo",
            "§8§m───────────────────────",
            "§7Mensagens ativas no servidor"));
        
        
        inv.setItem(37, createItem(Material.BOOK, "§e§l💡 EXEMPLOS DE USO",
            "§8§m─────────────────────"));
        
        inv.setItem(38, createItem(Material.PAPER, "§f§l📝 Exemplos de Mensagens",
            "§8§m───────────────────────",
            "§f§lExemplos de Entrada:",
            "§8▸ §a§l+ §f{player} §eentrou!",
            "§8▸ §eBem-vindo §f{player}§e!",
            "§8▸ §f{player} §aconectou §7[§f{total}§7]",
            "§7",
            "§f§lExemplos de Saída:",
            "§8▸ §c§l- §f{player} §esaiu!",
            "§8▸ §eAté logo §f{player}§e!",
            "§8▸ §f{player} §cdesconectou §7[§f{total}§7]",
            "§8§m───────────────────────",
            "§7Use cores e variáveis livremente!"));
        
        
        inv.setItem(49, createItem(Material.ARROW, "§7§l« Voltar",
            "§8§m───────────────────────",
            "§7Retornar ao menu principal",
            "§8§m───────────────────────",
            "§e§l➤ Clique para voltar"));
        
        
        for (int i = 1; i < 8; i++) inv.setItem(i, border);
        for (int i = 14; i < 17; i++) inv.setItem(i, empty);
        for (int i = 22; i < 26; i++) inv.setItem(i, empty);
        for (int i = 31; i < 35; i++) inv.setItem(i, empty);
        for (int i = 39; i < 44; i++) inv.setItem(i, empty);
        for (int i = 46; i < 53; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, border);
        }
        
        player.openInventory(inv);
    }
    
    public void handleClick(Player player, ItemStack clicked, InventoryClickEvent event) {
        String displayName = clicked.getItemMeta().getDisplayName();
        
        if (displayName.contains("Voltar")) {
            editorGUI.openMainMenu(player);
        }
    }
}
