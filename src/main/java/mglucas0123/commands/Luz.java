package mglucas0123.commands;

import java.util.ArrayList;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import mglucas0123.Principal;

public class Luz
        implements CommandExecutor {

    public static ArrayList<String> Luz = new ArrayList<String>();
    Principal Main = Principal.plugin;

    public String recolorir(String texto) {
        return texto.replace("&", "§");
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {
        Player p = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("Luz")) {
            if (sender.hasPermission("mgz.luz") || p.hasPermission("mgz.*")) {
                if (!Luz.contains(p.getName())) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 5, true));
                    Luz.add(p.getName());
                    p.sendMessage("§6[§e!§6] §aVoce Ativou o /Luz");
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                } else {
                    p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    Luz.remove(p.getName());
                    p.sendMessage("§6[§e!§6] §cVoce Desativou o /Luz");
                    p.playSound(p.getLocation(), Sound.ENTITY_BAT_DEATH, 1.0f, 1.0f);
                }
            } else {
                p.sendMessage("§6[§e!§6] §cVoce nao tem acesso a esse comando.");
            }
        }
        return false;
    }
}
