package mglucas0123.config.menus;

import mglucas0123.Principal;
import mglucas0123.config.ConfigEditorGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryBackupMenu extends BaseMenu {
    
    private ConfigEditorGUI editorGUI;
    
    public InventoryBackupMenu(Principal plugin, ConfigEditorGUI editorGUI) {
        super(plugin);
        this.editorGUI = editorGUI;
    }
    
    @Override
    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8§l▬▬▬▬▬ §6§l💾 Inventory Backup 💾 §8§l▬▬▬▬▬");
        
        int interval = plugin.getConfig().getInt("InventoryBackup.AutoBackupInterval");
        int maxBackups = plugin.getConfig().getInt("InventoryBackup.MaxBackupsPerPlayer");
        boolean onJoin = plugin.getConfig().getBoolean("InventoryBackup.BackupOnJoin");
        boolean onQuit = plugin.getConfig().getBoolean("InventoryBackup.BackupOnQuit");
        boolean onDeath = plugin.getConfig().getBoolean("InventoryBackup.BackupOnDeath");
        
        // ===== HEADER =====
        ItemStack headerBorder = createItem(Material.CYAN_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 9; i++) inv.setItem(i, headerBorder);
        
        inv.setItem(4, createItem(Material.ENDER_CHEST, "§6§l💾 Sistema de Backup de Inventário",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§7Proteção automática de inventários",
            "§7",
            "§f§lEstatísticas:",
            "§8▸ §7Intervalo: §e" + formatInterval(interval),
            "§8▸ §7Histórico: §e" + maxBackups + " backups/jogador",
            "§8▸ §7Eventos ativos: §e" + countActiveEvents(onJoin, onQuit, onDeath) + "/3",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        
        // ===== ROW 1: SISTEMA PRINCIPAL =====
        inv.setItem(11, createToggleItem(
            Material.CHEST,
            "§6§l⚙ Sistema Principal",
            true,
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§a§l✔ SISTEMA ATIVO",
            "§7",
            "§7Backup automático de inventários",
            "§7com múltiplos pontos de salvamento",
            "§7",
            "§f§lModo de Operação:",
            "§8▸ §7Backup periódico ativo",
            "§8▸ §7Eventos configuráveis",
            "§8▸ §7Restauração individual",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        
        // ===== ROW 2: PRESETS DE INTERVALO (5 opções rápidas) =====
        int[] presetIntervals = {300, 600, 900, 1800, 3600}; // 5min, 10min, 15min, 30min, 1h
        String[] presetNames = {"5 min", "10 min", "15 min", "30 min", "1 hora"};
        Material[] presetMaterials = {
            Material.LIME_DYE,      // 5min - Verde (rápido)
            Material.YELLOW_DYE,    // 10min - Amarelo (balanceado)
            Material.ORANGE_DYE,    // 15min - Laranja (moderado)
            Material.RED_DYE,       // 30min - Vermelho (lento)
            Material.PURPLE_DYE     // 1h - Roxo (muito lento)
        };
        String[][] presetDescriptions = {
            {"§a§lRÁPIDO", "§7Ideal para:", "§8▸ §7Servidor PvP intenso", "§8▸ §7Alto risco de perda"},
            {"§e§lBALANCEADO §7§o(Recomendado)", "§7Ideal para:", "§8▸ §7Maioria dos servidores", "§8▸ §7Equilíbrio perfeito"},
            {"§6§lMODERADO", "§7Ideal para:", "§8▸ §7Servidor survival", "§8▸ §7Médio risco"},
            {"§c§lLENTO", "§7Ideal para:", "§8▸ §7Servidor criativo", "§8▸ §7Baixo risco"},
            {"§5§lMUITO LENTO", "§7Ideal para:", "§8▸ §7Servidor estável", "§8▸ §7Economia de recursos"}
        };
        
        for (int i = 0; i < 5; i++) {
            inv.setItem(19 + i, createPresetIntervalItem(
                presetMaterials[i],
                presetNames[i],
                presetIntervals[i],
                interval == presetIntervals[i],
                presetDescriptions[i]
            ));
        }
        
        // ===== ROW 3: AJUSTES FINOS + MÁXIMO DE BACKUPS =====
        inv.setItem(28, createItem(Material.REDSTONE, "§c§l⏴ Diminuir Intervalo",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§7Reduz o tempo entre backups",
            "§7",
            "§fAtual: §e" + formatInterval(interval),
            "§fNovo: §a" + formatInterval(Math.max(60, interval - 60)),
            "§7",
            "§8▸ §7Ajuste: §c-1 minuto",
            "§8▸ §7Mínimo: §c60 segundos",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§e§l➤ Clique para diminuir"));
        
        inv.setItem(29, createItem(Material.LIME_DYE, "§a§l⏵ Aumentar Intervalo",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§7Aumenta o tempo entre backups",
            "§7",
            "§fAtual: §e" + formatInterval(interval),
            "§fNovo: §a" + formatInterval(interval + 60),
            "§7",
            "§8▸ §7Ajuste: §a+1 minuto",
            "§8▸ §7Máximo: §aSem limite",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§e§l➤ Clique para aumentar"));
        
        inv.setItem(31, createItem(Material.BARREL, "§6§l📦 Máximo de Backups",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§7Limite de backups armazenados",
            "§7por jogador (FIFO - mais antigo removido)",
            "§7",
            "§fAtual: §e" + maxBackups + " backups",
            "§fEspaço estimado: §e~" + estimateStoragePerPlayer(maxBackups) + " MB/jogador",
            "§7",
            "§f§lRecomendações:",
            "§8▸ §f5 backups §8- Econômico",
            "§8▸ §f10 backups §8- Balanceado",
            "§8▸ §f15 backups §8- Completo",
            "§8▸ §f20+ backups §8- Máxima proteção",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§c§l◀ Esq §f-1  §8│  §a§l▶ Dir §f+1"));
        
        inv.setItem(32, createItem(Material.COMPARATOR, "§e§l🔄 Resetar Padrões",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§7Restaura configurações originais",
            "§7",
            "§f§lValores padrão:",
            "§8▸ §7Intervalo: §e10 minutos",
            "§8▸ §7Máximo: §e10 backups",
            "§8▸ §7Join: §a✔ Ativado",
            "§8▸ §7Quit: §a✔ Ativado",
            "§8▸ §7Death: §a✔ Ativado",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§e§l➤ Clique para resetar"));
        
        // ===== ROW 4: EVENTOS DE BACKUP =====
        inv.setItem(37, createToggleItem(
            onJoin ? Material.LIME_WOOL : Material.RED_WOOL,
            "§a§l⬇ Backup ao Entrar (Join)",
            onJoin,
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            onJoin ? "§a§l✔ ATIVADO" : "§c§l✖ DESATIVADO",
            "§7",
            "§7Salva inventário quando jogador",
            "§7conecta no servidor",
            "§7",
            "§f§lBenefícios:",
            "§8▸ §7Protege contra rollback",
            "§8▸ §7Snapshot do estado inicial",
            "§8▸ §7Comparação futura",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§e§l➤ Clique para alternar"));
        
        inv.setItem(38, createToggleItem(
            onQuit ? Material.LIME_WOOL : Material.RED_WOOL,
            "§c§l⬆ Backup ao Sair (Quit)",
            onQuit,
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            onQuit ? "§a§l✔ ATIVADO" : "§c§l✖ DESATIVADO",
            "§7",
            "§7Salva inventário quando jogador",
            "§7desconecta do servidor",
            "§7",
            "§f§lBenefícios:",
            "§8▸ §7Captura progresso final",
            "§8▸ §7Protege contra crashs",
            "§8▸ §7Último estado conhecido",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§e§l➤ Clique para alternar"));
        
        inv.setItem(39, createToggleItem(
            onDeath ? Material.LIME_WOOL : Material.RED_WOOL,
            "§4§l💀 Backup ao Morrer (Death)",
            onDeath,
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            onDeath ? "§a§l✔ ATIVADO" : "§c§l✖ DESATIVADO",
            "§7",
            "§7Salva inventário ANTES da morte",
            "§7para recuperação total",
            "§7",
            "§f§lBenefícios:",
            "§8▸ §7Recupera itens perdidos",
            "§8▸ §7Anti-grief de morte",
            "§8▸ §7Rollback de morte acidental",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§e§l➤ Clique para alternar"));
        
        inv.setItem(41, createItem(Material.WRITABLE_BOOK, "§b§l� Estatísticas",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§7Resumo da configuração atual",
            "§7",
            "§f§lSistema:",
            "§8▸ §7Intervalo: §e" + formatInterval(interval),
            "§8▸ §7Backups/jogador: §e" + maxBackups,
            "§8▸ §7Eventos ativos: §e" + countActiveEvents(onJoin, onQuit, onDeath) + "/3",
            "§7",
            "§f§lEspaço Estimado:",
            "§8▸ §7Por jogador: §e~" + estimateStoragePerPlayer(maxBackups) + " MB",
            "§8▸ §710 jogadores: §e~" + (estimateStoragePerPlayer(maxBackups) * 10) + " MB",
            "§8▸ §750 jogadores: §e~" + (estimateStoragePerPlayer(maxBackups) * 50) + " MB",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        
        // ===== FOOTER =====
        ItemStack footerBorder = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 45; i < 54; i++) inv.setItem(i, footerBorder);
        
        inv.setItem(49, createItem(Material.ARROW, "§f§l« Voltar",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§7Retornar ao menu principal",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§e§l➤ Clique para voltar"));
        
        inv.setItem(53, createItem(Material.KNOWLEDGE_BOOK, "§e§l❓ Ajuda",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
            "§f§lComo funciona o backup?",
            "§7",
            "§71. §fBackup automático periódico",
            "§72. §fBackup em eventos específicos",
            "§73. §fArmazenamento em arquivo JSON",
            "§74. §fRestauração via comando",
            "§7",
            "§f§lComandos disponíveis:",
            "§8▸ §e/playerdata backup <jogador> §7- Força backup",
            "§8▸ §e/playerdata restore <jogador> <#> §7- Restaura",
            "§8▸ §e/playerdata list <jogador> §7- Lista backups",
            "§8▸ §e/playerdata delete <jogador> <#> §7- Deleta backup",
            "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        
        // ===== PREENCHER ESPAÇOS VAZIOS =====
        ItemStack filler = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        int[] fillerSlots = {9, 10, 12, 13, 14, 15, 16, 17, 18, 24, 25, 26, 27, 30, 33, 34, 35, 36, 40, 42, 43, 44};
        for (int slot : fillerSlots) inv.setItem(slot, filler);
        
        player.openInventory(inv);
    }
    
    public void handleClick(Player player, ItemStack clicked, InventoryClickEvent event) {
        String displayName = clicked.getItemMeta().getDisplayName();
        
        // ===== PRESETS DE INTERVALO (identificação pelo displayName exato) =====
        if (displayName.equals("§6§l⏱ 5 min")) {
            plugin.getConfig().set("InventoryBackup.AutoBackupInterval", 300);
            plugin.saveConfig();
            player.sendMessage("§6§l💾 §e[Backup] §7Intervalo: §a5 minutos §7(Rápido)");
            open(player);
        } else if (displayName.equals("§6§l⏱ 10 min")) {
            plugin.getConfig().set("InventoryBackup.AutoBackupInterval", 600);
            plugin.saveConfig();
            player.sendMessage("§6§l💾 §e[Backup] §7Intervalo: §e10 minutos §7(Balanceado)");
            open(player);
        } else if (displayName.equals("§6§l⏱ 15 min")) {
            plugin.getConfig().set("InventoryBackup.AutoBackupInterval", 900);
            plugin.saveConfig();
            player.sendMessage("§6§l💾 §e[Backup] §7Intervalo: §615 minutos §7(Moderado)");
            open(player);
        } else if (displayName.equals("§6§l⏱ 30 min")) {
            plugin.getConfig().set("InventoryBackup.AutoBackupInterval", 1800);
            plugin.saveConfig();
            player.sendMessage("§6§l💾 §e[Backup] §7Intervalo: §c30 minutos §7(Lento)");
            open(player);
        } else if (displayName.equals("§6§l⏱ 1 hora")) {
            plugin.getConfig().set("InventoryBackup.AutoBackupInterval", 3600);
            plugin.saveConfig();
            player.sendMessage("§6§l💾 §e[Backup] §7Intervalo: §51 hora §7(Muito Lento)");
            open(player);
        }
        
        // ===== AJUSTES FINOS =====
        else if (displayName.contains("Diminuir Intervalo")) {
            int current = plugin.getConfig().getInt("InventoryBackup.AutoBackupInterval");
            int newValue = Math.max(60, current - 60);
            plugin.getConfig().set("InventoryBackup.AutoBackupInterval", newValue);
            plugin.saveConfig();
            player.sendMessage("§6§l💾 §e[Backup] §7Intervalo: §f" + formatInterval(newValue) + " §7(§c-1 minuto§7)");
            open(player);
        } else if (displayName.contains("Aumentar Intervalo")) {
            int current = plugin.getConfig().getInt("InventoryBackup.AutoBackupInterval");
            int newValue = current + 60;
            plugin.getConfig().set("InventoryBackup.AutoBackupInterval", newValue);
            plugin.saveConfig();
            player.sendMessage("§6§l💾 §e[Backup] §7Intervalo: §f" + formatInterval(newValue) + " §7(§a+1 minuto§7)");
            open(player);
        }
        
        // ===== MÁXIMO DE BACKUPS =====
        else if (displayName.contains("Máximo de Backups")) {
            int current = plugin.getConfig().getInt("InventoryBackup.MaxBackupsPerPlayer");
            int newValue;
            
            if (event.isLeftClick()) {
                newValue = Math.max(1, current - 1);
                player.sendMessage("§6§l💾 §e[Backup] §7Máximo: §c" + newValue + " backups §7(§c-1§7)");
            } else {
                newValue = Math.min(50, current + 1);
                player.sendMessage("§6§l💾 §e[Backup] §7Máximo: §a" + newValue + " backups §7(§a+1§7)");
            }
            
            plugin.getConfig().set("InventoryBackup.MaxBackupsPerPlayer", newValue);
            plugin.saveConfig();
            open(player);
        }
        
        // ===== RESETAR PADRÕES =====
        else if (displayName.contains("Resetar Padrões")) {
            plugin.getConfig().set("InventoryBackup.AutoBackupInterval", 600);
            plugin.getConfig().set("InventoryBackup.MaxBackupsPerPlayer", 10);
            plugin.getConfig().set("InventoryBackup.BackupOnJoin", true);
            plugin.getConfig().set("InventoryBackup.BackupOnQuit", true);
            plugin.getConfig().set("InventoryBackup.BackupOnDeath", true);
            plugin.saveConfig();
            player.sendMessage("§6§l💾 §e[Backup] §aConfiguração resetada para padrão!");
            player.sendMessage("§7▸ Intervalo: §e10 minutos");
            player.sendMessage("§7▸ Máximo: §e10 backups");
            player.sendMessage("§7▸ Todos eventos: §a✔ Ativados");
            open(player);
        }
        
        // ===== EVENTOS =====
        else if (displayName.contains("Backup ao Entrar")) {
            boolean current = plugin.getConfig().getBoolean("InventoryBackup.BackupOnJoin");
            plugin.getConfig().set("InventoryBackup.BackupOnJoin", !current);
            plugin.saveConfig();
            player.sendMessage("§a§l⬇ §e[Backup] Join: " + (!current ? "§a§lATIVADO ✔" : "§c§lDESATIVADO ✖"));
            open(player);
        } else if (displayName.contains("Backup ao Sair")) {
            boolean current = plugin.getConfig().getBoolean("InventoryBackup.BackupOnQuit");
            plugin.getConfig().set("InventoryBackup.BackupOnQuit", !current);
            plugin.saveConfig();
            player.sendMessage("§c§l⬆ §e[Backup] Quit: " + (!current ? "§a§lATIVADO ✔" : "§c§lDESATIVADO ✖"));
            open(player);
        } else if (displayName.contains("Backup ao Morrer")) {
            boolean current = plugin.getConfig().getBoolean("InventoryBackup.BackupOnDeath");
            plugin.getConfig().set("InventoryBackup.BackupOnDeath", !current);
            plugin.saveConfig();
            player.sendMessage("§4§l💀 §e[Backup] Death: " + (!current ? "§a§lATIVADO ✔" : "§c§lDESATIVADO ✖"));
            open(player);
        }
        
        // ===== VOLTAR =====
        else if (displayName.contains("Voltar")) {
            editorGUI.openMainMenu(player);
        }
    }
    
    // ===== MÉTODOS AUXILIARES =====
    
    /**
     * Formata intervalo em segundos para formato legível
     * @param seconds Segundos
     * @return String formatada (ex: "5min", "1h 30min")
     */
    private String formatInterval(int seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            int minutes = seconds / 60;
            int remainingSeconds = seconds % 60;
            if (remainingSeconds == 0) {
                return minutes + " minuto" + (minutes != 1 ? "s" : "");
            }
            return minutes + "min " + remainingSeconds + "s";
        } else {
            int hours = seconds / 3600;
            int remainingMinutes = (seconds % 3600) / 60;
            if (remainingMinutes == 0) {
                return hours + " hora" + (hours != 1 ? "s" : "");
            }
            return hours + "h " + remainingMinutes + "min";
        }
    }
    
    /**
     * Conta quantos eventos estão ativos
     * @param onJoin Join ativo
     * @param onQuit Quit ativo
     * @param onDeath Death ativo
     * @return Número de eventos ativos
     */
    private int countActiveEvents(boolean onJoin, boolean onQuit, boolean onDeath) {
        int count = 0;
        if (onJoin) count++;
        if (onQuit) count++;
        if (onDeath) count++;
        return count;
    }
    
    /**
     * Estima espaço de armazenamento por jogador
     * @param maxBackups Número máximo de backups
     * @return Espaço estimado em MB
     */
    private double estimateStoragePerPlayer(int maxBackups) {
        // Estimativa: ~50KB por backup de inventário completo (JSON comprimido)
        double bytesPerBackup = 50 * 1024; // 50KB
        double totalBytes = bytesPerBackup * maxBackups;
        double megabytes = totalBytes / (1024 * 1024);
        return Math.round(megabytes * 10.0) / 10.0; // 1 casa decimal
    }
    
    /**
     * Cria item de preset de intervalo com glow se ativo
     * @param material Material do item
     * @param name Nome do preset
     * @param presetInterval Intervalo do preset
     * @param isActive Se é o preset ativo
     * @param descriptions Linhas de descrição
     * @return ItemStack configurado
     */
    private ItemStack createPresetIntervalItem(Material material, String name, int presetInterval, boolean isActive, String... descriptions) {
        List<String> lore = new ArrayList<>();
        lore.add("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        if (isActive) {
            lore.add("§a§l✔ PRESET ATIVO");
        } else {
            lore.add("§7Clique para ativar");
        }
        
        lore.add("§7");
        for (String desc : descriptions) {
            lore.add(desc);
        }
        
        lore.add("§7");
        lore.add("§f§lIntervalo: §e" + formatInterval(presetInterval));
        lore.add("§f§lBackups/dia: §e~" + estimateBackupsPerDay(presetInterval));
        lore.add("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        if (isActive) {
            lore.add("§a§l✦ Configurado");
        } else {
            lore.add("§e§l➤ Clique para configurar");
        }
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§l⏱ " + name);
        meta.setLore(lore);
        
        // Glow se for o preset ativo
        if (isActive) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Estima quantos backups serão criados por dia
     * @param intervalSeconds Intervalo em segundos
     * @return Número de backups estimados por dia
     */
    private int estimateBackupsPerDay(int intervalSeconds) {
        int secondsPerDay = 24 * 60 * 60; // 86400 segundos
        return secondsPerDay / intervalSeconds;
    }
}
