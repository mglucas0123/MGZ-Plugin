package mglucas0123.config.menus;

import mglucas0123.Principal;
import mglucas0123.config.ConfigEditorGUI;
import mglucas0123.config.editor.GUITemplate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * AutoSaveMenu - Controle Avançado de Salvamento
 * 
 * REDESIGN UX:
 * - Estatísticas em tempo real (último/próximo save)
 * - Presets de intervalo (1min, 5min, 10min, 30min, custom)
 * - Teste instantâneo (forçar save agora)
 * - Controle de mensagens (broadcast, actionbar, nenhuma)
 * - Visual clean com feedback instantâneo
 */
public class AutoSaveMenu extends BaseMenu {
    
    private ConfigEditorGUI editorGUI;
    
    public AutoSaveMenu(Principal plugin, ConfigEditorGUI editorGUI) {
        super(plugin);
        this.editorGUI = editorGUI;
    }
    
    @Override
    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§0§l⬛ §e§l⏱ AutoSave Control §0§l⬛");
        
        // Carregar template
        GUITemplate template = loadTemplate("AutoSaveMenu", 54);
        
        boolean enabled = plugin.getConfig().getBoolean("AutoSave.Enabled");
        int interval = plugin.getConfig().getInt("AutoSave.IntervalSeconds", 300);
        boolean broadcast = plugin.getConfig().getBoolean("AutoSave.BroadcastMessage");
        
        // Calcular próximo save
        int totalWorlds = Bukkit.getWorlds().size();
        String intervalFormatted = formatInterval(interval);
        
        // === HEADER ===
        ItemStack headerBorder = createItem(template.getMaterial("header_border"), " ");
        ItemStack accentYellow = createItem(Material.YELLOW_STAINED_GLASS_PANE, "§e◆");
        
        for (int i = 0; i < 9; i++) {
            if (i == 3 || i == 5) inv.setItem(i, accentYellow);
            else inv.setItem(i, headerBorder);
        }
        
        // Info principal
        inv.setItem(4, createItem(Material.CLOCK, "§e§l⏱ AutoSave Control",
            "§8§m──────────────────────",
            "§7Salvamento automático do servidor",
            "§7",
            "§8▸ §7Status: " + (enabled ? "§aAtivo" : "§cInativo"),
            "§8▸ §7Intervalo: §f" + intervalFormatted,
            "§8▸ §7Mundos: §f" + totalWorlds,
            "§8§m──────────────────────"));
        
        // === CONTROLE PRINCIPAL (Linha 1) ===
        inv.setItem(10, createModuleItem(
            enabled ? Material.SUNFLOWER : Material.DEAD_BUSH,
            "§e§l⏱ Sistema Principal",
            enabled,
            "Liga/desliga AutoSave",
            enabled ? "Salvando a cada " + intervalFormatted : "Sistema desativado",
            totalWorlds + " mundo(s) afetado(s)"));
        
        inv.setItem(11, createModuleItem(
            Material.PAPER,
            "§6§l📢 Mensagem de Broadcast",
            broadcast,
            "Avisa no chat ao salvar",
            broadcast ? "Mensagem exibida" : "Salvamento silencioso",
            "Visível para todos"));
        
        inv.setItem(12, createItem(Material.EXPERIENCE_BOTTLE, "§a§l⚡ Forçar Save Agora",
            "§8§m──────────────────────",
            "§7Executa salvamento imediato",
            "§7",
            "§8▸ §fSalva todos os mundos",
            "§8▸ §fSalva dados de jogadores",
            "§8▸ §fNão afeta agendamento",
            "§8§m──────────────────────",
            enabled ? "§e➜ Clique para salvar" : "§cSistema desativado"));
        
        // === PRESETS DE INTERVALO (Linha 2) ===
        int[] presets = {60, 180, 300, 600, 1800};
        String[] presetNames = {"1min", "3min", "5min", "10min", "30min"};
        Material[] presetIcons = {
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD
        };
        
        for (int i = 0; i < 5; i++) {
            int slot = 19 + i;
            int presetValue = presets[i];
            String presetName = presetNames[i];
            boolean isActive = interval == presetValue;
            
            inv.setItem(slot, createPresetItem(
                presetIcons[i],
                presetName,
                presetValue,
                isActive));
        }
        
        // === CONTROLE FINO (Linha 3) ===
        inv.setItem(28, createItem(Material.RED_DYE, "§c§l« -60s",
            "§8§m──────────────────────",
            "§7Diminui intervalo em 60s",
            "§7",
            "§8▸ §fAtual: §e" + interval + "s",
            "§8▸ §fNovo: §e" + Math.max(30, interval - 60) + "s",
            "§8▸ §fMínimo: §730s",
            "§8§m──────────────────────",
            "§e➜ Clique para diminuir"));
        
