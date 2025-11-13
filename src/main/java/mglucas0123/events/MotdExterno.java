package mglucas0123.events;

import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import mglucas0123.Principal;

public class MotdExterno
        implements Listener {

    @EventHandler
    public void Ping(ServerListPingEvent event) {
        String motd = Principal.plugin.getConfig().getStringList("MOTD.Mensagem").get(new Random().nextInt(Principal.plugin.getConfig().getStringList("MOTD.Mensagem").size())).replace("&", "§");
        event.setMotd(motd);
    }

}
