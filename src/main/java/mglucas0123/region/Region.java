package mglucas0123.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class Region implements ConfigurationSerializable {
    
    private String name;
    private String world;
    private Location pos1;
    private Location pos2;
    private Map<RegionFlag, Boolean> flags;
    private int priority;
    private boolean isGlobal;
    private Set<String> blockedItems;
    private Set<String> whitelistedItems;
    private Set<String> totalBlockedItems; // Itens que não podem nem ser segurados
    
    public Region(String name, String world, Location pos1, Location pos2) {
        this.name = name;
        this.world = world;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.flags = new HashMap<>();
        this.priority = 0;
        this.isGlobal = false;
        this.blockedItems = new HashSet<>();
        this.whitelistedItems = new HashSet<>();
        this.totalBlockedItems = new HashSet<>();
        initializeDefaultFlags();
    }
    
    public Region(String name, String world) {
        this.name = name;
        this.world = world;
        this.pos1 = null;
        this.pos2 = null;
        this.flags = new HashMap<>();
        this.priority = -1000;
        this.isGlobal = true;
        this.blockedItems = new HashSet<>();
        this.whitelistedItems = new HashSet<>();
        this.totalBlockedItems = new HashSet<>();
        initializeDefaultFlags();
    }
    
    private void initializeDefaultFlags() {
        // Aplicar o profile DEFAULT para valores padrões balanceados e seguros
        flags.put(RegionFlag.PVP, false);
        flags.put(RegionFlag.MOB_SPAWN, false);
        flags.put(RegionFlag.BUILD, false);
        flags.put(RegionFlag.BREAK, false);
        flags.put(RegionFlag.INTERACT, true);
        flags.put(RegionFlag.INVINCIBLE, true);
        flags.put(RegionFlag.FIRE_SPREAD, true);
        flags.put(RegionFlag.EXPLOSION, false);
        flags.put(RegionFlag.ITEM_DROP, true);
        flags.put(RegionFlag.ITEM_PICKUP, true);
    }
    
    public boolean contains(Location loc) {
        if (!loc.getWorld().getName().equals(world)) {
            return false;
        }
        
        if (isGlobal) {
            return true;
        }
        
        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());
        
        return loc.getX() >= minX && loc.getX() <= maxX &&
               loc.getY() >= minY && loc.getY() <= maxY &&
               loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }
    
    public String getName() {
        return name;
    }
    
    public String getWorldName() {
        return world;
    }
    
    public Location getPos1() {
        return pos1;
    }
    
    public Location getPos2() {
        return pos2;
    }
    
    public boolean getFlag(RegionFlag flag) {
        return flags.getOrDefault(flag, true);
    }
    
    public void setFlag(RegionFlag flag, boolean value) {
        flags.put(flag, value);
    }
    
    public Map<RegionFlag, Boolean> getFlags() {
        return new HashMap<>(flags);
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public boolean isGlobal() {
        return isGlobal;
    }
    
    public void addBlockedItem(String material) {
        blockedItems.add(material.toUpperCase());
    }
    
    public void addBlockedItem(String material, boolean totalBlock) {
        blockedItems.add(material.toUpperCase());
        if (totalBlock) {
            totalBlockedItems.add(material.toUpperCase());
        }
    }
    
    public void removeBlockedItem(String material) {
        blockedItems.remove(material.toUpperCase());
        totalBlockedItems.remove(material.toUpperCase()); // Remove também do bloqueio total
    }
    
    public boolean isItemBlocked(String material) {
        return blockedItems.contains(material.toUpperCase());
    }
    
    public boolean isItemTotalBlocked(String material) {
        return totalBlockedItems.contains(material.toUpperCase());
    }
    
    public void addTotalBlockedItem(String material) {
        totalBlockedItems.add(material.toUpperCase());
    }
    
    public Set<String> getBlockedItems() {
        return new HashSet<>(blockedItems);
    }
    
    public Set<String> getTotalBlockedItems() {
        return new HashSet<>(totalBlockedItems);
    }
    
    public void clearBlockedItems() {
        blockedItems.clear();
        totalBlockedItems.clear();
    }
    
    // ===== WHITELIST METHODS =====
    
    public void addWhitelistedItem(String material) {
        whitelistedItems.add(material.toUpperCase());
    }
    
    public void removeWhitelistedItem(String material) {
        whitelistedItems.remove(material.toUpperCase());
    }
    
    public boolean isItemWhitelisted(String material) {
        return whitelistedItems.contains(material.toUpperCase());
    }
    
    public Set<String> getWhitelistedItems() {
        return new HashSet<>(whitelistedItems);
    }
    
    public void clearWhitelistedItems() {
        whitelistedItems.clear();
    }
    
    public long getVolume() {
        if (isGlobal) {
            return Long.MAX_VALUE; // Volume infinito
        }
        double dx = Math.abs(pos2.getX() - pos1.getX()) + 1;
        double dy = Math.abs(pos2.getY() - pos1.getY()) + 1;
        double dz = Math.abs(pos2.getZ() - pos1.getZ()) + 1;
        return (long) (dx * dy * dz);
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("world", world);
        data.put("global", isGlobal);
        
        if (!isGlobal) {
            data.put("pos1", pos1.getWorld().getName() + "," + pos1.getX() + "," + pos1.getY() + "," + pos1.getZ());
            data.put("pos2", pos2.getWorld().getName() + "," + pos2.getX() + "," + pos2.getY() + "," + pos2.getZ());
        }
        
        data.put("priority", priority);
        
        Map<String, Boolean> flagsData = new HashMap<>();
        for (Map.Entry<RegionFlag, Boolean> entry : flags.entrySet()) {
            flagsData.put(entry.getKey().name().toLowerCase(), entry.getValue());
        }
        data.put("flags", flagsData);
        
        // Salvar itens bloqueados como lista
        if (!blockedItems.isEmpty()) {
            data.put("blocked-items", new ArrayList<>(blockedItems));
        }
        
        // Salvar itens com bloqueio total como lista
        if (!totalBlockedItems.isEmpty()) {
            data.put("total-blocked-items", new ArrayList<>(totalBlockedItems));
        }
        
        // Salvar itens na whitelist como lista
        if (!whitelistedItems.isEmpty()) {
            data.put("whitelisted-items", new ArrayList<>(whitelistedItems));
        }
        
        return data;
    }
    
    @SuppressWarnings("unchecked")
    public static Region deserialize(Map<String, Object> data, World world) {
        String name = (String) data.get("name");
        String worldName = (String) data.get("world");
        boolean isGlobal = data.containsKey("global") && (boolean) data.get("global");
        
        Region region;
        
        if (isGlobal) {
            region = new Region(name, worldName);
        } else {
            String[] pos1Data = ((String) data.get("pos1")).split(",");
            Location pos1 = new Location(world, 
                Double.parseDouble(pos1Data[1]), 
                Double.parseDouble(pos1Data[2]), 
                Double.parseDouble(pos1Data[3]));
            
            String[] pos2Data = ((String) data.get("pos2")).split(",");
            Location pos2 = new Location(world, 
                Double.parseDouble(pos2Data[1]), 
                Double.parseDouble(pos2Data[2]), 
                Double.parseDouble(pos2Data[3]));
            
            region = new Region(name, worldName, pos1, pos2);
        }
        
        if (data.containsKey("priority")) {
            region.setPriority((int) data.get("priority"));
        }
        
        if (data.containsKey("flags")) {
            Object flagsObj = data.get("flags");
            if (flagsObj instanceof ConfigurationSection) {
                ConfigurationSection flagsSection = (ConfigurationSection) flagsObj;
                for (String key : flagsSection.getKeys(false)) {
                    try {
                        RegionFlag flag = RegionFlag.valueOf(key.toUpperCase());
                        region.setFlag(flag, flagsSection.getBoolean(key));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            } else if (flagsObj instanceof Map) {
                Map<String, Boolean> flagsData = (Map<String, Boolean>) flagsObj;
                for (Map.Entry<String, Boolean> entry : flagsData.entrySet()) {
                    try {
                        RegionFlag flag = RegionFlag.valueOf(entry.getKey().toUpperCase());
                        region.setFlag(flag, entry.getValue());
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }
        
        if (data.containsKey("blocked-items")) {
            Object blockedObj = data.get("blocked-items");
            if (blockedObj instanceof List) {
                List<String> blockedItemsList = (List<String>) blockedObj;
                for (String item : blockedItemsList) {
                    region.addBlockedItem(item);
                }
            }
        } else if (data.containsKey("blockedItems")) {
            Object blockedObj = data.get("blockedItems");
            if (blockedObj instanceof List) {
                List<String> blockedItemsList = (List<String>) blockedObj;
                for (String item : blockedItemsList) {
                    region.addBlockedItem(item);
                }
            }
        }
        
        // Carregar bloqueios totais
        if (data.containsKey("total-blocked-items")) {
            Object totalBlockedObj = data.get("total-blocked-items");
            if (totalBlockedObj instanceof List) {
                List<String> totalBlockedItemsList = (List<String>) totalBlockedObj;
                for (String item : totalBlockedItemsList) {
                    region.totalBlockedItems.add(item.toUpperCase());
                }
            }
        }
        
        // Carregar whitelist
        if (data.containsKey("whitelisted-items")) {
            Object whitelistObj = data.get("whitelisted-items");
            if (whitelistObj instanceof List) {
                List<String> whitelistedItemsList = (List<String>) whitelistObj;
                for (String item : whitelistedItemsList) {
                    region.addWhitelistedItem(item);
                }
            }
        }
        
        return region;
    }
}