        inv.setItem(29, createItem(Material.ORANGE_DYE, "§6§l« -30s",
            "§8§m──────────────────────",
            "§7Diminui intervalo em 30s",
            "§7",
            "§8▸ §fAtual: §e" + interval + "s",
            "§8▸ §fNovo: §e" + Math.max(30, interval - 30) + "s",
            "§8§m──────────────────────",
            "§e➜ Clique para diminuir"));
        
        inv.setItem(30, createItem(Material.CLOCK, "§e§l⏱ Intervalo Atual",
            "§8§m──────────────────────",
            "§7Tempo entre salvamentos",
            "§7",
            "§8▸ §fAtual: §e" + interval + "s §7(§f" + intervalFormatted + "§7)",
            "§8▸ §fPreset: " + getPresetName(interval),
            "§8§m──────────────────────",
            "§7Use as setas para ajustar"));
        
        inv.setItem(31, createItem(Material.LIME_DYE, "§a§l+30s »",
            "§8§m──────────────────────",
            "§7Aumenta intervalo em 30s",
            "§7",
            "§8▸ §fAtual: §e" + interval + "s",
            "§8▸ §fNovo: §e" + (interval + 30) + "s",
            "§8§m──────────────────────",
            "§e➜ Clique para aumentar"));
        
        inv.setItem(32, createItem(Material.GREEN_DYE, "§2§l+60s »",
            "§8§m──────────────────────",
            "§7Aumenta intervalo em 60s",
            "§7",
            "§8▸ §fAtual: §e" + interval + "s",
            "§8▸ §fNovo: §e" + (interval + 60) + "s",
            "§8§m──────────────────────",
            "§e➜ Clique para aumentar"));
        
