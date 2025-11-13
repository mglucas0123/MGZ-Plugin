package mglucas0123.config;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mglucas0123.Principal;
import mglucas0123.config.editor.EditorModeManager;

/**
 * ConfigEditorGUI - Menu Principal Redesenhado
 * 
 * FILOSOFIA UX (Jogador experiente de 25 anos):
 * - Informação visual imediata (status verde/vermelho)
 * - Layout em GRID organizado (3x3 por categoria)
 * - Contexto rápido sem abrir submenus
 * - Design clean, moderno e profissional
 * - Enchant glow nos sistemas ativos
 */
public class ConfigEditorGUI {
    
    private Principal plugin;
    
    public ConfigEditorGUI(Principal plugin) {
        this.plugin = plugin;
    }
    
    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§0§l⬛ §8§l▎§6§l⚙ MGZ §8§l⚙ §8§l▎§0§l⬛");
        
        // ===== LAYOUT INFORMATIVO =====
        // Linha 0: Header com informações
        // Linhas 1-4: Grid 3x3 organizado por categoria
        // Linha 5: Footer com ações
        
        // === HEADER (Linha 0) ===
        ItemStack headerBorder = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        ItemStack accentGold = createItem(Material.ORANGE_STAINED_GLASS_PANE, "§6◆");
        
        for (int i = 0; i < 9; i++) {
            if (i == 3 || i == 5) inv.setItem(i, accentGold);
            else inv.setItem(i, headerBorder);
        }
        
        // Info do servidor no header
        inv.setItem(4, createItem(Material.NETHER_STAR, "§6§l⚙ MGZ Configuration",
            "§8§m──────────────────────",
            "§7Painel de controle completo",
            "§7",
            "§8▸ §710 módulos configuráveis",
            "§8▸ §7Alterações salvas automaticamente",
            "§8§m──────────────────────"));
        
        // === GRID 3x3 - CATEGORIA: AUTOMAÇÃO (Verde) ===
        inv.setItem(10, createModuleItem(Material.CLOCK, 
            "§a§l⏱ AutoSave", 
            plugin.getConfig().getBoolean("AutoSave.Enabled"),
            "Salvamento automático do servidor",
            plugin.getConfig().getInt("AutoSave.IntervalSeconds") + "s de intervalo",
            plugin.getConfig().getBoolean("AutoSave.BroadcastMessage") ? "Broadcast ativado" : "Sem broadcast"));
        
        inv.setItem(11, createModuleItem(Material.REDSTONE_BLOCK, 
            "§c§l⚡ AutoRestart", 
            plugin.getConfig().getBoolean("AutoRestart.Enabled"),
            "Reinício automático agendado",
            plugin.getConfig().getStringList("AutoRestart.Times").size() + " horários configurados",
            plugin.getConfig().getBoolean("AutoRestart.EnableCountdown") ? "Contagem regressiva ativa" : "Sem contagem"));
        
        inv.setItem(12, createModuleItem(Material.CHEST, 
            "§6§l💾 Backup de Inventário", 
            plugin.getConfig().getBoolean("InventoryBackup.Enabled"),
            "Proteção automática de itens",
            "Máx: " + plugin.getConfig().getInt("InventoryBackup.MaxBackupsPerPlayer") + " backups/jogador",
            plugin.getConfig().getInt("InventoryBackup.IntervalSeconds") + "s entre backups"));
        
        // === GRID 3x3 - CATEGORIA: GAMEPLAY (Amarelo/Laranja) ===
        inv.setItem(19, createModuleItem(Material.ENCHANTED_BOOK, 
            "§e§l📜 GameRules", 
            true,
            "Regras do Minecraft personalizadas",
            "8 regras booleanas",
            "3 regras numéricas + whitelist"));
        
        inv.setItem(20, createModuleItem(Material.ENDER_PEARL, 
            "§d§l🌀 Random Teleport", 
            true,
            "Teleporte aleatório configurável",
            "Mundos: " + plugin.getConfig().getString("RandomTP.TargetWorld", "world"),
            plugin.getConfig().getInt("RandomTP.DelaySeconds") + "s de delay"));
        
