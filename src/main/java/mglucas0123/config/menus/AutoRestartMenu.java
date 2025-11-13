package mglucas0123.config.menus;

import mglucas0123.Principal;
import mglucas0123.config.ChatInputManager;
import mglucas0123.config.ConfigEditorGUI;
import mglucas0123.config.editor.GUITemplate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AutoRestartMenu - Timeline Visual e Controle Avançado
 * 
 * REDESIGN UX:
 * - Timeline visual de horários (24h)
 * - Próximo restart destacado com contagem regressiva
 * - Edição granular (remover qualquer horário)
 * - Configuração de avisos (tempos personalizados)
 * - Status em tempo real
 */
public class AutoRestartMenu extends BaseMenu {
    
    private ConfigEditorGUI editorGUI;
    private ChatInputManager chatInputManager;
    
    public AutoRestartMenu(Principal plugin, ConfigEditorGUI editorGUI, ChatInputManager chatInputManager) {
        super(plugin);
        this.editorGUI = editorGUI;
        this.chatInputManager = chatInputManager;
    }
    
    @Override
    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§0§l⬛ §c§l⚡ AutoRestart Control §0§l⬛");
        
        GUITemplate template = loadTemplate("AutoRestartMenu", 54);
        
        boolean enabled = plugin.getConfig().getBoolean("AutoRestart.Enabled");
        boolean countdown = plugin.getConfig().getBoolean("AutoRestart.EnableCountdown");
        List<String> times = plugin.getConfig().getStringList("AutoRestart.Times");
        
        // Calcular próximo restart e tempo restante
        String nextRestart = getNextRestartTime(times);
        long minutesUntilRestart = getMinutesUntilRestart(times);
        
        // === HEADER ===
        ItemStack headerBorder = createItem(template.getMaterial("header_border"), " ");
        ItemStack accentRed = createItem(Material.RED_STAINED_GLASS_PANE, "§c◆");
        
        for (int i = 0; i < 9; i++) {
            if (i == 3 || i == 5) inv.setItem(i, accentRed);
            else inv.setItem(i, headerBorder);
        }
        
        // Info principal com tempo real
        String statusLine;
        if (!enabled) {
            statusLine = "§c✖ Sistema desativado";
        } else if (times.isEmpty()) {
            statusLine = "§e⚠ Nenhum horário configurado";
        } else if (nextRestart == null) {
            statusLine = "§e⚠ Horários inválidos";
        } else {
            statusLine = "§a✓ Próximo: §f" + nextRestart + " §7(§e" + minutesUntilRestart + "min§7)";
        }
        
        inv.setItem(4, createItem(Material.REDSTONE_BLOCK, "§c§l⚡ AutoRestart Control",
            "§8§m──────────────────────",
            "§7Reinício automático do servidor",
            "§7",
            "§8▸ §7Status: " + (enabled ? "§aAtivo" : "§cInativo"),
            "§8▸ §7Horários: §f" + times.size(),
            "§8▸ " + statusLine,
            "§8§m──────────────────────"));
        
        // === CONTROLE PRINCIPAL (Linha 1) ===
        inv.setItem(10, createModuleItem(
            enabled ? Material.REDSTONE : Material.REPEATER,
            "§c§l⚡ Sistema Principal",
            enabled,
            "Liga/desliga AutoRestart",
            enabled ? "Servidor reinicia automaticamente" : "Sem reinícios automáticos",
            times.size() + " horário(s) configurado(s)"));
        
        inv.setItem(11, createModuleItem(
            Material.CLOCK,
            "§e§l⏱ Contagem Regressiva",
            countdown,
            "Avisa jogadores antes do restart",
            countdown ? "Avisos ativos" : "Avisos desativados",
            "Mensagens em chat"));
        
        inv.setItem(12, createModuleItem(
            Material.WRITABLE_BOOK,
            "§6§l📝 Tempos de Aviso",
            true,
            "Configurar quando avisar",
            "Ex: 10min, 5min, 1min antes",
            "Clique para personalizar"));
        
        // === TIMELINE VISUAL (Linha 2) ===
        // Ordenar horários para exibição
        List<String> sortedTimes = new ArrayList<>(times);
        Collections.sort(sortedTimes);
        
