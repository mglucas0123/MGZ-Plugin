package mglucas0123.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.apache.commons.lang3.StringUtils;

public class LimparChat
        implements CommandExecutor {
	
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("LimparChat")) {
            if (sender.hasPermission("mgz.limparchat") || sender.hasPermission("mgz.*")) {
                if (args.length == 0) {
                    String limparchat = StringUtils.repeat(" §c \n §c ", 100);
                    Bukkit.broadcastMessage(limparchat);
                    Bukkit.broadcastMessage("§6[§e!§6] §e" + sender.getName() + " §aLimpou o chat.");
                }
                return true;
            } else {
                sender.sendMessage("§6[§e!§6] §cVocê não tem acesso a esse comando.");
            }
        }
        return true;
    }
}
