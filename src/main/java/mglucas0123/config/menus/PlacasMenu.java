package mglucas0123.config.menus;

import mglucas0123.Principal;
import mglucas0123.config.ConfigEditorGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlacasMenu extends BaseMenu {
    
    private ConfigEditorGUI editorGUI;
    
    public PlacasMenu(Principal plugin, ConfigEditorGUI editorGUI) {
        super(plugin);
        this.editorGUI = editorGUI;
    }
    
    @Override
    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8§l▬▬▬▬▬ §6§l📋 Proteção de Placas 📋 §8§l▬▬▬▬▬");
        
        boolean habilitado = plugin.getConfig().getBoolean("Placas.Habilitado");
        List<String> ids = plugin.getConfig().getStringList("Placas.Ids");
        
        // ===== BORDAS =====
        ItemStack border = createItem(Material.BLACK_STAINED_GLASS_PANE, "§8", "");
        ItemStack empty = createItem(Material.GRAY_STAINED_GLASS_PANE, "§7", "");
        
        // Bordas superiores e inferiores
        for (int i = 0; i < 9; i++) inv.setItem(i, border);
        for (int i = 45; i < 54; i++) inv.setItem(i, border);
        
        // Bordas laterais
        inv.setItem(9, border);
        inv.setItem(18, border);
        inv.setItem(27, border);
        inv.setItem(36, border);
        inv.setItem(17, border);
        inv.setItem(26, border);
        inv.setItem(35, border);
        inv.setItem(44, border);
        
        // ===== SEÇÃO 1: SISTEMA =====
        inv.setItem(10, createItem(Material.OAK_SIGN, "§6§l🛡 SISTEMA DE PROTEÇÃO",
            "§8§m─────────────────────"));
        
        inv.setItem(11, createToggleItem(
            habilitado ? Material.OAK_SIGN : Material.BARRIER,
            "§6§l📋 Sistema de Bloqueio",
            habilitado,
            "§8§m───────────────────────",
            habilitado ? "§a§l✔ ATIVADO" : "§c§l✖ DESATIVADO",
            "§7",
            "§7Bloqueia a colocação de placas",
            "§7com textos específicos",
            "§7",
            "§f§lFunção:",
            "§8▸ §fImpede textos proibidos",
            "§8▸ §fProtege contra propaganda",
            "§8▸ §fMantém servidor organizado",
            "§8§m───────────────────────",
            "§e§l➤ Clique para alternar"));
        
        // ===== SEÇÃO 2: IDS BLOQUEADOS =====
        inv.setItem(19, createItem(Material.WRITABLE_BOOK, "§c§l🚫 IDS BLOQUEADOS",
            "§8§m─────────────────────"));
        
        inv.setItem(20, createItem(Material.BOOK, "§e§l📜 Lista de IDs Bloqueados",
            "§8§m───────────────────────",
            "§7Total de IDs bloqueados: §e" + ids.size(),
            "§7",
            ids.size() > 0 ? 
                "§7IDs: §f" + String.join("§7, §f", ids) :
                "§7Nenhum ID bloqueado",
            "§7",
            "§c⚠ §7Para editar a lista:",
            "§8▸ §fAbra §econfig.yml",
            "§8▸ §fSeção: §6Placas.Ids",
            "§8▸ §fAdicione ou remova IDs",
            "§8§m───────────────────────",
            "§7Edição manual necessária"));
        
        // ===== SEÇÃO 3: INFORMAÇÕES =====
        inv.setItem(28, createItem(Material.KNOWLEDGE_BOOK, "§b§l📖 INFORMAÇÕES",
            "§8§m─────────────────────"));
        
        inv.setItem(29, createItem(Material.ITEM_FRAME, "§7§lⓘ Como Funciona",
            "§8§m───────────────────────",
            "§7O sistema de proteção impede",
            "§7que placas com textos específicos",
            "§7sejam colocadas no servidor.",
            "§7",
            "§f§lFuncionalidades:",
            "§8▸ §fBloqueio por ID/texto",
            "§8▸ §fLista personalizável",
            "§8▸ §fProteção automática",
            "§8▸ §fPrevenção de spam",
            "§8§m───────────────────────",
            "§aEstá funcionando normalmente"));
        
        inv.setItem(30, createItem(Material.WRITABLE_BOOK, "§9§l📝 Status",
            "§8§m───────────────────────",
            "§7Sistema de proteção de placas",
            "§7",
            "§f§lConfigurações atuais:",
            "§8▸ §fSistema: " + (habilitado ? "§aAtivado" : "§cDesativado"),
            "§8▸ §fIDs Bloqueados: §e" + ids.size(),
            "§8▸ §fProteção: " + (habilitado ? "§aAtiva" : "§cInativa"),
            "§8§m───────────────────────",
            habilitado ? "§7Proteção ativa no servidor" : "§7Sistema desativado"));
        
        // ===== BOTÃO VOLTAR =====
        inv.setItem(49, createItem(Material.ARROW, "§7§l« Voltar",
            "§8§m───────────────────────",
            "§7Retornar ao menu principal",
            "§8§m───────────────────────",
            "§e§l➤ Clique para voltar"));
        
        // Preencher espaços vazios
        for (int i = 1; i < 8; i++) inv.setItem(i, border);
        for (int i = 14; i < 17; i++) inv.setItem(i, empty);
        for (int i = 22; i < 26; i++) inv.setItem(i, empty);
        for (int i = 31; i < 35; i++) inv.setItem(i, empty);
        for (int i = 37; i < 44; i++) inv.setItem(i, empty);
        for (int i = 46; i < 53; i++) {
            if (inv.getItem(i) == null) inv.setItem(i, border);
        }
        
        player.openInventory(inv);
    }
    
    public void handleClick(Player player, ItemStack clicked, InventoryClickEvent event) {
        String displayName = clicked.getItemMeta().getDisplayName();
        
        if (displayName.contains("Sistema de Bloqueio")) {
            boolean current = plugin.getConfig().getBoolean("Placas.Habilitado");
            plugin.getConfig().set("Placas.Habilitado", !current);
            plugin.saveConfig();
            player.sendMessage("§6§l📋 §e[Placas] §7Sistema: " + (!current ? "§a§lATIVADO" : "§c§lDESATIVADO"));
            open(player);
        } else if (displayName.contains("Voltar")) {
            editorGUI.openMainMenu(player);
        }
    }
}