        inv.setItem(21, createModuleItem(Material.BUCKET, 
            "§b§l☔ Controle de Clima", 
            plugin.getConfig().getBoolean("CancelRain.Enabled"),
            "Cancela chuva automaticamente",
            plugin.getConfig().getBoolean("CancelRain.Enabled") ? "Sistema ativo" : "Sistema inativo",
            "Previne chuva/tempestade"));
        
        // === GRID 3x3 - CATEGORIA: COMUNICAÇÃO (Azul) ===
        inv.setItem(28, createModuleItem(Material.WRITABLE_BOOK, 
            "§9§l💬 Chat Control", 
            plugin.getConfig().getBoolean("ChatControl.ShowDeathMessages"),
            "Gerenciamento de mensagens",
            plugin.getConfig().getBoolean("ChatControl.ShowDeathMessages") ? "Death messages: ON" : "Death messages: OFF",
            "Controle total do chat"));
        
        inv.setItem(29, createModuleItem(Material.BIRCH_SIGN, 
            "§a§l👋 Mensagens Join/Quit", 
            true,
            "Entrada e saída customizadas",
            "Join: " + (plugin.getConfig().getString("JoinQuit.JoinMessage", "").isEmpty() ? "Padrão" : "Custom"),
            "Quit: " + (plugin.getConfig().getString("JoinQuit.QuitMessage", "").isEmpty() ? "Padrão" : "Custom")));
        
        // === GRID 3x3 - CATEGORIA: PROTEÇÃO (Roxo) ===
        inv.setItem(30, createModuleItem(Material.OAK_SIGN, 
            "§7§l🪧 Bloqueio de Placas", 
            plugin.getConfig().getBoolean("SignBlock.Enabled"),
            "Anti-exploit de placas",
            plugin.getConfig().getStringList("SignBlock.BlockedIds").size() + " IDs bloqueados",
            plugin.getConfig().getBoolean("SignBlock.Enabled") ? "Proteção ativa" : "Proteção inativa"));
        
        inv.setItem(31, createModuleItem(Material.ARMOR_STAND, 
            "§5§l🗿 Armor Stand Control", 
            plugin.getConfig().getBoolean("ArmorStand.AllowArmorStands"),
            "Controle de armor stands",
            plugin.getConfig().getBoolean("ArmorStand.AllowArmorStands") ? "Permitido" : "Bloqueado",
            "Gerenciamento de entidades"));
        
