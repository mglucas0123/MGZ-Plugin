package mglucas0123.config.editor;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;


public class PatternDetector {
    
    
    public static String detectPattern(int slot, int size) {
        int rows = size / 9;
        int row = slot / 9;
        int col = slot % 9;
        
        
        if (row == 0) {
            
            if (col == 4) return "title_icon";
            return "header_border";
        }
        
        
        if (row == rows - 1) {
            return "footer_border";
        }
        
        
        if (col == 1 && row > 0 && row < rows - 1) {
            switch (slot) {
                case 10: return "category_player";
                case 19: return "category_world";
                case 28: return "category_mobs";
                case 37: return "category_system";
            }
        }
        
        
        if (col == 0 || col == 8) {
            return "side_border";
        }
        
        
        return "filler";
    }
    
    
    public static int[] getSlotsForPattern(String pattern, int size) {
        int rows = size / 9;
        
        switch (pattern) {
            case "header_border":
                
                int[] headerSlots = new int[8];
                int idx = 0;
                for (int i = 0; i < 9; i++) {
                    if (i != 4) headerSlots[idx++] = i;
                }
                return headerSlots;
                
            case "footer_border":
                
                int start = (rows - 1) * 9;
                return generateRange(start, start + 8);
                
            case "side_border":
                
                int[] sides = new int[(rows - 2) * 2];
                int index = 0;
                for (int row = 1; row < rows - 1; row++) {
                    sides[index++] = row * 9;      
                    sides[index++] = row * 9 + 8;  
                }
                return sides;
            
            
            case "title_icon":
                return new int[]{4};
            case "category_player":
                return new int[]{10};
            case "category_world":
                return new int[]{19};
            case "category_mobs":
                return new int[]{28};
            case "category_system":
                return new int[]{37};
                
            default:
                return new int[0];
        }
    }
    
    
    private static int[] generateRange(int start, int end) {
        int[] range = new int[end - start + 1];
        for (int i = 0; i < range.length; i++) {
            range[i] = start + i;
        }
        return range;
    }
    
    
    public static boolean isEditable(int slot, Inventory inv) {
        ItemStack item = inv.getItem(slot);
        if (item == null) return true; 
        
        
        String pattern = detectPattern(slot, inv.getSize());
        
        
        if (pattern.contains("border") || 
            pattern.equals("filler") || 
            pattern.contains("category_") ||
            pattern.equals("title_icon")) {
            return true;
        }
        
        
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            
            String itemName = item.getItemMeta().hasDisplayName() ? 
                item.getItemMeta().getDisplayName() : item.getType().name();
            org.bukkit.Bukkit.getLogger().info("[PatternDetector] Bloqueado slot " + slot + 
                " (tem lore - botão funcional): " + itemName);
            return false; 
        }
        
        return true;
    }
    
    
    public static Map<String, ItemStack> detectAllPatterns(Inventory inv) {
        Map<String, ItemStack> patterns = new HashMap<>();
        
        for (int slot = 0; slot < inv.getSize(); slot++) {
            ItemStack item = inv.getItem(slot);
            if (item == null) continue;
            
            String pattern = detectPattern(slot, inv.getSize());
            if (!patterns.containsKey(pattern)) {
                patterns.put(pattern, item.clone());
            }
        }
        
        return patterns;
    }
}
