package mglucas0123.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mglucas0123.Principal;

public class Anuncio
        implements CommandExecutor {

    Principal Main = Principal.plugin;
    public static ArrayList<Player> DelayList = new ArrayList<Player>();
    public static int Delay = 0;
    public static int DelayReport;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        final Player p = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("Anuncio")) {
            if (sender.hasPermission("mgz.anuncio") || p.isOp() || p.hasPermission("mgz.*")) {
                if (args.length == 0) {
                    sender.sendMessage("§6[§e!§6] §cPorfavor digite uma Mensagem.");
                } else {
                    String msg = "";
                    for (int i = 0; i < args.length; i++) {
                        msg = msg + args[i] + " ";
                    }
                    if (!DelayList.contains(p)) {
                        DelayList.add(p);
                        if (sender.hasPermission("mgz.anuncio.cooldown") || p.isOp() || sender.hasPermission("mgz.*")) {
                            DelayList.remove(p);
                        }
                        Bukkit.broadcastMessage("                                                                ");
                        Bukkit.broadcastMessage("§e§l[ANUNCIO] - §a§l" + sender.getName() + ": " + "§d§l" + msg);
                        Bukkit.broadcastMessage("                                                                ");

                        Delay = Main.getConfig().getInt("Delays.Anuncio");
                        DelayReport = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main, new Runnable() {
                            public void run() {
                                Delay -= 1;
                                if (Delay == 0) {
                                    Delay = 0;
                                    DelayList.remove(p);
                                    Bukkit.getScheduler().cancelTask(DelayReport);
                                }
                            }
                        }, 0L, 20L);
                    } else {
                        p.sendMessage("§6[§e!§6] §cAguarde §a" + Delay + " §cSegundos para usar esse comando novamente.");
                    }
                }
            } else {
                sender.sendMessage("§6[§e!§6] §cVoce nao tem acesso a esse comando.");
            }
        }
        return false;
    }
}
