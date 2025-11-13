package mglucas0123.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mglucas0123.Principal;

public class Rtp implements CommandExecutor {

    private final Principal plugin;
    private final ArrayList<Player> delayList = new ArrayList<>();
    private final Map<Player, Integer> searchTasks = new HashMap<>();
    private int delay = 0;
    private int delayReport;
    
    
    private static final int MAX_ATTEMPTS = 30;
    private static final int MIN_DISTANCE_FROM_SPAWN = 100;

    public Rtp(Principal plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Esse comando nao pode ser usado pelo console");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("mgz.rtp") && !player.hasPermission("mgz.*")) {
            player.sendMessage("§6[§e!§6] §cVocê não tem acesso a esse comando.");
            return true;
        }

        if (delayList.contains(player)) {
            player.sendMessage("§6[§e!§6] §cAguarde " + delay + " segundos para usar esse comando novamente.");
            return true;
        }

        delayList.add(player);
        player.sendMessage("§6[§e!§6] §aProcurando localização segura...");

        
        int taskId = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            searchAndTeleport(player);
        }).getTaskId();

        searchTasks.put(player, taskId);

        delay = plugin.getConfig().getInt("Delays.RTP");
        delayReport = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                delay--;
                if (delay == 0) {
                    delayList.remove(player);
                    Bukkit.getScheduler().cancelTask(delayReport);
                }
            }
        }, 0L, 20L);

        return true;
    }

    private void searchAndTeleport(Player player) {
        World world = Bukkit.getWorld(plugin.getConfig().getString("RTP.World"));
        if (world == null) {
            player.sendMessage("§c[Erro] Mundo RTP não configurado!");
            return;
        }

        Random random = new Random();
        int maxX = plugin.getConfig().getInt("RTP.CoordsX");
        int maxZ = plugin.getConfig().getInt("RTP.CoordsZ");

        Location[] safeLocationHolder = new Location[1];
        int attempts = 0;

        
        while (safeLocationHolder[0] == null && attempts < MAX_ATTEMPTS) {
            attempts++;

            
            int x = random.nextInt(maxX * 2) - maxX;
            int z = random.nextInt(maxZ * 2) - maxZ;

            
            if (Math.hypot(x, z) < MIN_DISTANCE_FROM_SPAWN) {
                continue;
            }

            
            safeLocationHolder[0] = findSafeLocation(world, x, z);
        }

        final Location safeLocation = safeLocationHolder[0];

        if (safeLocation == null) {
            player.sendMessage("§c[Erro] Não foi possível encontrar localização segura. Tente novamente.");
            return;
        }

        
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!player.isOnline()) {
                return;
            }

            player.teleport(safeLocation);
            player.sendMessage("§6[§e!§6] §aTeleportado Para X:§7 " + safeLocation.getBlockX() + 
                              " §aY:§7 " + safeLocation.getBlockY() + 
                              " §aZ:§7 " + safeLocation.getBlockZ() + 
                              " §ano Mundo §e" + world.getName());
        });
    }

    
    private Location findSafeLocation(World world, int x, int z) {
        
        for (int y = 64; y < 256; y++) {
            Block block = world.getBlockAt(x, y, z);
            Block blockAbove = world.getBlockAt(x, y + 1, z);

            
            if (isSafeBlock(block) && blockAbove.getType() == Material.AIR) {
                return new Location(world, x + 0.5, y + 1, z + 0.5);
            }
        }

        return null;
    }

    
    private boolean isSafeBlock(Block block) {
        Material type = block.getType();
        
        
        if (type.isSolid() && !type.toString().contains("WATER") && !type.toString().contains("LAVA")) {
            return true;
        }

        return false;
    }
}