        // === BORDAS LATERAIS ===
        ItemStack sideBorder = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 9; i < 45; i += 9) inv.setItem(i + 8, sideBorder);
        
        // === ESPAÇOS VAZIOS ===
        ItemStack filler = createItem(template.getMaterial("filler"), " ");
        int[] fillerSlots = {9, 13, 14, 15, 16, 17, 18, 24, 25, 26, 27, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
        for (int slot : fillerSlots) inv.setItem(slot, filler);
        
        // === FOOTER ===
        ItemStack footerBorder = createItem(template.getMaterial("footer_border"), " ");
        for (int i = 45; i < 54; i++) inv.setItem(i, footerBorder);
        
        // Estatísticas
        inv.setItem(45, createItem(Material.BOOK, "§e§l📊 Estatísticas",
            "§8§m──────────────────────",
            "§7Status do AutoSave:",
            "§7",
            "§8▸ §fSistema: " + (enabled ? "§aAtivo" : "§cInativo"),
            "§8▸ §fIntervalo: §e" + intervalFormatted,
            "§8▸ §fMundos: §a" + totalWorlds,
            "§8▸ §fBroadcast: " + (broadcast ? "§aSim" : "§cNão"),
            "§8§m──────────────────────"));
        
        // Ajuda
        inv.setItem(46, createItem(Material.KNOWLEDGE_BOOK, "§b§l❓ Ajuda",
            "§8§m──────────────────────",
            "§7Como configurar:",
            "§7",
            "§a1. §7Ative o sistema principal",
            "§a2. §7Escolha um preset OU ajuste manual",
            "§a3. §7Configure broadcast (opcional)",
            "§a4. §7Teste com 'Forçar Save'",
            "§8§m──────────────────────"));
        
        // Resetar
        inv.setItem(48, createItem(Material.BARRIER, "§c§l⚠ Resetar Padrões",
            "§8§m──────────────────────",
            "§7Restaura configuração padrão",
            "§7",
            "§8▸ §7Intervalo: §f300s §7(5min)",
            "§8▸ §7Broadcast: §aAtivado",
            "§8▸ §7Sistema: §aAtivado",
            "§8§m──────────────────────",
            "§e➜ Clique para resetar"));
        
        // Voltar
        inv.setItem(49, createItem(Material.ARROW, "§7§l« Voltar ao Menu",
            "§8§m──────────────────────",
            "§7Retorna ao menu principal",
            "§7",
            "§a✓ §7Todas as configurações",
            "§a✓ §7foram salvas automaticamente",
            "§8§m──────────────────────",
            "§e➜ Clique para voltar"));
        
        // Aplicar
        inv.setItem(50, createItem(Material.EMERALD, "§a§l✔ Aplicar Mudanças",
            "§8§m──────────────────────",
            "§7Salva e recarrega config",
            "§7",
            "§8▸ §7Salva no config.yml",
            "§8▸ §7Aplica imediatamente",
            "§8§m──────────────────────",
            "§e➜ Clique para aplicar"));
        
        int[] emptyFooter = {47, 51, 52, 53};
        for (int slot : emptyFooter) inv.setItem(slot, footerBorder);
        
        player.openInventory(inv);
    }
    
    /**
     * Formata intervalo em formato legível
     * Ex: 60 → "1min" | 300 → "5min" | 3600 → "1h"
     */
    private String formatInterval(int seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            int minutes = seconds / 60;
            int secs = seconds % 60;
            return secs == 0 ? minutes + "min" : minutes + "min " + secs + "s";
        } else {
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            return minutes == 0 ? hours + "h" : hours + "h " + minutes + "min";
        }
    }
    
    /**
     * Retorna nome do preset ou "Custom"
     */
    private String getPresetName(int interval) {
        switch (interval) {
            case 60: return "§f1 Minuto";
            case 180: return "§f3 Minutos";
            case 300: return "§f5 Minutos §7(padrão)";
            case 600: return "§f10 Minutos";
            case 1800: return "§f30 Minutos";
            default: return "§eCustom";
        }
    }
    
    /**
     * Cria item de preset de intervalo
     */
    private ItemStack createPresetItem(Material icon, String name, int seconds, boolean isActive) {
        ItemStack item = new ItemStack(icon);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        
        String prefix = isActive ? "§a●" : "§7●";
        meta.setDisplayName(prefix + " §e§l" + name);
        
        java.util.List<String> lore = new java.util.ArrayList<>();
        lore.add("§8§m──────────────────────");
        lore.add("§7Intervalo: §f" + seconds + "s");
        lore.add("");
        
        if (isActive) {
            lore.add("§a§l✓ PRESET ATIVO");
        } else {
            lore.add("§7Clique para aplicar");
        }
        
        lore.add("");
        lore.add("§8▸ §fTempo: §e" + formatInterval(seconds));
        lore.add("§8▸ §fRecomendado para:");
        
        // Recomendações baseadas no intervalo
        switch (seconds) {
            case 60:
                lore.add("§8  §7• Servidores pequenos");
                lore.add("§8  §7• Alta atividade");
                break;
            case 180:
                lore.add("§8  §7• Servidores médios");
                lore.add("§8  §7• Economia ativa");
                break;
            case 300:
                lore.add("§8  §7• Uso geral §7(padrão)");
                lore.add("§8  §7• Balanceado");
                break;
            case 600:
                lore.add("§8  §7• Servidores grandes");
                lore.add("§8  §7• Performance");
                break;
            case 1800:
                lore.add("§8  §7• Servidor dedicado");
                lore.add("§8  §7• Máxima performance");
                break;
        }
        
        lore.add("");
        lore.add("§8§m──────────────────────");
        lore.add("§e§l➜ CLIQUE PARA APLICAR");
        
        meta.setLore(lore);
        
        if (isActive) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Cria item de módulo com status visual
     */
    private ItemStack createModuleItem(Material icon, String name, boolean enabled, String description, String... info) {
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
        
        // Sistema Principal
        if (displayName.contains("Sistema Principal")) {
            boolean current = plugin.getConfig().getBoolean("AutoSave.Enabled");
            plugin.getConfig().set("AutoSave.Enabled", !current);
            plugin.saveConfig();
            player.sendMessage("§e§l⏱ §6[AutoSave] §fSistema: " + (!current ? "§a§lATIVADO" : "§c§lDESATIVADO"));
            open(player);
        }
        // Broadcast
        else if (displayName.contains("Broadcast")) {
            boolean current = plugin.getConfig().getBoolean("AutoSave.BroadcastMessage");
            plugin.getConfig().set("AutoSave.BroadcastMessage", !current);
            plugin.saveConfig();
            player.sendMessage("§6§l📢 §6[AutoSave] §fBroadcast: " + (!current ? "§a§lATIVADO" : "§c§lDESATIVADO"));
            open(player);
        }
        // Forçar Save Agora
        else if (displayName.contains("Forçar Save Agora")) {
            boolean enabled = plugin.getConfig().getBoolean("AutoSave.Enabled");
            
            if (!enabled) {
                player.sendMessage("§c✗ [AutoSave] Sistema está desativado!");
                return;
            }
            
            player.sendMessage("§a§l⚡ [AutoSave] §7Executando salvamento forçado...");
            player.closeInventory();
            
            // Salvar todos os mundos
            int worldCount = 0;
            for (World world : Bukkit.getWorlds()) {
                world.save();
                worldCount++;
            }
            
            // Salvar jogadores
            Bukkit.savePlayers();
            
            player.sendMessage("§a§l✔ [AutoSave] §fSalvamento concluído!");
            player.sendMessage("§7▸ §f" + worldCount + " mundo(s) salvos");
            player.sendMessage("§7▸ §f" + Bukkit.getOnlinePlayers().size() + " jogador(es) salvos");
        }
        // Presets (detectar pelo nome: 1min, 3min, 5min, 10min, 30min)
        else if (displayName.contains("1min") || displayName.contains("3min") || 
                 displayName.contains("5min") || displayName.contains("10min") || 
                 displayName.contains("30min")) {
            
            int newInterval = 0;
            if (displayName.contains("1min")) newInterval = 60;
            else if (displayName.contains("3min")) newInterval = 180;
            else if (displayName.contains("5min")) newInterval = 300;
            else if (displayName.contains("10min")) newInterval = 600;
            else if (displayName.contains("30min")) newInterval = 1800;
            
            if (newInterval > 0) {
                plugin.getConfig().set("AutoSave.IntervalSeconds", newInterval);
                plugin.saveConfig();
                player.sendMessage("§e§l⏱ §6[AutoSave] §fPreset aplicado: §e" + formatInterval(newInterval));
                open(player);
            }
        }
        // Ajustes manuais
        else if (displayName.contains("-60s")) {
            int current = plugin.getConfig().getInt("AutoSave.IntervalSeconds");
            int newValue = Math.max(30, current - 60);
            plugin.getConfig().set("AutoSave.IntervalSeconds", newValue);
            plugin.saveConfig();
            player.sendMessage("§e§l⏱ §6[AutoSave] §fIntervalo: §e" + newValue + "s §7(§f" + formatInterval(newValue) + "§7)");
            open(player);
        }
        else if (displayName.contains("-30s")) {
            int current = plugin.getConfig().getInt("AutoSave.IntervalSeconds");
            int newValue = Math.max(30, current - 30);
            plugin.getConfig().set("AutoSave.IntervalSeconds", newValue);
            plugin.saveConfig();
            player.sendMessage("§e§l⏱ §6[AutoSave] §fIntervalo: §e" + newValue + "s §7(§f" + formatInterval(newValue) + "§7)");
            open(player);
        }
        else if (displayName.contains("+30s")) {
            int current = plugin.getConfig().getInt("AutoSave.IntervalSeconds");
            int newValue = current + 30;
            plugin.getConfig().set("AutoSave.IntervalSeconds", newValue);
            plugin.saveConfig();
            player.sendMessage("§e§l⏱ §6[AutoSave] §fIntervalo: §e" + newValue + "s §7(§f" + formatInterval(newValue) + "§7)");
            open(player);
        }
        else if (displayName.contains("+60s")) {
            int current = plugin.getConfig().getInt("AutoSave.IntervalSeconds");
            int newValue = current + 60;
            plugin.getConfig().set("AutoSave.IntervalSeconds", newValue);
            plugin.saveConfig();
            player.sendMessage("§e§l⏱ §6[AutoSave] §fIntervalo: §e" + newValue + "s §7(§f" + formatInterval(newValue) + "§7)");
            open(player);
        }
        // Resetar Padrões
        else if (displayName.contains("Resetar Padrões")) {
            plugin.getConfig().set("AutoSave.Enabled", true);
            plugin.getConfig().set("AutoSave.IntervalSeconds", 300);
            plugin.getConfig().set("AutoSave.BroadcastMessage", true);
            plugin.saveConfig();
            
            player.sendMessage("§e§l⏱ §6[AutoSave] §fConfigurações resetadas para padrão!");
            player.sendMessage("§7▸ Intervalo: §f300s §7(5min)");
            player.sendMessage("§7▸ Broadcast: §aAtivado");
            player.sendMessage("§7▸ Sistema: §aAtivado");
            open(player);
        }
        // Aplicar Mudanças
        else if (displayName.contains("Aplicar Mudanças")) {
            plugin.saveConfig();
            player.sendMessage("§a§l✔ §6[AutoSave] §fConfigurações aplicadas com sucesso!");
            open(player);
        }
        // Voltar
        else if (displayName.contains("Voltar")) {
            editorGUI.openMainMenu(player);
        }
    }
}
