package mglucas0123.commands;

import mglucas0123.Principal;
import mglucas0123.events.ServerControl;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadConfig implements CommandExecutor {
    
    private Principal plugin;
    
    public ReloadConfig(Principal plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("mgz.admin.reload")) {
            sender.sendMessage("§cVocê não tem permissão para usar este comando!");
            return true;
        }
        
        try {
            
            plugin.reloadConfig();
            
            
            ServerControl serverControl = new ServerControl(plugin);
            serverControl.applyGameRulesToAllWorlds();
            
            sender.sendMessage("§a§l✔ Configurações recarregadas com sucesso!");
            sender.sendMessage("§7Game rules aplicadas em todos os mundos:");
            sender.sendMessage("§7- KeepInventory: §f" + plugin.getConfig().getBoolean("GameRules.KeepInventory"));
            sender.sendMessage("§7- AnnounceAdvancements: §f" + plugin.getConfig().getBoolean("GameRules.AnnounceAdvancements"));
            sender.sendMessage("§7- MostrarMorte: §f" + plugin.getConfig().getBoolean("ChatControl.MostrarMorte"));
            
            
            Bukkit.getConsoleSender().sendMessage("§a[MGZ] Configurações recarregadas por: " + sender.getName());
            
        } catch (Exception e) {
            sender.sendMessage("§cErro ao recarregar configurações!");
            sender.sendMessage("§cDetalhes: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
}
