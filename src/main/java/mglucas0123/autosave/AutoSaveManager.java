package mglucas0123.autosave;

import mglucas0123.Principal;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSaveManager {
    
    private Principal plugin;
    private BukkitRunnable saveTask;
    private int intervalSeconds;
    private boolean broadcastSave;
    private boolean saveEnabled;
    
    public AutoSaveManager(Principal plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    private void loadConfig() {
        this.intervalSeconds = plugin.getConfig().getInt("AutoSave.IntervalSeconds", 300);
        this.broadcastSave = plugin.getConfig().getBoolean("AutoSave.BroadcastMessage", true);
        this.saveEnabled = plugin.getConfig().getBoolean("AutoSave.Enabled", true);
    }
    
    public void start() {
        if (!saveEnabled) {
            return;
        }
        
        if (saveTask != null) {
            saveTask.cancel();
        }
        
        long intervalTicks = intervalSeconds * 20L;
        
        saveTask = new BukkitRunnable() {
            @Override
            public void run() {
                performAutoSave();
            }
        };
        
        saveTask.runTaskTimer(plugin, intervalTicks, intervalTicks);
        
        Bukkit.getConsoleSender().sendMessage("§a[MGZ] AutoSave iniciado! Intervalo: " + intervalSeconds + "s");
    }
    
    public void performAutoSave() {
        if (broadcastSave) {
            Bukkit.broadcastMessage("§e[AutoSave] §7Salvando servidor...");
        }
        
        Bukkit.getConsoleSender().sendMessage("§7[AutoSave] Salvando jogadores...");
        Bukkit.savePlayers();
        
        Bukkit.getConsoleSender().sendMessage("§7[AutoSave] Salvando mundos...");
        for (World world : Bukkit.getWorlds()) {
            try {
                world.save();
                Bukkit.getConsoleSender().sendMessage("§7[AutoSave] - Mundo '" + world.getName() + "' salvo");
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("§c[AutoSave] Erro ao salvar mundo '" + world.getName() + "': " + e.getMessage());
            }
        }
        
        if (broadcastSave) {
            Bukkit.broadcastMessage("§a[AutoSave] §7Servidor salvo com sucesso!");
        }
        
        Bukkit.getConsoleSender().sendMessage("§a[AutoSave] Salvamento automático concluído!");
    }
    
    public void stop() {
        if (saveTask != null) {
            saveTask.cancel();
            saveTask = null;
            Bukkit.getConsoleSender().sendMessage("§c[MGZ] AutoSave desativado!");
        }
    }
    
    public void reload() {
        stop();
        loadConfig();
        start();
    }
    
    public boolean isEnabled() {
        return saveEnabled;
    }
}
