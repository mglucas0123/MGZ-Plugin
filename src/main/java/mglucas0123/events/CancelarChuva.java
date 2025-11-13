package mglucas0123.events;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import mglucas0123.Principal;

public class CancelarChuva implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        boolean desativarChuva = Principal.plugin.getConfig().getBoolean("Chuva.Desativar");

        if (!desativarChuva) {
            return;
        }

        World world = e.getWorld();
        boolean toRain = e.toWeatherState();

        if (toRain) {
            e.setCancelled(true);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (world.hasStorm()) {
                        world.setStorm(false);
                        world.setWeatherDuration(6000);
                    }
                }
            }.runTaskLater(Principal.plugin, 1L);
        }
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent e) {
        boolean desativarChuva = Principal.plugin.getConfig().getBoolean("Chuva.Desativar");

        if (!desativarChuva) {
            return;
        }

        World world = e.getWorld();
        boolean toThunder = e.toThunderState();

        if (toThunder) {
            e.setCancelled(true);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (world.isThundering()) {
                        world.setThundering(false);
                        world.setThunderDuration(6000);
                    }
                }
            }.runTaskLater(Principal.plugin, 1L);
        }
    }
}