        // === BORDAS LATERAIS ===
        ItemStack sideBorder = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 9; i < 45; i += 9) inv.setItem(i + 8, sideBorder); // Direita
        
        // === ESPAÇOS VAZIOS (Design clean) ===
        ItemStack filler = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        int[] fillerSlots = {9, 13, 14, 15, 16, 17, 18, 22, 23, 24, 25, 26, 27, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
        for (int slot : fillerSlots) inv.setItem(slot, filler);
        
        // === FOOTER - Informações e Ações ===
        ItemStack footerBorder = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 45; i < 54; i++) inv.setItem(i, headerBorder);
        
        // Informação útil
        inv.setItem(45, createItem(Material.BOOKSHELF, "§e§l📚 Documentação",
            "§8§m──────────────────────",
            "§7Como usar este painel:",
            "§7",
            "§a✓ §7Clique nos ícones para configurar",
            "§a✓ §7Verde = Ativo §8│ §cVermelho = Inativo",
            "§a✓ §7Mudanças salvas automaticamente",
            "§8§m──────────────────────"));
        
        inv.setItem(46, createItem(Material.COMMAND_BLOCK, "§b§l⚡ Status do Servidor",
            "§8§m──────────────────────",
            "§7Informações em tempo real:",
            "§7",
            "§8▸ §7Plugins: §a" + org.bukkit.Bukkit.getPluginManager().getPlugins().length,
            "§8▸ §7Mundos: §a" + org.bukkit.Bukkit.getWorlds().size(),
            "§8▸ §7Online: §a" + org.bukkit.Bukkit.getOnlinePlayers().size() + "§7/§a" + org.bukkit.Bukkit.getMaxPlayers(),
            "§8§m──────────────────────"));
        
        // Ações principais - MODO EDITOR TOGGLE
        boolean editorAtivo = EditorModeManager.isActive(player);
        Material toggleIcon = editorAtivo ? Material.LIME_DYE : Material.GRAY_DYE;
        String toggleStatus = editorAtivo ? "§a§lATIVO" : "§c§lDESATIVADO";
        String toggleAction = editorAtivo ? "§e➜ Clique para DESATIVAR" : "§e➜ Clique para ATIVAR";
        
        inv.setItem(47, createItem(toggleIcon, "§6§l⚙ Modo Editor: " + toggleStatus,
            "§8§m──────────────────────",
            "§7Permite editar GUIs visualmente",
            "§7",
            "§f§lQuando ATIVO:",
            "§8▸ §7Navegue pelos menus normalmente",
            "§8▸ §7Arraste itens para substituir",
            "§8▸ §7Bordas aplicam em todos os slots",
            "§8▸ §7Salva automaticamente",
            "§7",
            editorAtivo ? "§a§l✓ Modo editor está ATIVO" : "§c§l✖ Modo editor está DESATIVADO",
            "§8§m──────────────────────",
            toggleAction));
        
        inv.setItem(48, createItem(Material.EMERALD, "§a§l✔ Recarregar Config",
            "§8§m──────────────────────",
            "§7Recarrega §econfig.yml §7do disco",
            "§7",
            "§c⚠ §7Descarta mudanças não salvas!",
            "§7Use apenas se necessário",
            "§8§m──────────────────────",
            "§e➜ Clique para recarregar"));
        
        inv.setItem(49, createItem(Material.PAPER, "§6§l📋 Sobre o MGZ",
            "§8§m──────────────────────",
            "§7Plugin de configuração avançada",
            "§7",
            "§8▸ §7Versão: §f1.0.0",
            "§8▸ §7Autor: §fmglucas0123",
            "§8▸ §7Build: §fPaper 1.20.1",
            "§8§m──────────────────────"));
        
        inv.setItem(50, createItem(Material.BARRIER, "§c§l✖ Fechar Menu",
            "§8§m──────────────────────",
            "§7Fecha este painel",
            "§7",
            "§a✓ §7Todas as configurações",
            "§a✓ §7foram salvas automaticamente",
            "§8§m──────────────────────",
            "§e➜ Clique para sair"));
        
        // Slots restantes
        int[] emptyFooter = {51, 52, 53};
        for (int slot : emptyFooter) inv.setItem(slot, headerBorder);
        
        player.openInventory(inv);
    }
    
    /**
     * Cria um item de módulo com status visual
     * ENCHANT GLOW = Sistema ativo
     */
    private ItemStack createModuleItem(Material icon, String name, boolean enabled, String description, String... info) {
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        
        // Nome com indicador de status
        String status = enabled ? "§a●" : "§c●";
        meta.setDisplayName(status + " " + name);
        
        // Lore informativa
        java.util.List<String> lore = new java.util.ArrayList<>();
        lore.add("§8§m──────────────────────");
        lore.add("§7" + description);
        lore.add("");
        
        // Status visual destacado
        if (enabled) {
            lore.add("§a§l✓ SISTEMA ATIVO");
        } else {
            lore.add("§c§l✖ SISTEMA INATIVO");
        }
        lore.add("");
        
        // Informações adicionais
        for (String line : info) {
            lore.add("§8▸ §f" + line);
        }
        
        lore.add("");
        lore.add("§8§m──────────────────────");
        lore.add("§e§l➜ CLIQUE PARA CONFIGURAR");
        
        meta.setLore(lore);
        
        // Adicionar enchant glow se ativo
        if (enabled) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
        
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null && lore.length > 0) {
            meta.setLore(java.util.Arrays.asList(lore));
        }
        item.setItemMeta(meta);
        return item;
    }
}
