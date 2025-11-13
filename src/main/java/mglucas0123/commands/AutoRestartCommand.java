package mglucas0123.commands;

import mglucas0123.Principal;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AutoRestartCommand implements CommandExecutor {
    
    private Principal plugin;
    
    public AutoRestartCommand(Principal plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("mgz.autorestart")) {
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
                sender.sendMessage("§c[AutoRestart] Iniciando reinício imediato...");
                plugin.getAutoRestartManager().performRestart(true);
                break;
                
            case "countdown":
                sender.sendMessage("§c[AutoRestart] Iniciando reinício com contagem regressiva...");
                plugin.getAutoRestartManager().performRestart(false);
                break;
                
            case "reload":
                plugin.getAutoRestartManager().reload();
                sender.sendMessage("§a[AutoRestart] Configuração recarregada!");
                break;
                
            case "info":
                sender.sendMessage("§6§l========== AutoRestart Info ==========");
                sender.sendMessage("§eStatus: " + (plugin.getAutoRestartManager().isEnabled() ? "§aAtivo" : "§cInativo"));
                sender.sendMessage("§eHorários: §f" + String.join(", ", plugin.getConfig().getStringList("AutoRestart.Times")));
                sender.sendMessage("§eContagem: " + (plugin.getConfig().getBoolean("AutoRestart.Countdown.Enabled") ? "§aAtiva" : "§cInativa"));
                sender.sendMessage("§6§l======================================");
                break;
                
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l========== AutoRestart Commands ==========");
        sender.sendMessage("§e/autorestart now §7- Reinicia imediatamente");
        sender.sendMessage("§e/autorestart countdown §7- Reinicia com contagem");
        sender.sendMessage("§e/autorestart reload §7- Recarrega config");
        sender.sendMessage("§e/autorestart info §7- Mostra informações");
        sender.sendMessage("§6§l==========================================");
    }
}
