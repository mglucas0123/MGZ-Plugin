package mglucas0123.events;

import mglucas0123.Principal;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.List;

public class ServerControl implements Listener {
    
    private Principal plugin;
    
    public ServerControl(Principal plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onWorldLoad(WorldLoadEvent event) {
        applyGameRules(event.getWorld());
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        boolean mostrarMorte = plugin.getConfig().getBoolean("ChatControl.MostrarMorte", true);
        
        if (!mostrarMorte) {
            event.setDeathMessage(null);
        }
    }
    
    public void applyGameRules(World world) {
        String worldName = world.getName();
        
        applyBooleanGameRule(world, worldName, "KeepInventory", GameRule.KEEP_INVENTORY);
        applyBooleanGameRule(world, worldName, "AnnounceAdvancements", GameRule.ANNOUNCE_ADVANCEMENTS);
        applyBooleanGameRule(world, worldName, "CommandBlockOutput", GameRule.COMMAND_BLOCK_OUTPUT);
        applyBooleanGameRule(world, worldName, "MobGriefing", GameRule.MOB_GRIEFING);
        applyBooleanGameRule(world, worldName, "DoInsomnia", GameRule.DO_INSOMNIA);
        applyBooleanGameRule(world, worldName, "DoDaylightCycle", GameRule.DO_DAYLIGHT_CYCLE);
        applyBooleanGameRule(world, worldName, "DoImmediateRespawn", GameRule.DO_IMMEDIATE_RESPAWN);
        applyBooleanGameRule(world, worldName, "DoFireTick", GameRule.DO_FIRE_TICK);
        
        applyIntegerGameRule(world, worldName, "PlayersSleepingPercentage", GameRule.PLAYERS_SLEEPING_PERCENTAGE);
        applyIntegerGameRule(world, worldName, "RandomTickSpeed", GameRule.RANDOM_TICK_SPEED);
        applyIntegerGameRule(world, worldName, "MaxEntityCramming", GameRule.MAX_ENTITY_CRAMMING);
    }
    
    private void applyBooleanGameRule(World world, String worldName, String ruleName, GameRule<Boolean> gameRule) {
        String basePath = "GameRules." + ruleName;
        
        boolean useWhitelist = plugin.getConfig().getBoolean(basePath + ".Whitelist", false);
        List<String> worldList = plugin.getConfig().getStringList(basePath + ".Worlds");
        boolean enabledValue = plugin.getConfig().getBoolean(basePath + ".Enabled", false);
        
        if (!useWhitelist) {
            world.setGameRule(gameRule, enabledValue);
        } else {
            if (worldList.contains(worldName)) {
                world.setGameRule(gameRule, enabledValue);
            } else {
                world.setGameRule(gameRule, !enabledValue);
            }
        }
    }
    
    private void applyIntegerGameRule(World world, String worldName, String ruleName, GameRule<Integer> gameRule) {
        String basePath = "GameRules." + ruleName;
        
        boolean useWhitelist = plugin.getConfig().getBoolean(basePath + ".Whitelist", false);
        List<String> worldList = plugin.getConfig().getStringList(basePath + ".Worlds");
        int configuredValue = plugin.getConfig().getInt(basePath + ".Value", 0);
        
        if (!useWhitelist) {
            world.setGameRule(gameRule, configuredValue);
        } else {
            if (worldList.contains(worldName)) {
                world.setGameRule(gameRule, configuredValue);
            } else {
            }
        }
    }
    
    public void applyGameRulesToAllWorlds() {
        Bukkit.getConsoleSender().sendMessage("§7[§6MGZ§7] §fAplicando GameRules em todos os mundos...");
        
        for (World world : Bukkit.getWorlds()) {
            applyGameRules(world);
            
            String keepInv = world.getGameRuleValue(GameRule.KEEP_INVENTORY) ? "§aAtivado" : "§cDesativado";
            String announceAdv = world.getGameRuleValue(GameRule.ANNOUNCE_ADVANCEMENTS) ? "§aAtivado" : "§cDesativado";
            String mobGrief = world.getGameRuleValue(GameRule.MOB_GRIEFING) ? "§aAtivado" : "§cDesativado";
            String doInsomnia = world.getGameRuleValue(GameRule.DO_INSOMNIA) ? "§aAtivado" : "§cDesativado";
            String doDaylight = world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE) ? "§aAtivado" : "§cDesativado";
            String doRespawn = world.getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN) ? "§aAtivado" : "§cDesativado";
            String doFire = world.getGameRuleValue(GameRule.DO_FIRE_TICK) ? "§aAtivado" : "§cDesativado";
            
            Integer sleepPercent = world.getGameRuleValue(GameRule.PLAYERS_SLEEPING_PERCENTAGE);
            Integer tickSpeed = world.getGameRuleValue(GameRule.RANDOM_TICK_SPEED);
            Integer maxCram = world.getGameRuleValue(GameRule.MAX_ENTITY_CRAMMING);
            
            Bukkit.getConsoleSender().sendMessage("§7[§6MGZ§7] §fMundo §e" + world.getName() + "§7:");
            Bukkit.getConsoleSender().sendMessage("  §8▪ §7KeepInventory: " + keepInv);
            Bukkit.getConsoleSender().sendMessage("  §8▪ §7AnnounceAdvancements: " + announceAdv);
            Bukkit.getConsoleSender().sendMessage("  §8▪ §7MobGriefing: " + mobGrief);
            Bukkit.getConsoleSender().sendMessage("  §8▪ §7DoInsomnia: " + doInsomnia);
            Bukkit.getConsoleSender().sendMessage("  §8▪ §7DoDaylightCycle: " + doDaylight);
            Bukkit.getConsoleSender().sendMessage("  §8▪ §7DoImmediateRespawn: " + doRespawn);
            Bukkit.getConsoleSender().sendMessage("  §8▪ §7DoFireTick: " + doFire);
            Bukkit.getConsoleSender().sendMessage("  §8▪ §7PlayersSleepingPercentage: §b" + sleepPercent + "%");
            Bukkit.getConsoleSender().sendMessage("  §8▪ §7RandomTickSpeed: §b" + tickSpeed);
            Bukkit.getConsoleSender().sendMessage("  §8▪ §7MaxEntityCramming: §b" + maxCram);
        }
        
        Bukkit.getConsoleSender().sendMessage("§7[§6MGZ§7] §aGameRules aplicadas com sucesso!");
    }
}
