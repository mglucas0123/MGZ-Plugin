package mglucas0123.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SelectionManager {
    
    private Map<UUID, Selection> selections;
    
    public SelectionManager() {
        this.selections = new HashMap<>();
    }
    
    public void setPos1(Player player, Location location) {
        UUID uuid = player.getUniqueId();
        Selection selection = selections.getOrDefault(uuid, new Selection());
        selection.setPos1(location);
        selections.put(uuid, selection);
    }
    
    public void setPos2(Player player, Location location) {
        UUID uuid = player.getUniqueId();
        Selection selection = selections.getOrDefault(uuid, new Selection());
        selection.setPos2(location);
        selections.put(uuid, selection);
    }
    
    public Selection getSelection(Player player) {
        return selections.get(player.getUniqueId());
    }
    
    public boolean hasCompleteSelection(Player player) {
        Selection selection = selections.get(player.getUniqueId());
        return selection != null && selection.isComplete();
    }
    
    public void clearSelection(Player player) {
        selections.remove(player.getUniqueId());
    }
    
    public void expandVertical(Player player) {
        Selection selection = selections.get(player.getUniqueId());
        if (selection != null && selection.isComplete()) {
            selection.expandVertical();
        }
    }
    
    public static class Selection {
        private Location pos1;
        private Location pos2;
        
        public Location getPos1() {
            return pos1;
        }
        
        public void setPos1(Location pos1) {
            this.pos1 = pos1;
        }
        
        public Location getPos2() {
            return pos2;
        }
        
        public void setPos2(Location pos2) {
            this.pos2 = pos2;
        }
        
        public boolean isComplete() {
            return pos1 != null && pos2 != null && 
                   pos1.getWorld().equals(pos2.getWorld());
        }
        
        public long getVolume() {
            if (!isComplete()) return 0;
            double dx = Math.abs(pos2.getX() - pos1.getX()) + 1;
            double dy = Math.abs(pos2.getY() - pos1.getY()) + 1;
            double dz = Math.abs(pos2.getZ() - pos1.getZ()) + 1;
            return (long) (dx * dy * dz);
        }
        
        public void expandVertical() {
            if (!isComplete()) return;
            
            
            pos1.setY(pos1.getWorld().getMinHeight());
            
            
            pos2.setY(pos2.getWorld().getMaxHeight() - 1);
        }
    }
}
