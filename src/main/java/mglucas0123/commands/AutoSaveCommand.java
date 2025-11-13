package mglucas0123.commands;

import mglucas0123.Principal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AutoSaveCommand implements CommandExecutor {
    
    private Principal plugin;
    
    public AutoSaveCommand(Principal plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("mgz.autosave")) {
            sender.sendMessage("§cVocê não tem permissão para usar este comando!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "now":
            case "save":
                if (plugin.getAutoSaveManager() != null) {
                    sender.sendMessage("§e[AutoSave] Executando salvamento manual...");
                    plugin.getAutoSaveManager().performAutoSave();
                } else {
                    sender.sendMessage("§cAutoSave está desativado!");
                }
                break;
                
            case "reload":
                if (plugin.getAutoSaveManager() != null) {
                    plugin.getAutoSaveManager().reload();
                    sender.sendMessage("§a[AutoSave] Configuração recarregada!");
                } else {
                    sender.sendMessage("§cAutoSave está desativado!");
                }
                break;
                
            case "info":
                if (plugin.getAutoSaveManager() != null) {
                    sender.sendMessage("§6§l========== AutoSave Info ==========");
                    sender.sendMessage("§eStatus: " + (plugin.getAutoSaveManager().isEnabled() ? "§aAtivo" : "§cInativo"));
                    sender.sendMessage("§eIntervalo: §f" + plugin.getConfig().getInt("AutoSave.IntervalSeconds") + " segundos");
                    sender.sendMessage("§eBroadcast: " + (plugin.getConfig().getBoolean("AutoSave.BroadcastMessage") ? "§aSim" : "§cNão"));
                    sender.sendMessage("§6§l==================================");
                } else {
                    sender.sendMessage("§cAutoSave está desativado!");
                }
                break;
                
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l========== AutoSave Commands ==========");
        sender.sendMessage("§e/autosave now §7- Salva agora");
        sender.sendMessage("§e/autosave reload §7- Recarrega config");
        sender.sendMessage("§e/autosave info §7- Mostra informações");
        sender.sendMessage("§6§l======================================");
    }
}
