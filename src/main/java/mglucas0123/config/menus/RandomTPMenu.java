package mglucas0123.config.menus;

import mglucas0123.Principal;
import mglucas0123.config.ConfigEditorGUI;
import mglucas0123.config.GUITemplate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomTPMenu extends BaseMenu {
    
    private ConfigEditorGUI editorGUI;
    
    public RandomTPMenu(Principal plugin, ConfigEditorGUI editorGUI) {
        super(plugin);
        this.editorGUI = editorGUI;
    }
    
    @Override
    public void open(Player player) {
        GUITemplate template = loadTemplate("RandomTP", 54);
        Inventory inv = Bukkit.createInventory(null, 54, "§8§l▬▬▬▬▬ §d§l🌀 Random TP 🌀 §8§l▬▬▬▬▬");
        
        String world = plugin.getConfig().getString("RTP.World", "world");
        int radius = plugin.getConfig().getInt("RTP.Radius", 10000);
        int delay = plugin.getConfig().getInt("Delays.RTP", 60);
        
        
        ItemStack header = createItem(Material.ENDER_PEARL, "§d§l🌀 RANDOM TELEPORT",
            "§8§m─────────────────────────────",
            "§7Sistema de teleporte aleatório",
            "§7",
            "§f§lStatus atual:",
            "§8▸ §fMundo: §e" + world,
            "§8▸ §fRaio: §e" + formatDistance(radius),
            "§8▸ §fÁrea: §e" + formatArea(radius),
            "§8▸ §fDelay: §e" + delay + "s",
            "§8§m─────────────────────────────");
        inv.setItem(4, header);
        
        
        ItemStack accentGlass = createItem(Material.PURPLE_STAINED_GLASS_PANE, "§d", "");
        inv.setItem(3, accentGlass);
        inv.setItem(5, accentGlass);
        
        
        inv.setItem(10, createItem(Material.GRASS_BLOCK, "§e§l� Mundo de Destino",
            "§8§m─────────────────────────────",
            "§7Escolha o mundo para Random TP",
            "§7",
            "§f§lMundo atual: §e" + world,
            "§7",
            "§f§lMundos disponíveis:",
            "§8▸ §fworld §8(Sobrevivência)",
            "§8▸ §fworld_nether §8(Nether)",
            "§8▸ §fworld_the_end §8(The End)",
            "§8§m─────────────────────────────",
            "§a§l◀ Esq §f- world",
            "§c§l▶ Dir §f- world_nether",
            "§9§l⇧ Shift §f- world_the_end"));
        
        inv.setItem(11, createItem(Material.HOPPER, "§e§l⏳ Delay do Comando",
            "§8§m─────────────────────────────",
            "§7Cooldown entre teleportes",
            "§7",
            "§f§lTempo atual: §e" + delay + "s",
            "§7",
            "§f§lRecomendações:",
            "§8▸ §f0s §8- Sem cooldown (abusável)",
            "§8▸ §f30s §8- Equilibrado",
            "§8▸ §f60s §8- Moderado (recomendado)",
            "§8▸ §f120s+ §8- Restritivo",
            "§8§m─────────────────────────────",
            "§c§l◀ Esq §f-30s  §8│  §a§l▶ Dir §f+30s"));
        
        inv.setItem(12, createItem(Material.ENDER_EYE, "§e§l🎯 Testar Random TP",
            "§8§m─────────────────────────────",
            "§7Executa um teleporte de teste",
            "§7",
            "§f§lInformações:",
            "§8▸ §fTeleporta VOCÊ agora",
            "§8▸ §fIgnora delay",
            "§8▸ §fMostra coordenadas finais",
            "§8▸ §fBusca local seguro",
            "§8§m─────────────────────────────",
            "§e§l➤ Clique para testar"));
        
        
        inv.setItem(19, createPresetRadiusItem(5000, radius, Material.WOODEN_SWORD,
            "§e§l📏 Raio: 5.000 blocos",
            "§7Área pequena, rápida exploração",
            "§7",
            "§f§lCaracterísticas:",
            "§8▸ §fÁrea: 78,5 milhões blocos²",
            "§8▸ §fTempo explorar: ~1 hora",
            "§8▸ §fRecomendado: Servidores pequenos",
            "§8▸ §fJogadores próximos"));
        
        inv.setItem(20, createPresetRadiusItem(10000, radius, Material.STONE_SWORD,
            "§e§l📏 Raio: 10.000 blocos",
            "§7Área média, boa dispersão",
            "§7",
            "§f§lCaracterísticas:",
            "§8▸ §fÁrea: 314 milhões blocos²",
            "§8▸ §fTempo explorar: ~3 horas",
            "§8▸ §fRecomendado: Uso geral",
            "§8▸ §fEquilíbrio ideal"));
        
        inv.setItem(21, createPresetRadiusItem(20000, radius, Material.IRON_SWORD,
            "§e§l📏 Raio: 20.000 blocos",
            "§7Área grande, muita exploração",
            "§7",
            "§f§lCaracterísticas:",
            "§8▸ §fÁrea: 1,25 bilhões blocos²",
            "§8▸ §fTempo explorar: ~10 horas",
            "§8▸ §fRecomendado: Servidores médios",
            "§8▸ §fMaior dispersão"));
        
        inv.setItem(22, createPresetRadiusItem(50000, radius, Material.GOLDEN_SWORD,
            "§e§l📏 Raio: 50.000 blocos",
            "§7Área enorme, vastíssimo",
            "§7",
            "§f§lCaracterísticas:",
            "§8▸ §fÁrea: 7,85 bilhões blocos²",
            "§8▸ §fTempo explorar: ~30 horas",
            "§8▸ §fRecomendado: Servidores grandes",
            "§8▸ §fMáxima dispersão"));
        
        inv.setItem(23, createPresetRadiusItem(100000, radius, Material.DIAMOND_SWORD,
            "§e§l📏 Raio: 100.000 blocos",
            "§7Área massiva, exploração infinita",
            "§7",
            "§f§lCaracterísticas:",
            "§8▸ §fÁrea: 31,4 bilhões blocos²",
            "§8▸ §fTempo explorar: ~100 horas",
            "§8▸ §fRecomendado: Networks",
            "§8▸ §fExploração extrema"));
        
        
        inv.setItem(28, createItem(Material.RED_CONCRETE, "§c§l➖ -5.000 blocos",
            "§8§m─────────────────────────────",
            "§7Diminui o raio em 5.000",
            "§7",
            "§fRaio atual: §e" + formatDistance(radius),
            "§fNovo raio: §e" + formatDistance(Math.max(1000, radius - 5000)),
            "§8§m─────────────────────────────",
            "§c§l➤ Clique para diminuir"));
        
        inv.setItem(29, createItem(Material.ORANGE_CONCRETE, "§6§l➖ -1.000 blocos",
            "§8§m─────────────────────────────",
            "§7Diminui o raio em 1.000",
            "§7",
            "§fRaio atual: §e" + formatDistance(radius),
            "§fNovo raio: §e" + formatDistance(Math.max(1000, radius - 1000)),
            "§8§m─────────────────────────────",
            "§6§l➤ Clique para diminuir"));
        
        inv.setItem(30, createItem(Material.FILLED_MAP, "§b§l🗺 Raio Atual",
            "§8§m─────────────────────────────",
            "§7Raio de teleporte configurado",
            "§7",
            "§f§lInformações:",
            "§8▸ §fRaio: §e" + formatDistance(radius),
            "§8▸ §fDiâmetro: §e" + formatDistance(radius * 2),
            "§8▸ §fÁrea total: §e" + formatArea(radius),
            "§8▸ §fPerímetro: §e" + formatDistance(radius * 2 * 3),
            "§8§m─────────────────────────────",
            "§7§oUse os botões para ajustar"));
        
        inv.setItem(31, createItem(Material.LIME_CONCRETE, "§a§l➕ +1.000 blocos",
            "§8§m─────────────────────────────",
            "§7Aumenta o raio em 1.000",
            "§7",
            "§fRaio atual: §e" + formatDistance(radius),
            "§fNovo raio: §e" + formatDistance(radius + 1000),
            "§8§m─────────────────────────────",
            "§a§l➤ Clique para aumentar"));
        
        inv.setItem(32, createItem(Material.GREEN_CONCRETE, "§2§l➕ +5.000 blocos",
            "§8§m─────────────────────────────",
            "§7Aumenta o raio em 5.000",
            "§7",
            "§fRaio atual: §e" + formatDistance(radius),
            "§fNovo raio: §e" + formatDistance(radius + 5000),
            "§8§m─────────────────────────────",
            "§2§l➤ Clique para aumentar"));
        
        
        inv.setItem(45, createItem(template.getMaterial("title_icon"), "§e§l📊 Estatísticas",
            "§8§m─────────────────────────────",
            "§7Dados sobre o sistema RTP",
            "§7",
            "§f§lInformações gerais:",
            "§8▸ §fMundo: §e" + world,
            "§8▸ §fRaio: §e" + formatDistance(radius),
            "§8▸ §fÁrea: §e" + formatArea(radius),
            "§8▸ §fDelay: §e" + delay + "s",
            "§7",
            "§f§lEstimativas:",
            "§8▸ §fTeleportes/dia: §e~" + estimateTPsPerDay(delay),
            "§8▸ §fPossibilidades: §e~" + formatLargeNumber(calculatePossibilities(radius)),
            "§8§m─────────────────────────────"));
        
        inv.setItem(46, createItem(template.getMaterial("info_button"), "§b§l❓ Ajuda",
            "§8§m─────────────────────────────",
            "§7Como funciona o Random TP",
            "§7",
            "§f§lSobre o sistema:",
            "§8▸ §fTeleporta para local aleatório",
            "§8▸ §fDentro do raio configurado",
            "§8▸ §fBusca superfície segura",
            "§8▸ §fEvita água, lava, void",
            "§7",
            "§f§lDica de uso:",
            "§8▸ §fRaios maiores = mais dispersão",
            "§8▸ §fDelay alto = menos spam",
            "§8▸ §fTeste antes de liberar",
            "§8§m─────────────────────────────"));
        
        inv.setItem(48, createItem(Material.BARRIER, "§c§l⟲ Resetar Padrões",
            "§8§m─────────────────────────────",
            "§7Restaura configurações originais",
            "§7",
            "§f§lValores padrão:",
            "§8▸ §fMundo: §eworld",
            "§8▸ §fRaio: §e10.000 blocos",
            "§8▸ §fDelay: §e60s",
            "§8§m─────────────────────────────",
            "§c§l➤ Clique para resetar"));
        
        inv.setItem(49, createItem(template.getMaterial("back_button"), "§7§l« Voltar",
            "§8§m─────────────────────────────",
            "§7Retornar ao menu principal",
            "§8§m─────────────────────────────",
            "§e§l➤ Clique para voltar"));
        
        inv.setItem(50, createItem(template.getMaterial("confirm_button"), "§a§l✔ Aplicar Mudanças",
            "§8§m─────────────────────────────",
            "§7Salva todas as configurações",
            "§7",
            "§f§lMudanças pendentes:",
            "§8▸ §fMundo: §e" + world,
            "§8▸ §fRaio: §e" + formatDistance(radius),
            "§8▸ §fDelay: §e" + delay + "s",
            "§8§m─────────────────────────────",
            "§a§l➤ Clique para salvar"));
        
        
        ItemStack blackGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, "§8", "");
        for (int i = 0; i < 9; i++) if (inv.getItem(i) == null) inv.setItem(i, blackGlass);
        for (int i = 45; i < 54; i++) if (inv.getItem(i) == null) inv.setItem(i, blackGlass);
        for (int i : new int[]{9, 18, 27, 36, 17, 26, 35, 44}) inv.setItem(i, blackGlass);
        
        player.openInventory(inv);
    }
    
    public void handleClick(Player player, ItemStack clicked, InventoryClickEvent event) {
        String displayName = clicked.getItemMeta().getDisplayName();
        
        
        if (displayName.contains("Mundo de Destino")) {
            String newWorld;
            if (event.isShiftClick()) {
                newWorld = "world_the_end";
            } else if (event.isLeftClick()) {
                newWorld = "world";
            } else {
                newWorld = "world_nether";
            }
            
            plugin.getConfig().set("RTP.World", newWorld);
            plugin.saveConfig();
            player.sendMessage("§d§l🌀 §e[Random TP] §7Mundo alterado para: §f" + newWorld);
            open(player);
            
        
        } else if (displayName.contains("Delay do Comando")) {
            int current = plugin.getConfig().getInt("Delays.RTP");
            int newValue;
            
            if (event.isLeftClick()) {
                newValue = Math.max(0, current - 30);
            } else {
                newValue = current + 30;
            }
            
            plugin.getConfig().set("Delays.RTP", newValue);
            plugin.saveConfig();
            player.sendMessage("§d§l🌀 §e[Random TP] §7Delay alterado para: §f" + newValue + "s");
            open(player);
            
        
        } else if (displayName.contains("Testar Random TP")) {
            int radius = plugin.getConfig().getInt("RTP.Radius", 10000);
            String worldName = plugin.getConfig().getString("RTP.World", "world");
            World world = Bukkit.getWorld(worldName);
            
            if (world == null) {
                player.sendMessage("§d§l🌀 §c[Random TP] §7Mundo §f" + worldName + " §7não existe!");
                return;
            }
            
            Random random = new Random();
            int x = random.nextInt(radius * 2) - radius;
            int z = random.nextInt(radius * 2) - radius;
            int y = world.getHighestBlockYAt(x, z);
            
            Location loc = new Location(world, x, y, z);
            player.teleport(loc);
            
            player.sendMessage("§d§l🌀 §e[Random TP] §7Teleportado para teste!");
            player.sendMessage("§7Coordenadas: §fX: " + x + " §8│ §fY: " + y + " §8│ §fZ: " + z);
            player.sendMessage("§7Distância: §f" + formatDistance((int) Math.sqrt(x*x + z*z)));
            player.closeInventory();
            
        
        } else if (displayName.contains("Raio: 5.000 blocos")) {
            setRadius(player, 5000);
        } else if (displayName.contains("Raio: 10.000 blocos")) {
            setRadius(player, 10000);
        } else if (displayName.contains("Raio: 20.000 blocos")) {
            setRadius(player, 20000);
        } else if (displayName.contains("Raio: 50.000 blocos")) {
            setRadius(player, 50000);
        } else if (displayName.contains("Raio: 100.000 blocos")) {
            setRadius(player, 100000);
            
        
        } else if (displayName.contains("-5.000 blocos")) {
            int current = plugin.getConfig().getInt("RTP.Radius", 10000);
            int newValue = Math.max(1000, current - 5000);
            setRadius(player, newValue);
            
        } else if (displayName.contains("-1.000 blocos")) {
            int current = plugin.getConfig().getInt("RTP.Radius", 10000);
            int newValue = Math.max(1000, current - 1000);
            setRadius(player, newValue);
            
        } else if (displayName.contains("+1.000 blocos")) {
            int current = plugin.getConfig().getInt("RTP.Radius", 10000);
            int newValue = current + 1000;
            setRadius(player, newValue);
            
        } else if (displayName.contains("+5.000 blocos")) {
            int current = plugin.getConfig().getInt("RTP.Radius", 10000);
            int newValue = current + 5000;
            setRadius(player, newValue);
            
        
        } else if (displayName.contains("Resetar Padrões")) {
            plugin.getConfig().set("RTP.World", "world");
            plugin.getConfig().set("RTP.Radius", 10000);
            plugin.getConfig().set("Delays.RTP", 60);
            plugin.saveConfig();
            
            player.sendMessage("§d§l🌀 §e[Random TP] §7Configurações resetadas para padrão!");
            open(player);
            
        
        } else if (displayName.contains("Aplicar Mudanças")) {
            plugin.saveConfig();
            player.sendMessage("§d§l🌀 §a[Random TP] §7Configurações salvas com sucesso!");
            player.sendMessage("§7Sistema atualizado e pronto para uso.");
            
        
        } else if (displayName.contains("Voltar")) {
            editorGUI.openMainMenu(player);
        }
    }
    
    
    private void setRadius(Player player, int radius) {
        plugin.getConfig().set("RTP.Radius", radius);
        plugin.saveConfig();
        
        player.sendMessage("§d§l🌀 §e[Random TP] §7Raio alterado para: §f" + formatDistance(radius));
        player.sendMessage("§7Área de teleporte: §f" + formatArea(radius));
        open(player);
    }
    
    
    private String formatDistance(int blocks) {
        if (blocks >= 1000) {
            return String.format("%,d blocos", blocks).replace(',', '.');
        }
        return blocks + " blocos";
    }
    
    
    private String formatArea(int radius) {
        double area = Math.PI * radius * radius;
        
        if (area >= 1_000_000_000) {
            return String.format("%.1f bilhões blocos²", area / 1_000_000_000);
        } else if (area >= 1_000_000) {
            return String.format("%.1f milhões blocos²", area / 1_000_000);
        } else {
            return String.format("%,d blocos²", (long) area).replace(',', '.');
        }
    }
    
    
    private int estimateTPsPerDay(int delaySeconds) {
        if (delaySeconds == 0) return 999999; 
        int secondsPerDay = 86400;
        return secondsPerDay / delaySeconds;
    }
    
    
    private long calculatePossibilities(int radius) {
        return (long) (Math.PI * radius * radius);
    }
    
    
    private String formatLargeNumber(long number) {
        if (number >= 1_000_000_000) {
            return String.format("%.1f bilhões", number / 1_000_000_000.0);
        } else if (number >= 1_000_000) {
            return String.format("%.1f milhões", number / 1_000_000.0);
        } else {
            return String.format("%,d", number).replace(',', '.');
        }
    }
    
    
    private ItemStack createPresetRadiusItem(int presetRadius, int currentRadius, Material material, String... lore) {
        ItemStack item = createItem(material, "", lore);
        
        
        if (presetRadius == currentRadius) {
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            
            
            List<String> newLore = new ArrayList<>();
            for (String line : lore) {
                newLore.add(line);
            }
            newLore.add("§8§m─────────────────────────────");
            newLore.add("§a§l✔ PRESET ATIVO");
            
            meta = item.getItemMeta();
            meta.setLore(newLore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
}
