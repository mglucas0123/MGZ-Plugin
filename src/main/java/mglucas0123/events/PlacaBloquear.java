package mglucas0123.events;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import mglucas0123.Principal;

import java.util.List;

public class PlacaBloquear implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onSignInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        
        if (player.hasPermission("mgz.sign.bypass")) {
            return;
        }
        
        Block block = e.getClickedBlock();
        if (block == null) {
            return;
        }
        
        if (!isSign(block.getType())) {
            return;
        }
        
        try {
            Sign sign = (Sign) block.getState();
            
            if (containsBlockedText(sign.getLines(), player.getName())) {
                e.setCancelled(true);
                player.sendMessage("§c§l⚠ Esta placa contém conteúdo bloqueado!");
                player.sendMessage("§7Você não pode interagir com ela.");
            }
        } catch (Exception ex) {
            Principal.plugin.getLogger().warning(
                "Erro ao verificar placa em " + block.getLocation() + ": " + ex.getMessage()
            );
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onSignCreate(SignChangeEvent e) {
        Player player = e.getPlayer();
        
        if (player.hasPermission("mgz.sign.bypass")) {
            return;
        }
        
        String[] lines = e.getLines();
        
        if (containsBlockedText(lines, player.getName())) {
            e.setCancelled(true);
            
            e.getBlock().breakNaturally();
            
            player.sendMessage("§c§l⚠ PLACA BLOQUEADA!");
            player.sendMessage("§7Você não pode escrever esse conteúdo em placas.");
            player.sendMessage("§7Texto bloqueado detectado.");
        }
    }
    
    private boolean containsBlockedText(String[] lines, String playerName) {
        List<String> blockedWords = Principal.plugin.getConfig().getStringList("Placas.Placa_Bloquear");
        
        if (blockedWords == null || blockedWords.isEmpty()) {
            return false;
        }
        
        for (String line : lines) {
            if (line == null || line.isEmpty()) {
                continue;
            }
            
            String normalizedLine = line.toLowerCase().trim();
            
            for (String blockedWord : blockedWords) {
                if (blockedWord == null || blockedWord.isEmpty()) {
                    continue;
                }
                
                String normalizedBlocked = blockedWord.toLowerCase().trim();
                
                if (normalizedLine.contains(normalizedBlocked)) {
                    Principal.plugin.getLogger().info(
                        String.format("§c[PlacaBloquear] §f%s §7tentou criar placa com: §e%s",
                            playerName, blockedWord)
                    );
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean isSign(Material material) {
        try {
            return Tag.SIGNS.isTagged(material) || Tag.WALL_SIGNS.isTagged(material);
        } catch (NoSuchFieldError e) {
            String name = material.name();
            return name.contains("SIGN");
        }
    }
}
