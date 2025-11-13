package mglucas0123.config.menus;

import mglucas0123.Principal;
import mglucas0123.config.ConfigEditorGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class ArmorStandMenu extends BaseMenu {

    private ConfigEditorGUI editorGUI;

    public ArmorStandMenu(Principal plugin, ConfigEditorGUI editorGUI) {
        super(plugin);
        this.editorGUI = editorGUI;
    }

    @Override
    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§0§l⬛ §5§l🗿 Armor Stand Control §0§l⬛");

        boolean allowArmorStands = plugin.getConfig().getBoolean("ArmorStand.AllowArmorStands", true);

        
        int totalArmorStands = 0;
        for (World world : Bukkit.getWorlds()) {
            totalArmorStands += world.getEntitiesByClass(ArmorStand.class).size();
        }

        
        ItemStack headerBorder = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        ItemStack accentPurple = createItem(Material.PURPLE_STAINED_GLASS_PANE, "§5◆");

        for (int i = 0; i < 9; i++) {
            if (i == 3 || i == 5)
                inv.setItem(i, accentPurple);
            else
                inv.setItem(i, headerBorder);
        }

        
        inv.setItem(4, createItem(Material.ARMOR_STAND, "§5§l🗿 Armor Stand Control",
                "§8§m──────────────────────",
                "§7Controle total de armor stands",
                "§7",
                "§8▸ §7Total no servidor: §f" + totalArmorStands,
                "§8▸ §7Status: " + (allowArmorStands ? "§a✓ Permitido" : "§c✖ Bloqueado"),
                "§8§m──────────────────────"));

        
        inv.setItem(10, createModuleItem(
                allowArmorStands ? Material.ARMOR_STAND : Material.BARRIER,
                "§5§l🗿 Sistema Principal",
                allowArmorStands,
                "Permite armor stands no servidor",
                allowArmorStands ? "Jogadores podem usar" : "Uso bloqueado",
                "Total: " + totalArmorStands + " entidades"));

        inv.setItem(11, createModuleItem(
                Material.STICK,
                "§7§l🔧 Permitir Colocação",
                plugin.getConfig().getBoolean("ArmorStand.AllowPlacement", true),
                "Jogadores podem colocar armor stands",
                plugin.getConfig().getBoolean("ArmorStand.AllowPlacement", true) ? "Colocação permitida"
                        : "Colocação bloqueada",
                "Requer permissão"));

        inv.setItem(12, createModuleItem(
                Material.IRON_PICKAXE,
                "§c§l⛏ Permitir Remoção",
                plugin.getConfig().getBoolean("ArmorStand.AllowBreak", true),
                "Jogadores podem quebrar armor stands",
                plugin.getConfig().getBoolean("ArmorStand.AllowBreak", true) ? "Remoção permitida"
                        : "Remoção bloqueada",
                "Proteção contra griefing"));

        
        inv.setItem(19, createModuleItem(
                Material.TNT,
                "§c§l� Proteção: Explosões",
                plugin.getConfig().getBoolean("ArmorStand.ProtectFromExplosions", true),
                "Armor stands resistem a explosões",
                plugin.getConfig().getBoolean("ArmorStand.ProtectFromExplosions", true) ? "Imune a TNT/Creepers"
                        : "Vulnerável a explosões",
                "Anti-griefing"));

        inv.setItem(20, createModuleItem(
                Material.PISTON,
                "§6§l⚙ Proteção: Pistons",
                plugin.getConfig().getBoolean("ArmorStand.ProtectFromPistons", true),
                "Armor stands não movem com pistons",
                plugin.getConfig().getBoolean("ArmorStand.ProtectFromPistons", true) ? "Imóvel a pistons"
                        : "Movível por pistons",
                "Previne deslocamento"));

        inv.setItem(21, createModuleItem(
                Material.LAVA_BUCKET,
                "§c§l🔥 Proteção: Fogo/Lava",
                plugin.getConfig().getBoolean("ArmorStand.ProtectFromFire", true),
                "Armor stands resistem a fogo e lava",
                plugin.getConfig().getBoolean("ArmorStand.ProtectFromFire", true) ? "Imune a fogo"
                        : "Vulnerável a fogo",
                "Proteção contra acidentes"));

        
        inv.setItem(28, createModuleItem(
                Material.DIAMOND_CHESTPLATE,
                "§b§l� Permitir Equipar Itens",
                plugin.getConfig().getBoolean("ArmorStand.AllowEquip", true),
                "Jogadores podem equipar armaduras",
                plugin.getConfig().getBoolean("ArmorStand.AllowEquip", true) ? "Interação permitida"
                        : "Equipamento bloqueado",
                "Click direito para equipar"));

        inv.setItem(29, createModuleItem(
                Material.GOLDEN_SWORD,
                "§e§l⚔ Permitir Trocar Mão",
                plugin.getConfig().getBoolean("ArmorStand.AllowHandSwap", true),
                "Jogadores podem trocar item na mão",
                plugin.getConfig().getBoolean("ArmorStand.AllowHandSwap", true) ? "Troca permitida" : "Troca bloqueada",
                "Customização de pose"));

        inv.setItem(30, createModuleItem(
                Material.NAME_TAG,
                "§f§l📛 Permitir Renomear",
                plugin.getConfig().getBoolean("ArmorStand.AllowRename", true),
                "Jogadores podem renomear com nametag",
                plugin.getConfig().getBoolean("ArmorStand.AllowRename", true) ? "Renomeação permitida"
                        : "Nomes bloqueados",
                "Uso de nametags"));

        
        ItemStack sideBorder = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 9; i < 45; i += 9)
            inv.setItem(i + 8, sideBorder);

        
        ItemStack filler = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        int[] fillerSlots = { 9, 13, 14, 15, 16, 17, 18, 22, 23, 24, 25, 26, 27, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                41, 42, 43, 44 };
        for (int slot : fillerSlots)
            inv.setItem(slot, filler);

        
        for (int i = 45; i < 54; i++)
            inv.setItem(i, headerBorder);

        
        inv.setItem(45, createItem(Material.BOOK, "§e§l� Estatísticas",
                "§8§m──────────────────────",
                "§7Armor Stands no servidor:",
                "§7",
                "§8▸ §fTotal: §a" + totalArmorStands,
                "§8▸ §fSistema: " + (allowArmorStands ? "§aPermitido" : "§cBloqueado"),
                "§8▸ §fProteções: §a" + getActiveProtectionsCount() + "§7/3 ativas",
                "§8§m──────────────────────"));

        
        inv.setItem(46, createItem(Material.KNOWLEDGE_BOOK, "§b§l❓ Ajuda",
                "§8§m──────────────────────",
                "§7Configurações disponíveis:",
                "§7",
                "§a✓ §7Sistema Principal - Liga/desliga tudo",
                "§a✓ §7Proteções - Anti-griefing",
                "§a✓ §7Interações - Controle de uso",
                "§8§m──────────────────────"));

        
        inv.setItem(48, createItem(Material.REDSTONE_BLOCK, "§c§l⚠ Resetar Padrões",
                "§8§m──────────────────────",
                "§7Restaura configuração padrão",
                "§7",
                "§c⚠ §7Isso irá:",
                "§8▸ §7Permitir armor stands",
                "§8▸ §7Ativar todas proteções",
                "§8▸ §7Permitir todas interações",
                "§8§m──────────────────────",
                "§e➜ Clique para resetar"));

        
        inv.setItem(49, createItem(Material.ARROW, "§7§l« Voltar ao Menu",
                "§8§m──────────────────────",
                "§7Retorna ao menu principal",
                "§7",
                "§a✓ §7Todas as configurações",
                "§a✓ §7foram salvas automaticamente",
                "§8§m──────────────────────",
                "§e➜ Clique para voltar"));

        
        inv.setItem(50, createItem(Material.EMERALD, "§a§l✔ Aplicar Mudanças",
                "§8§m──────────────────────",
                "§7Salva e recarrega configurações",
                "§7",
                "§8▸ §7Salva no config.yml",
                "§8▸ §7Aplica imediatamente",
                "§8§m──────────────────────",
                "§e➜ Clique para aplicar"));

        int[] emptyFooter = { 47, 51, 52, 53 };
        for (int slot : emptyFooter)
            inv.setItem(slot, headerBorder);

        player.openInventory(inv);
    }

    
    private int getActiveProtectionsCount() {
        int count = 0;
        if (plugin.getConfig().getBoolean("ArmorStand.ProtectFromExplosions", true))
            count++;
        if (plugin.getConfig().getBoolean("ArmorStand.ProtectFromPistons", true))
            count++;
        if (plugin.getConfig().getBoolean("ArmorStand.ProtectFromFire", true))
            count++;
        return count;
    }

    
    private ItemStack createModuleItem(Material icon, String name, boolean enabled, String description,
            String... info) {
        ItemStack item = new ItemStack(icon);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();

        String status = enabled ? "§a●" : "§c●";
        meta.setDisplayName(status + " " + name);

        java.util.List<String> lore = new java.util.ArrayList<>();
        lore.add("§8§m──────────────────────");
        lore.add("§7" + description);
        lore.add("");

        if (enabled) {
            lore.add("§a§l✓ ATIVO");
        } else {
            lore.add("§c§l✖ INATIVO");
        }
        lore.add("");

        for (String line : info) {
            lore.add("§8▸ §f" + line);
        }

        lore.add("");
        lore.add("§8§m──────────────────────");
        lore.add("§e§l➜ CLIQUE PARA ALTERNAR");

        meta.setLore(lore);

        if (enabled) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
        return item;
    }

    public void handleClick(Player player, ItemStack clicked, InventoryClickEvent event) {
        String displayName = clicked.getItemMeta().getDisplayName();

        
        if (displayName.contains("Sistema Principal")) {
            toggleConfig("ArmorStand.AllowArmorStands", player, "Sistema Principal");
            open(player);
        }
        
        else if (displayName.contains("Permitir Colocação")) {
            toggleConfig("ArmorStand.AllowPlacement", player, "Colocação de Armor Stands");
            open(player);
        } else if (displayName.contains("Permitir Remoção")) {
            toggleConfig("ArmorStand.AllowBreak", player, "Remoção de Armor Stands");
            open(player);
        }
        
        else if (displayName.contains("Proteção: Explosões")) {
            toggleConfig("ArmorStand.ProtectFromExplosions", player, "Proteção contra Explosões");
            open(player);
        } else if (displayName.contains("Proteção: Pistons")) {
            toggleConfig("ArmorStand.ProtectFromPistons", player, "Proteção contra Pistons");
            open(player);
        } else if (displayName.contains("Proteção: Fogo/Lava")) {
            toggleConfig("ArmorStand.ProtectFromFire", player, "Proteção contra Fogo");
            open(player);
        }
        
        else if (displayName.contains("Permitir Equipar Itens")) {
            toggleConfig("ArmorStand.AllowEquip", player, "Equipar Itens");
            open(player);
        } else if (displayName.contains("Permitir Trocar Mão")) {
            toggleConfig("ArmorStand.AllowHandSwap", player, "Troca de Mão");
            open(player);
        } else if (displayName.contains("Permitir Renomear")) {
            toggleConfig("ArmorStand.AllowRename", player, "Renomeação");
            open(player);
        }
        
        else if (displayName.contains("Resetar Padrões")) {
            resetToDefaults(player);
            open(player);
        } else if (displayName.contains("Aplicar Mudanças")) {
            plugin.saveConfig();
            player.sendMessage("§a§l✔ §5[Armor Stand] §fConfigurações aplicadas com sucesso!");
            open(player);
        } else if (displayName.contains("Voltar")) {
            editorGUI.openMainMenu(player);
        }
    }

    
    private void toggleConfig(String path, Player player, String featureName) {
        boolean current = plugin.getConfig().getBoolean(path, true);
        plugin.getConfig().set(path, !current);
        plugin.saveConfig();

        String status = !current ? "§a§lATIVADO" : "§c§lDESATIVADO";
        player.sendMessage("§5§l🗿 §e[Armor Stand] §f" + featureName + ": " + status);
    }

    
    private void resetToDefaults(Player player) {
        plugin.getConfig().set("ArmorStand.AllowArmorStands", true);
        plugin.getConfig().set("ArmorStand.AllowPlacement", true);
        plugin.getConfig().set("ArmorStand.AllowBreak", true);
        plugin.getConfig().set("ArmorStand.ProtectFromExplosions", true);
        plugin.getConfig().set("ArmorStand.ProtectFromPistons", true);
        plugin.getConfig().set("ArmorStand.ProtectFromFire", true);
        plugin.getConfig().set("ArmorStand.AllowEquip", true);
        plugin.getConfig().set("ArmorStand.AllowHandSwap", true);
        plugin.getConfig().set("ArmorStand.AllowRename", true);
        plugin.saveConfig();

        player.sendMessage("§5§l🗿 §e[Armor Stand] §fConfigurações resetadas para padrão!");
        player.sendMessage("§7▸ Todas as opções foram §aativadas");
    }
}