        // Exibir até 6 horários na timeline
        for (int i = 0; i < 6; i++) {
            int slot = 19 + i;
            
            if (i < sortedTimes.size()) {
                String time = sortedTimes.get(i);
                boolean isNext = time.equals(nextRestart);
                
                inv.setItem(slot, createTimelineItem(time, isNext, i + 1));
            } else {
                // Slot vazio para adicionar horário
                inv.setItem(slot, createItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 
                    "§7§l+ Slot Vazio",
                    "§8§m──────────────────────",
                    "§7Clique para adicionar",
                    "§7um novo horário aqui",
                    "§8§m──────────────────────",
                    "§e➜ Clique para adicionar"));
            }
        }
        
        // Se há mais de 6 horários, mostrar indicador
        if (sortedTimes.size() > 6) {
            inv.setItem(25, createItem(Material.ARROW, "§e§l▼ Mais Horários",
                "§8§m──────────────────────",
                "§7Total: §f" + sortedTimes.size() + " horários",
                "§7Exibindo: §f6 primeiros",
                "§7",
                "§7Horários restantes:",
                getExtraTimesLore(sortedTimes),
                "§8§m──────────────────────"));
        }
        
        // === AÇÕES RÁPIDAS (Linha 3) ===
        inv.setItem(28, createItem(Material.EMERALD, "§a§l➕ Adicionar Horário",
            "§8§m──────────────────────",
            "§7Digite no formato §fHH:MM",
            "§7",
            "§fExemplos:",
            "§8▸ §f03:00 §7(3h da manhã)",
            "§8▸ §f12:00 §7(meio-dia)",
            "§8▸ §f22:30 §7(22h30)",
            "§8§m──────────────────────",
            "§e➜ Clique para adicionar"));
        
        inv.setItem(29, createItem(Material.BARRIER, "§c§l➖ Remover Horário",
            "§8§m──────────────────────",
            times.isEmpty() ? "§cNenhum horário configurado" : "§7Clique em um horário da timeline",
            "§7para removê-lo",
            "§7",
            times.isEmpty() ? "" : "§7Ou use §e/mgzconfig §7para",
            times.isEmpty() ? "" : "§7gerenciar horários",
            "§8§m──────────────────────"));
        
        inv.setItem(30, createItem(Material.PAPER, "§9§l📋 Lista Completa",
            "§8§m──────────────────────",
            "§7Ver todos os horários",
            "§7configurados no chat",
            "§7",
            "§7Total: §f" + times.size() + " horário(s)",
            "§8§m──────────────────────",
            "§e➜ Clique para listar"));
        
        inv.setItem(31, createItem(Material.REDSTONE_BLOCK, "§c§l� Limpar Todos",
            "§8§m──────────────────────",
            "§7Remove TODOS os horários",
            "§7",
            "§c⚠ §7Ação irreversível!",
            "§7Use com cuidado",
            "§8§m──────────────────────",
            times.isEmpty() ? "§7Nenhum horário para limpar" : "§e➜ Clique para limpar"));
        
        // === BORDAS LATERAIS ===
        ItemStack sideBorder = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 9; i < 45; i += 9) inv.setItem(i + 8, sideBorder);
        
        // === ESPAÇOS VAZIOS ===
        ItemStack filler = createItem(template.getMaterial("filler"), " ");
        int[] fillerSlots = {9, 13, 14, 15, 16, 17, 18, 26, 27, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
        for (int slot : fillerSlots) inv.setItem(slot, filler);
        
        // === FOOTER ===
        ItemStack footerBorder = createItem(template.getMaterial("footer_border"), " ");
        for (int i = 45; i < 54; i++) inv.setItem(i, footerBorder);
        
        // Estatísticas
        inv.setItem(45, createItem(Material.BOOK, "§e§l📊 Estatísticas",
            "§8§m──────────────────────",
            "§7Status do AutoRestart:",
            "§7",
            "§8▸ §fSistema: " + (enabled ? "§aAtivo" : "§cInativo"),
            "§8▸ §fHorários: §a" + times.size(),
            "§8▸ §fPróximo: " + (nextRestart != null ? "§f" + nextRestart : "§c-"),
            "§8▸ §fTempo: " + (minutesUntilRestart >= 0 ? "§e" + minutesUntilRestart + "min" : "§c-"),
            "§8§m──────────────────────"));
        
        // Ajuda
        inv.setItem(46, createItem(Material.KNOWLEDGE_BOOK, "§b§l❓ Ajuda",
            "§8§m──────────────────────",
            "§7Como configurar:",
            "§7",
            "§a1. §7Adicione horários (HH:MM)",
            "§a2. §7Ative o sistema principal",
            "§a3. §7Configure avisos (opcional)",
            "§a4. §7Servidor reinicia automaticamente",
            "§8§m──────────────────────"));
        
        // Testar agora
        inv.setItem(48, createItem(Material.TNT, "§6§l⚠ Testar Restart",
            "§8§m──────────────────────",
            "§7Simula um restart AGORA",
            "§7",
            "§c⚠ §7Isto irá reiniciar",
            "§c⚠ §7o servidor imediatamente!",
            "§8§m──────────────────────",
            enabled ? "§e➜ Clique para testar" : "§cSistema desativado"));
        
        // Voltar
        inv.setItem(49, createItem(Material.ARROW, "§7§l« Voltar ao Menu",
            "§8§m──────────────────────",
            "§7Retorna ao menu principal",
            "§7",
            "§a✓ §7Todas as configurações",
            "§a✓ §7foram salvas automaticamente",
            "§8§m──────────────────────",
            "§e➜ Clique para voltar"));
        
        // Reload
        inv.setItem(50, createItem(Material.EMERALD, "§a§l✔ Aplicar Mudanças",
            "§8§m──────────────────────",
            "§7Salva e recarrega config",
            "§7",
            "§8▸ §7Salva no config.yml",
            "§8▸ §7Reaplica horários",
            "§8§m──────────────────────",
            "§e➜ Clique para aplicar"));
        
        int[] emptyFooter = {47, 51, 52, 53};
        for (int slot : emptyFooter) inv.setItem(slot, headerBorder);
        
        player.openInventory(inv);
    }
    
    /**
     * Calcula o próximo horário de restart
     */
    private String getNextRestartTime(List<String> times) {
        if (times.isEmpty()) return null;
        
        try {
            LocalTime now = LocalTime.now();
            LocalTime closest = null;
            
            for (String timeStr : times) {
                LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
                
                if (time.isAfter(now)) {
                    if (closest == null || time.isBefore(closest)) {
                        closest = time;
                    }
                }
            }
            
            // Se nenhum horário futuro hoje, pegar o primeiro de amanhã
            if (closest == null) {
                closest = times.stream()
                    .map(t -> LocalTime.parse(t, DateTimeFormatter.ofPattern("HH:mm")))
                    .min(LocalTime::compareTo)
                    .orElse(null);
            }
            
            return closest != null ? closest.format(DateTimeFormatter.ofPattern("HH:mm")) : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Calcula minutos até o próximo restart
     */
    private long getMinutesUntilRestart(List<String> times) {
        String next = getNextRestartTime(times);
        if (next == null) return -1;
        
        try {
            LocalTime now = LocalTime.now();
            LocalTime restart = LocalTime.parse(next, DateTimeFormatter.ofPattern("HH:mm"));
            
            long minutes = now.until(restart, ChronoUnit.MINUTES);
            
            // Se negativo, é amanhã
            if (minutes < 0) {
                minutes = 1440 + minutes; // 24h em minutos
            }
            
            return minutes;
        } catch (Exception e) {
            return -1;
        }
    }
    
    /**
     * Cria item de horário na timeline
     */
    private ItemStack createTimelineItem(String time, boolean isNext, int position) {
        Material icon = isNext ? Material.CLOCK : Material.PAPER;
        String prefix = isNext ? "§e§l⏰" : "§7§l🕐";
        
        ItemStack item = new ItemStack(icon);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(prefix + " §f" + time + (isNext ? " §a§l← PRÓXIMO" : ""));
        
        java.util.List<String> lore = new java.util.ArrayList<>();
        lore.add("§8§m──────────────────────");
        lore.add("§7Horário #" + position);
        lore.add("");
        
        if (isNext) {
            long minutes = getMinutesUntilRestart(java.util.Arrays.asList(time));
            if (minutes >= 0) {
                long hours = minutes / 60;
                long mins = minutes % 60;
                
                lore.add("§a§l⏱ PRÓXIMO RESTART");
                lore.add("");
                lore.add("§8▸ §fTempo restante:");
                if (hours > 0) {
                    lore.add("§8▸ §e" + hours + "h " + mins + "min");
                } else {
                    lore.add("§8▸ §e" + mins + " minutos");
                }
            }
        } else {
            lore.add("§7Restart agendado");
        }
        
        lore.add("");
        lore.add("§8§m──────────────────────");
        lore.add("§c§l➜ CLIQUE PARA REMOVER");
        
        meta.setLore(lore);
        
        if (isNext) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.LUCK, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Gera lore para horários extras (quando há mais de 6)
     */
    private String getExtraTimesLore(List<String> sortedTimes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 6; i < sortedTimes.size() && i < 12; i++) {
            sb.append("§8▸ §f").append(sortedTimes.get(i));
            if (i < sortedTimes.size() - 1 && i < 11) sb.append("\n");
        }
        if (sortedTimes.size() > 12) {
            sb.append("\n§7... e mais ").append(sortedTimes.size() - 12).append(" horário(s)");
        }
        return sb.toString();
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
            boolean current = plugin.getConfig().getBoolean("AutoRestart.Enabled");
            plugin.getConfig().set("AutoRestart.Enabled", !current);
            plugin.saveConfig();
            player.sendMessage("§c§l⚡ §e[AutoRestart] §fSistema: " + (!current ? "§a§lATIVADO" : "§c§lDESATIVADO"));
            open(player);
        }
        // Contagem Regressiva
        else if (displayName.contains("Contagem Regressiva")) {
            boolean current = plugin.getConfig().getBoolean("AutoRestart.EnableCountdown");
            plugin.getConfig().set("AutoRestart.EnableCountdown", !current);
            plugin.saveConfig();
            player.sendMessage("§e§l⏱ §e[AutoRestart] §fContagem: " + (!current ? "§a§lATIVADA" : "§c§lDESATIVADA"));
            open(player);
        }
        // Tempos de Aviso (placeholder - pode implementar submenu)
        else if (displayName.contains("Tempos de Aviso")) {
            player.sendMessage("§6§l📝 §e[AutoRestart] §7Recurso em desenvolvimento!");
            player.sendMessage("§7Configure os tempos em §econfig.yml §7→ §fAutoRestart.CountdownTimes");
        }
        // Adicionar Horário
        else if (displayName.contains("Adicionar Horário") || displayName.contains("Slot Vazio")) {
            List<String> times = plugin.getConfig().getStringList("AutoRestart.Times");
            
            player.closeInventory();
            player.sendMessage("§c§l⚡ §e[AutoRestart] §7Digite o horário no formato §fHH:MM");
            player.sendMessage("§7Exemplos: §f05:30§7, §f14:00§7, §f23:59");
            player.sendMessage("§7Digite §c'cancelar' §7para abortar");
            
            chatInputManager.requestInput(player, "autorestart_time", input -> {
                if (input.equalsIgnoreCase("cancelar")) {
                    player.sendMessage("§c§l⚡ §e[AutoRestart] §7Operação cancelada");
                    open(player);
                    return;
                }
                
                // Validar formato HH:MM
                if (!input.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                    player.sendMessage("§c✗ [AutoRestart] Formato inválido!");
                    player.sendMessage("§7Use o formato §fHH:MM §7(ex: §f05:30§7, §f14:00§7)");
                    open(player);
                    return;
                }
                
                // Verificar duplicatas
                if (times.contains(input)) {
                    player.sendMessage("§c✗ [AutoRestart] Este horário já existe!");
                    open(player);
                    return;
                }
                
                // Adicionar horário
                times.add(input);
                plugin.getConfig().set("AutoRestart.Times", times);
                plugin.saveConfig();
                
                player.sendMessage("§a§l✔ [AutoRestart] §7Horário adicionado: §f" + input);
                player.sendMessage("§7Total de horários: §e" + times.size());
                open(player);
            });
        }
        // Remover horário específico da timeline
        else if (displayName.contains("🕐") || displayName.contains("⏰")) {
            // Extrair horário do nome (formato: "§7§l🕐 §f14:00" ou "§e§l⏰ §f03:00 §a§l← PRÓXIMO")
            String timeToRemove = extractTimeFromDisplayName(displayName);
            
            if (timeToRemove != null) {
                List<String> times = plugin.getConfig().getStringList("AutoRestart.Times");
                
                if (times.remove(timeToRemove)) {
                    plugin.getConfig().set("AutoRestart.Times", times);
                    plugin.saveConfig();
                    
                    player.sendMessage("§a§l✔ [AutoRestart] §7Horário removido: §f" + timeToRemove);
                    player.sendMessage("§7Total restante: §e" + times.size());
                    open(player);
                } else {
                    player.sendMessage("§c✗ [AutoRestart] Erro ao remover horário!");
                }
            }
        }
        // Lista Completa
        else if (displayName.contains("Lista Completa")) {
            List<String> times = plugin.getConfig().getStringList("AutoRestart.Times");
            
            if (times.isEmpty()) {
                player.sendMessage("§c§l⚡ [AutoRestart] §7Nenhum horário configurado");
                return;
            }
            
            player.sendMessage("§c§l━━━━━━━━ HORÁRIOS DE RESTART ━━━━━━━━");
            
            List<String> sorted = new ArrayList<>(times);
            Collections.sort(sorted);
            
            String next = getNextRestartTime(times);
            
            for (int i = 0; i < sorted.size(); i++) {
                String time = sorted.get(i);
                boolean isNext = time.equals(next);
                
                String prefix = isNext ? "§a⏰" : "§7🕐";
                String suffix = isNext ? " §a§l← PRÓXIMO" : "";
                
                player.sendMessage("§7[§f" + (i+1) + "§7] " + prefix + " §f" + time + suffix);
            }
            
            player.sendMessage("§c§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
        // Limpar Todos
        else if (displayName.contains("Limpar Todos")) {
            List<String> times = plugin.getConfig().getStringList("AutoRestart.Times");
            
            if (times.isEmpty()) {
                player.sendMessage("§c✗ [AutoRestart] Nenhum horário para limpar!");
                return;
            }
            
            int count = times.size();
            times.clear();
            plugin.getConfig().set("AutoRestart.Times", times);
            plugin.saveConfig();
            
            player.sendMessage("§c§l🗑 [AutoRestart] §fTodos os horários foram removidos!");
            player.sendMessage("§7Total removido: §e" + count);
            open(player);
        }
        // Testar Restart
        else if (displayName.contains("Testar Restart")) {
            boolean enabled = plugin.getConfig().getBoolean("AutoRestart.Enabled");
            
            if (!enabled) {
                player.sendMessage("§c✗ [AutoRestart] Sistema está desativado!");
                return;
            }
            
            player.sendMessage("§6§l⚠ [AutoRestart] §7Teste de restart será executado...");
            player.sendMessage("§c⚠ §7O servidor irá reiniciar em §e5 segundos§7!");
            
            player.closeInventory();
            
            // Agendar restart de teste (simulação)
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.broadcastMessage("§c§l⚠ [AutoRestart] §7Restart de TESTE executado!");
                Bukkit.broadcastMessage("§7Em ambiente real, o servidor reiniciaria agora.");
            }, 100L); // 5 segundos
        }
        // Aplicar Mudanças
        else if (displayName.contains("Aplicar Mudanças")) {
            plugin.saveConfig();
            player.sendMessage("§a§l✔ §e[AutoRestart] §fConfigurações aplicadas com sucesso!");
            open(player);
        }
        // Voltar
        else if (displayName.contains("Voltar")) {
            editorGUI.openMainMenu(player);
        }
    }
    
    /**
     * Extrai o horário do displayName de um item da timeline
     * Ex: "§7§l🕐 §f14:00" → "14:00"
     * Ex: "§e§l⏰ §f03:00 §a§l← PRÓXIMO" → "03:00"
     */
    private String extractTimeFromDisplayName(String displayName) {
        try {
            // Remove cores e símbolos
            String clean = displayName.replaceAll("§[0-9a-fk-or]", "");
            
            // Busca padrão HH:MM
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d{1,2}:\\d{2}");
            java.util.regex.Matcher matcher = pattern.matcher(clean);
            
            if (matcher.find()) {
                return matcher.group();
            }
        } catch (Exception e) {
            // Ignora erros
        }
        return null;
    }
}
