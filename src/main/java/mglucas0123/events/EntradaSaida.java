package mglucas0123.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import mglucas0123.Principal;

public class EntradaSaida implements Listener {

    @EventHandler
    public void Entrar(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String premsg = Principal.plugin.getConfig().getString("Entrada/Saida.Entrada");
        premsg = premsg.replace("{player}", p.getName());
        premsg = premsg.replace("&", "§");
        String msg = premsg;
        e.setJoinMessage(msg);
    }

    @EventHandler
    public void Sair(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String premsg = Principal.plugin.getConfig().getString("Entrada/Saida.Saida");
        premsg = premsg.replace("{player}", p.getName());
        premsg = premsg.replace("&", "§");
        String msg = premsg;
        e.setQuitMessage(msg);
    }
}
