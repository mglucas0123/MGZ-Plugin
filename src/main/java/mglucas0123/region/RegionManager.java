package mglucas0123.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RegionManager {
    
    private Map<String, Region> regions;
    private File regionsFile;
    private FileConfiguration regionsConfig;
    private RegionDatabase database;
    private boolean useSQLite;
    
    public RegionManager(File dataFolder) {
        this.regions = new HashMap<>();
        this.regionsFile = new File(dataFolder, "regions.yml");
        this.useSQLite = true;
        
        try {
            this.database = new RegionDatabase(dataFolder);
            if (database.isConnected()) {
                migrateFromYamlIfNeeded();
            } else {
                useSQLite = false;
            }
        } catch (Exception e) {
            useSQLite = false;
            e.printStackTrace();
        }
        
        loadRegions();
    }
    
    public void loadRegions() {
        regions.clear();
        
        if (useSQLite && database != null && database.isConnected()) {
            regions.putAll(database.loadAllRegions());
        } else {
            loadFromYaml();
        }
    }
    
    private void loadFromYaml() {
        if (!regionsFile.exists()) {
            try {
                regionsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        regionsConfig = YamlConfiguration.loadConfiguration(regionsFile);
        
        ConfigurationSection regionsSection = regionsConfig.getConfigurationSection("regions");
        if (regionsSection != null) {
            for (String key : regionsSection.getKeys(false)) {
                ConfigurationSection regionSection = regionsSection.getConfigurationSection(key);
                if (regionSection != null) {
                    Map<String, Object> data = new HashMap<>();
                    for (String dataKey : regionSection.getKeys(false)) {
                        data.put(dataKey, regionSection.get(dataKey));
                    }
                    
                    String worldName = (String) data.get("world");
                    World world = Bukkit.getWorld(worldName);
                    if (world != null) {
                        Region region = Region.deserialize(data, world);
                        
                        String mapKey;
                        if (region.isGlobal()) {
                            mapKey = (region.getName() + "_" + worldName).toLowerCase();
                        } else {
                            mapKey = region.getName().toLowerCase();
                        }
                        
                        regions.put(mapKey, region);
                    }
                }
            }
        }
    }
    
    private void migrateFromYamlIfNeeded() {
        if (regionsFile.exists() && regionsFile.length() > 0) {
            loadFromYaml();
            
            if (!regions.isEmpty()) {
                for (Region region : regions.values()) {
                    database.saveRegion(region);
                }
                
                File backup = new File(regionsFile.getParent(), "regions.yml.migrated");
                regionsFile.renameTo(backup);
            }
        }
    }
    
    public void saveRegions() {
        if (useSQLite && database != null && database.isConnected()) {
            for (Region region : regions.values()) {
                database.saveRegion(region);
            }
        } else {
            saveToYaml();
        }
    }
    
    private void saveToYaml() {
        regionsConfig.set("regions", null);
        
        for (Region region : regions.values()) {
            Map<String, Object> data = region.serialize();
            
            String saveKey;
            if (region.isGlobal()) {
                saveKey = region.getName() + "-" + region.getWorldName();
            } else {
                saveKey = region.getName();
            }
            
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                regionsConfig.set("regions." + saveKey + "." + entry.getKey(), entry.getValue());
            }
        }
        
        try {
            regionsConfig.save(regionsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void createRegion(String name, String world, Location pos1, Location pos2) {
        Region region = new Region(name, world, pos1, pos2);
        regions.put(name.toLowerCase(), region);
        saveRegions();
    }
    
    public void createGlobalRegion(String worldName) {
        String globalName = "__global__";
        String key = globalName + "_" + worldName;
        if (!regions.containsKey(key.toLowerCase())) {
            Region region = new Region(globalName, worldName);
            regions.put(key.toLowerCase(), region);
            saveRegions();
        }
    }
    
    public Region getGlobalRegion(String worldName) {
        String globalName = "__global__";
        String key = globalName + "_" + worldName;
        return regions.get(key.toLowerCase());
    }
    
    public void deleteRegion(String name) {
        Region region = regions.remove(name.toLowerCase());
        
        if (region != null && useSQLite && database != null && database.isConnected()) {
            database.deleteRegion(region.getName(), region.getWorldName());
        } else {
            saveRegions();
        }
    }
    
    public Region getRegion(String name) {
        return regions.get(name.toLowerCase());
    }
    
    public List<Region> getRegionsAt(Location location) {
        return regions.values().stream()
                .filter(region -> region.contains(location))
                .sorted(Comparator.comparingInt(Region::getPriority).reversed()
                        .thenComparingLong(Region::getVolume))
                .collect(Collectors.toList());
    }
    
    public Region getHighestPriorityRegion(Location location) {
        List<Region> regionsAt = getRegionsAt(location);
        return regionsAt.isEmpty() ? null : regionsAt.get(0);
    }
    
    public boolean isAllowed(Location location, RegionFlag flag) {
        Region region = getHighestPriorityRegion(location);
        if (region == null) {
            return true;
        }
        return region.getFlag(flag);
    }
    
    public Collection<Region> getAllRegions() {
        return regions.values();
    }
    
    public boolean regionExists(String name) {
        return regions.containsKey(name.toLowerCase());
    }
    
    public void shutdown() {
        if (database != null) {
            database.close();
        }
    }
}
