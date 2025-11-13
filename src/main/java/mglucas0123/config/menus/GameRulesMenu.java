package mglucas0123.config.menus;

import mglucas0123.Principal;
import mglucas0123.config.ConfigEditorGUI;
import mglucas0123.events.ServerControl;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class GameRulesMenu extends BaseMenu {

    private ConfigEditorGUI editorGUI;

    public GameRulesMenu(Principal plugin, ConfigEditorGUI editorGUI) {
        super(plugin);
        this.editorGUI = editorGUI;
    }

    @Override
    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8§l▬▬▬▬▬ §6§l📜 GameRules 📜 §8§l▬▬▬▬▬");

    ItemStack headerBorder = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
    ItemStack footerBorder = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
    ItemStack fillerItem = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
    ItemStack backButtonItem = createItem(Material.ARROW, "§f§l« Voltar",
        "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
        "§7Retornar ao menu principal",
        "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
        "§e§l➤ Clique para voltar");
    ItemStack applyButtonItem = createItem(Material.EMERALD, "§e§l🔄 Aplicar Agora",
        "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
        "§7Força aplicação de TODAS",
        "§7as regras em todos os mundos",
        "§7",
        "§f§lAção:",
        "§8▸ §7Recalcula configurações",
        "§8▸ §7Atualiza mundos online",
        "§8▸ §7Resolve inconsistências",
        "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
        "§a§l➤ Clique para aplicar");

        
        boolean keepInv = plugin.getConfig().getBoolean("GameRules.KeepInventory.Enabled");
        boolean announceAdv = plugin.getConfig().getBoolean("GameRules.AnnounceAdvancements.Enabled");
        boolean mobGrief = plugin.getConfig().getBoolean("GameRules.MobGriefing.Enabled");
        boolean doInsomnia = plugin.getConfig().getBoolean("GameRules.DoInsomnia.Enabled");
        boolean cmdBlock = plugin.getConfig().getBoolean("GameRules.CommandBlockOutput.Enabled");
        boolean doDaylight = plugin.getConfig().getBoolean("GameRules.DoDaylightCycle.Enabled");
        boolean doRespawn = plugin.getConfig().getBoolean("GameRules.DoImmediateRespawn.Enabled");
        boolean doFire = plugin.getConfig().getBoolean("GameRules.DoFireTick.Enabled");

        
        int sleepPercent = plugin.getConfig().getInt("GameRules.PlayersSleepingPercentage.Value");
        int tickSpeed = plugin.getConfig().getInt("GameRules.RandomTickSpeed.Value");
        int maxCram = plugin.getConfig().getInt("GameRules.MaxEntityCramming.Value");

        
        for (int i = 0; i < 9; i++)
            inv.setItem(i, headerBorder);

        inv.setItem(4, createItem(Material.BOOK, "§6§l📜 Sistema de GameRules",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§7Controle total das regras do servidor",
                "§7",
                "§f§lEstatísticas:",
                "§8▸ §7Regras booleanas: §e8 configuradas",
                "§8▸ §7Regras numéricas: §e3 configuradas",
                "§8▸ §7Total ativo: §e" + countActiveRules(keepInv, announceAdv, mobGrief, doInsomnia, cmdBlock,
                        doDaylight, doRespawn, doFire) + "/8",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        
        inv.setItem(10, createItem(Material.PLAYER_HEAD, "§e§l👤 CATEGORIA: JOGADOR",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§7Regras relacionadas ao jogador",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        inv.setItem(11, createToggleItemWithWorlds(
                keepInv ? Material.CHEST : Material.BARREL,
                "§6§l💎 Keep Inventory",
                keepInv,
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                keepInv ? "§a§l✔ ATIVADO" : "§c§l✖ DESATIVADO",
                "§7",
                "§7Manter inventário ao morrer",
                "§7Jogadores não perdem itens",
                "§7",
                "§f§lImpacto:",
                "§8▸ §7Servidor casual: §aRecomendado",
                "§8▸ §7Servidor hardcore: §cNão recomendado",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        inv.setItem(12, createToggleItemWithWorlds(
                announceAdv ? Material.EMERALD : Material.COAL,
                "§6§l🏆 Announce Advancements",
                announceAdv,
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                announceAdv ? "§a§l✔ ATIVADO" : "§c§l✖ DESATIVADO",
                "§7",
                "§7Anunciar conquistas no chat",
                "§7Todos veem quando alguém avança",
                "§7",
                "§f§lImpacto:",
                "§8▸ §7Comunidade ativa: §aRecomendado",
                "§8▸ §7Servidor privado: §7Opcional",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        inv.setItem(13, createToggleItemWithWorlds(
                doRespawn ? Material.TOTEM_OF_UNDYING : Material.SKELETON_SKULL,
                "§6§l⚡ Immediate Respawn",
                doRespawn,
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                doRespawn ? "§a§l✔ ATIVADO" : "§c§l✖ DESATIVADO",
                "§7",
                "§7Respawn instantâneo sem tela",
                "§7Jogador revive imediatamente",
                "§7",
                "§f§lImpacto:",
                "§8▸ §7Servidor PvP: §aRecomendado",
                "§8▸ §7Hardcore: §cNão recomendado",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        
        inv.setItem(19, createItem(Material.GRASS_BLOCK, "§a§l🌍 CATEGORIA: MUNDO",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§7Regras relacionadas ao ambiente",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        inv.setItem(20, createToggleItemWithWorlds(
                mobGrief ? Material.TNT : Material.BEDROCK,
                "§6§l💥 Mob Griefing",
                mobGrief,
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                mobGrief ? "§a§l✔ ATIVADO" : "§c§l✖ DESATIVADO",
                "§7",
                "§7Mobs podem destruir blocos",
                "§7Creepers explodem, Enderman movem",
                "§7",
                "§f§lImpacto:",
                "§8▸ §7Survival realista: §aAtivado",
                "§8▸ §7Proteção construções: §cDesativado",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        inv.setItem(21, createToggleItemWithWorlds(
                doFire ? Material.FLINT_AND_STEEL : Material.WATER_BUCKET,
                "§6§l🔥 Fire Tick",
                doFire,
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                doFire ? "§a§l✔ ATIVADO" : "§c§l✖ DESATIVADO",
                "§7",
                "§7Fogo se espalha e queima blocos",
                "§7Incêndios naturais podem ocorrer",
                "§7",
                "§f§lImpacto:",
                "§8▸ §7Realismo: §aAtivado",
                "§8▸ §7Segurança: §cDesativado",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        inv.setItem(22, createToggleItemWithWorlds(
                doDaylight ? Material.CLOCK : Material.SOUL_LANTERN,
                "§6§l☀ Daylight Cycle",
                doDaylight,
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                doDaylight ? "§a§l✔ ATIVADO" : "§c§l✖ DESATIVADO",
                "§7",
                "§7Ciclo de dia e noite ativo",
                "§7Tempo avança naturalmente",
                "§7",
                "§f§lImpacto:",
                "§8▸ §7Survival: §aAtivado",
                "§8▸ §7Build/Lobby: §cDesativado (tempo fixo)",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        
        inv.setItem(28, createItem(Material.ZOMBIE_HEAD, "§c§l👹 CATEGORIA: MOBS",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§7Regras relacionadas a criaturas",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        inv.setItem(29, createToggleItemWithWorlds(
                doInsomnia ? Material.PHANTOM_MEMBRANE : Material.LIGHT_GRAY_DYE,
                "§6§l👻 Do Insomnia",
                doInsomnia,
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                doInsomnia ? "§a§l✔ ATIVADO" : "§c§l✖ DESATIVADO",
                "§7",
                "§7Phantoms spawnam sem dormir",
                "§73+ dias sem dormir = Phantoms",
                "§7",
                "§f§lImpacto:",
                "§8▸ §7Survival desafiador: §aAtivado",
                "§8▸ §7Casual/Skyblock: §cDesativado",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        inv.setItem(30, createNumericItemWithWorlds(
                Material.MINECART,
                "§b§l📦 Max Entity Cramming",
                maxCram,
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§f§lValor atual: §e" + maxCram + " entidades",
                "§7",
                "§7Limite de entidades por bloco",
                "§7Anti-lag para farms automáticas",
                "§7",
                "§f§lRecomendações:",
                "§8▸ §724 §8- Padrão Minecraft",
                "§8▸ §78-12 §8- Farms otimizadas",
                "§8▸ §70 §8- Sem limite (lag)",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§c§l◀ Esq §f-2  §8│  §a§l▶ Dir §f+2",
                "§8(Shift = ±8)  §8│  §b§l⚙ Shift+Dir: Mundos"));

        
        inv.setItem(37, createItem(Material.REDSTONE, "§d§l⚙ CATEGORIA: SISTEMA",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§7Regras avançadas do servidor",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        inv.setItem(38, createToggleItemWithWorlds(
                cmdBlock ? Material.COMMAND_BLOCK : Material.BARRIER,
                "§6§l� Command Block Output",
                cmdBlock,
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                cmdBlock ? "§a§l✔ ATIVADO" : "§c§l✖ DESATIVADO",
                "§7",
                "§7Mostrar saída de Command Blocks",
                "§7Mensagens aparecem no chat",
                "§7",
                "§f§lImpacto:",
                "§8▸ §7Debug/Dev: §aAtivado",
                "§8▸ §7Produção: §cDesativado (spam)",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        inv.setItem(39, createNumericItemWithWorlds(
                Material.WHEAT_SEEDS,
                "§b§l� Random Tick Speed",
                tickSpeed,
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§f§lValor atual: §e" + tickSpeed + " ticks",
                "§7",
                "§7Velocidade de crescimento/decay",
                "§7Crops, grama, gelo, fogo, etc.",
                "§7",
                "§f§lRecomendações:",
                "§8▸ §70 §8- Lobby/Build (sem mudanças)",
                "§8▸ §73 §8- Survival padrão",
                "§8▸ §76+ §8- Farms rápidas (lag)",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§c§l◀ Esq §f-1  §8│  §a§l▶ Dir §f+1",
                "§8(Shift = ±5)  §8│  §b§l⚙ Shift+Dir: Mundos"));

        inv.setItem(40, createNumericItemWithWorlds(
                Material.RED_BED,
                "§b§l� Players Sleeping %",
                sleepPercent,
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§f§lValor atual: §e" + sleepPercent + "%",
                "§7",
                "§7% de jogadores para pular noite",
                "§7Define quantos precisam dormir",
                "§7",
                "§f§lRecomendações:",
                "§8▸ §7100% §8- Todos devem dormir",
                "§8▸ §750% §8- Metade é suficiente",
                "§8▸ §71% §8- Qualquer um pula (casual)",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§c§l◀ Esq §f-5  §8│  §a§l▶ Dir §f+5",
                "§8(Shift = ±10)  §8│  §b§l⚙ Shift+Dir: Mundos"));

        
        for (int i = 45; i < 54; i++)
            inv.setItem(i, footerBorder);

    inv.setItem(49, backButtonItem);

    inv.setItem(53, applyButtonItem);

        
        int[] fillerSlots = { 9, 14, 15, 16, 17, 18, 23, 24, 25, 26, 27, 31, 32, 33, 34, 35, 36, 41, 42, 43, 44 };
        for (int slot : fillerSlots)
        inv.setItem(slot, fillerItem);

        player.openInventory(inv);
    }

    public void handleClick(Player player, ItemStack clicked, InventoryClickEvent event) {
        String displayName = clicked.getItemMeta().getDisplayName();
        boolean isLeftClick = event.isLeftClick();
        boolean isRightClick = event.isRightClick();
        boolean isShift = event.isShiftClick();

        
        if (displayName.contains("Aplicar Agora")) {
            ServerControl serverControl = new ServerControl(plugin);
            serverControl.applyGameRulesToAllWorlds();
            player.sendMessage("§a§l✓ §6[GameRules] §fTodas as regras foram aplicadas!");
            player.sendMessage("§7▸ Mundos atualizados: §e" + Bukkit.getWorlds().size());
            open(player);
            return;
        }

        
        if (displayName.contains("Keep Inventory")) {
            if (isLeftClick)
                toggleBooleanRule("KeepInventory", player);
            else if (isRightClick)
                openWorldListMenu(player, "KeepInventory");
        } else if (displayName.contains("Announce Advancements")) {
            if (isLeftClick)
                toggleBooleanRule("AnnounceAdvancements", player);
            else if (isRightClick)
                openWorldListMenu(player, "AnnounceAdvancements");
        } else if (displayName.contains("Mob Griefing")) {
            if (isLeftClick)
                toggleBooleanRule("MobGriefing", player);
            else if (isRightClick)
                openWorldListMenu(player, "MobGriefing");
        } else if (displayName.contains("DoInsomnia") || displayName.contains("Do Insomnia")) {
            if (isLeftClick)
                toggleBooleanRule("DoInsomnia", player);
            else if (isRightClick)
                openWorldListMenu(player, "DoInsomnia");
        } else if (displayName.contains("CommandBlockOutput") || displayName.contains("Command Block Output")) {
            if (isLeftClick)
                toggleBooleanRule("CommandBlockOutput", player);
            else if (isRightClick)
                openWorldListMenu(player, "CommandBlockOutput");
        } else if (displayName.contains("Daylight Cycle")) {
            if (isLeftClick)
                toggleBooleanRule("DoDaylightCycle", player);
            else if (isRightClick)
                openWorldListMenu(player, "DoDaylightCycle");
        } else if (displayName.contains("Immediate Respawn")) {
            if (isLeftClick)
                toggleBooleanRule("DoImmediateRespawn", player);
            else if (isRightClick)
                openWorldListMenu(player, "DoImmediateRespawn");
        } else if (displayName.contains("Fire Tick")) {
            if (isLeftClick)
                toggleBooleanRule("DoFireTick", player);
            else if (isRightClick)
                openWorldListMenu(player, "DoFireTick");
        }

        
        else if (displayName.contains("Players Sleeping")) {
            if (isShift && isRightClick) {
                openWorldListMenu(player, "PlayersSleepingPercentage");
            } else {
                adjustNumericRule("PlayersSleepingPercentage", player, isLeftClick, isRightClick, isShift, 5, 10, 0,
                        100);
            }
        } else if (displayName.contains("Random Tick Speed")) {
            if (isShift && isRightClick) {
                openWorldListMenu(player, "RandomTickSpeed");
            } else {
                adjustNumericRule("RandomTickSpeed", player, isLeftClick, isRightClick, isShift, 1, 5, 0, 1000);
            }
        } else if (displayName.contains("Max Entity Cramming")) {
            if (isShift && isRightClick) {
                openWorldListMenu(player, "MaxEntityCramming");
            } else {
                adjustNumericRule("MaxEntityCramming", player, isLeftClick, isRightClick, isShift, 2, 8, 0, 100);
            }
        }

        else if (displayName.contains("Voltar")) {
            editorGUI.openMainMenu(player);
        }
    }

    
    private int countActiveRules(boolean... rules) {
        int count = 0;
        for (boolean rule : rules) {
            if (rule)
                count++;
        }
        return count;
    }

    
    private ItemStack createToggleItemWithWorlds(Material material, String name, boolean isActive, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        
        meta.setDisplayName(name);

        
        List<String> loreList = new ArrayList<>();
        for (String line : lore) {
            loreList.add(line);
        }
        loreList.add("§7");
        loreList.add("§e§l➤ Esq§7: Alternar  §8│  §b§l➤ Dir§7: Mundos");
        meta.setLore(loreList);

        
        if (isActive) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
        return item;
    }

    
    private ItemStack createNumericItemWithWorlds(Material material, String name, int value, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        
        meta.setDisplayName(name + " §8│ §f" + value);

        
        List<String> loreList = new ArrayList<>();

        
        for (String line : lore) {
            loreList.add(line);
        }

        
        loreList.add("§8(Shift = ±valor)  §8│  §b§l⚙ Shift+Dir§7: Mundos");

        meta.setLore(loreList);
        item.setItemMeta(meta);
        return item;
    }

    
    private void toggleBooleanRule(String ruleName, Player player) {
        String path = "GameRules." + ruleName + ".Enabled";
        boolean current = plugin.getConfig().getBoolean(path);
        plugin.getConfig().set(path, !current);
        plugin.saveConfig();

        ServerControl serverControl = new ServerControl(plugin);
        serverControl.applyGameRulesToAllWorlds();

        player.sendMessage("§6[GameRules] " + ruleName + ": " + (!current ? "§aAtivado" : "§cDesativado"));
        open(player);
    }

    
    private void adjustNumericRule(String ruleName, Player player, boolean isLeftClick, boolean isRightClick,
            boolean isShift, int normalChange, int shiftChange, int min, int max) {
        String path = "GameRules." + ruleName + ".Value";
        int current = plugin.getConfig().getInt(path);
        int change = 0;

        if (isLeftClick && isShift)
            change = shiftChange;
        else if (isLeftClick)
            change = normalChange;
        else if (isRightClick && isShift)
            change = -shiftChange;
        else if (isRightClick)
            change = -normalChange;

        int newValue = Math.max(min, Math.min(max, current + change));
        plugin.getConfig().set(path, newValue);
        plugin.saveConfig();

        ServerControl serverControl = new ServerControl(plugin);
        serverControl.applyGameRulesToAllWorlds();

        String suffix = ruleName.equals("PlayersSleepingPercentage") ? "%" : "";
        player.sendMessage("§6[GameRules] " + ruleName + ": §e" + newValue + suffix);
        open(player);
    }

    
    private void openWorldListMenu(Player player, String ruleName) {
        Inventory inv = Bukkit.createInventory(null, 45, "§8§l▬ §b§l🌍 " + ruleName + " §8│ §7Mundos §8§l▬");

        ItemStack headerBorder = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        ItemStack footerBorder = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        ItemStack sideBorder = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        ItemStack fillerItem = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");

        String basePath = "GameRules." + ruleName;
        boolean useWhitelist = plugin.getConfig().getBoolean(basePath + ".Whitelist", false);
        java.util.List<String> worldList = plugin.getConfig().getStringList(basePath + ".Worlds");

        
        for (int i = 0; i < 9; i++)
            inv.setItem(i, headerBorder);

        inv.setItem(4, createToggleItem(
                Material.WRITABLE_BOOK,
                useWhitelist ? "§a§l✓ Modo Whitelist §8│ §eATIVO" : "§c§l✗ Modo Global §8│ §7TODOS",
                useWhitelist,
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§f§lModo atual: " + (useWhitelist ? "§aWhitelist" : "§eGlobal"),
                "§7",
                useWhitelist ? "§7▸ Regra aplicada §eAPENAS§7 nos mundos" : "§7▸ Regra aplicada em §eTODOS§7 os mundos",
                useWhitelist ? "§7  marcados com §a✓ §7abaixo" : "§7  do servidor automaticamente",
                "§7",
                "§f§lEfeito da mudança:",
                useWhitelist ? "§8→ §7Desativar = §eaplica em todos" : "§8→ §7Ativar = §eaplica só marcados",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§e§l➤ Clique para alternar modo"));

        
        java.util.List<org.bukkit.World> worlds = Bukkit.getWorlds();
        int slot = 10; 
        int worldCount = 0;

        for (org.bukkit.World world : worlds) {
            if (worldCount >= 18)
                break; 

            String worldName = world.getName();
            boolean isInList = worldList.contains(worldName);

            
            Material icon;
            if (isInList) {
                icon = Material.LIME_CONCRETE;
            } else {
                icon = Material.RED_CONCRETE;
            }

            String status = isInList ? "§a§l✓ MARCADO" : "§7§l○ DESMARCADO";

            String aplicacao;
            if (useWhitelist) {
                aplicacao = isInList ? "§a▸ Regra SERÁ aplicada" : "§c▸ Regra NÃO será aplicada";
            } else {
                aplicacao = "§e▸ Regra aplicada (modo global)";
            }

            inv.setItem(slot, createItem(
                    icon,
                    (isInList ? "§a§l✓ " : "§7") + "§f" + worldName,
                    "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                    "§7Status: " + status,
                    "§7",
                    aplicacao,
                    "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                    "§e§l➤ Clique para " + (isInList ? "§cremover" : "§aadicionar")));

            slot++;
            worldCount++;

            
            if (slot % 9 == 0)
                slot += 1; 
            if (slot % 9 == 8)
                slot += 2; 
        }

        
        for (int i = 36; i < 45; i++)
            inv.setItem(i, footerBorder);

        inv.setItem(36, createItem(Material.EMERALD, "§a§l✓ Aplicar Agora",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§7Força aplicação imediata",
                "§7da regra em todos os mundos",
                "§7",
                "§e§l➤ Clique para aplicar"));

        inv.setItem(40, createItem(Material.ARROW, "§f§l« Voltar",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§7Retornar ao menu de GameRules",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§e§l➤ Clique para voltar"));

        inv.setItem(44, createItem(Material.KNOWLEDGE_BOOK, "§b§lℹ Informações",
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "§f§lRegra: §e" + ruleName,
                "§f§lMundos carregados: §e" + worlds.size(),
                "§f§lMundos marcados: §e" + worldList.size(),
                "§f§lModo: " + (useWhitelist ? "§aWhitelist" : "§eGlobal"),
                "§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        inv.setItem(9, sideBorder);
        inv.setItem(17, sideBorder);
        inv.setItem(18, sideBorder);
        inv.setItem(26, sideBorder);
        inv.setItem(27, sideBorder);
        inv.setItem(35, sideBorder);

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, fillerItem);
            }
        }
        player.openInventory(inv);
    }

    public void handleWorldListClick(Player player, ItemStack clicked, String title) {
        String displayName = clicked.getItemMeta().getDisplayName();

        String ruleName = title.replace("§8§l▬ §b§l🌍 ", "")
                .replace(" §8│ §7Mundos §8§l▬", "")
                .trim();
        String basePath = "GameRules." + ruleName;

        if (displayName.contains("Modo Whitelist") || displayName.contains("Modo Global")) {
            boolean current = plugin.getConfig().getBoolean(basePath + ".Whitelist");
            plugin.getConfig().set(basePath + ".Whitelist", !current);
            plugin.saveConfig();

            ServerControl serverControl = new ServerControl(plugin);
            serverControl.applyGameRulesToAllWorlds();

            player.sendMessage("§a§l✓ §6[GameRules] §fModo alterado!");
            player.sendMessage(
                    "§7▸ Modo: " + (!current ? "§aWhitelist §7(só mundos marcados)" : "§eGlobal §7(todos os mundos)"));
            player.sendMessage("§7▸ Regra: §e" + ruleName);
            player.sendMessage("§a✓ Aplicado imediatamente!");

            openWorldListMenu(player, ruleName);
        } else if (displayName.contains("Aplicar Agora") || displayName.contains("Aplicar Mudanças")) {
            ServerControl serverControl = new ServerControl(plugin);
            serverControl.applyGameRulesToAllWorlds();

            player.sendMessage("§a§l✓ §6[GameRules] §fConfigurações aplicadas!");
            player.sendMessage("§7▸ Mundos atualizados: §e" + Bukkit.getWorlds().size());
            player.closeInventory();
        } else if (displayName.contains("Voltar")) {
            open(player);
        } else {
            String worldName = org.bukkit.ChatColor.stripColor(displayName)
                    .replace("✓ ", "")
                    .trim();

            java.util.List<String> worldList = plugin.getConfig().getStringList(basePath + ".Worlds");

            if (worldList.contains(worldName)) {
                worldList.remove(worldName);
                player.sendMessage("§c§l✗ §7Mundo §f" + worldName + " §cremovido da lista");
            } else {
                worldList.add(worldName);
                player.sendMessage("§a§l✓ §7Mundo §f" + worldName + " §aadicionado à lista");
            }

            plugin.getConfig().set(basePath + ".Worlds", worldList);
            plugin.saveConfig();

            ServerControl serverControl = new ServerControl(plugin);
            serverControl.applyGameRulesToAllWorlds();
            player.sendMessage("§e⚡ Aplicado imediatamente em todos os mundos!");

            openWorldListMenu(player, ruleName);
        }
    }
}
