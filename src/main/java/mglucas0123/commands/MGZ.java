package mglucas0123.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import mglucas0123.Principal;

public class MGZ
        implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("Mgz")) {
            if (args.length == 0) {
                sender.sendMessage("§b§l===============< MGZ >===============");
                sender.sendMessage("");
                sender.sendMessage("§6§lCOMANDOS DISPONÍVEIS:");
                sender.sendMessage("§e/Anuncio §f- §7Enviar anuncio para o servidor");
                sender.sendMessage("§e/Luz §f- §7Ativar visão noturna");
                sender.sendMessage("§e/LimparChat §f- §7Limpar o chat");
                sender.sendMessage("§e/Repair §f- §7Reparar item na mão");
                sender.sendMessage("§e/Rtp §f- §7Teleporte aleatório pelo mundo");
                sender.sendMessage("");
                sender.sendMessage("§6§lSISTEMAS:");
                sender.sendMessage("§e/Region §f- §7Módulo de proteção de regiões");
                sender.sendMessage("§e/PlayerData §f- §7Gerenciar backups de dados");
                sender.sendMessage("§e/AutoSave §f- §7Sistema de salvamento automático");
                sender.sendMessage("§e/AutoRestart §f- §7Sistema de reinício automático");
                sender.sendMessage("");
                sender.sendMessage("§6§lADMINISTRAÇÃO:");
                sender.sendMessage("§e/MGZ Reload §f- §7Reiniciar configurações");
                sender.sendMessage("§e/MgzReload §f- §7Recarregar plugin completo");
                sender.sendMessage("§e/Mgzconfig §f- §7Ver o editor de configurações");
                sender.sendMessage("");
                sender.sendMessage("§b§l====================================");
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("Reload")) {
                    if (sender.hasPermission("mgz.reload") || sender.isOp() || sender.hasPermission("mgz.*")) {
                        Principal.plugin.reloadConfig();
                        sender.sendMessage("§a§l[MGZ] §aPlugin reiniciado com sucesso!");
                    } else {
                        sender.sendMessage("§c§l[!] §cVocê não tem acesso a esse comando.");
                    }
                    return false;
                }
            }
        }
        return false;
    }
}
