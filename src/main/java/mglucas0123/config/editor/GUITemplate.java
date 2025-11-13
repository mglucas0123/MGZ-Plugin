package mglucas0123.config.editor;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;


public class GUITemplate {
    
    private final String name;
    private final int size;
    private final Map<String, Material> materials; 
    
    public GUITemplate(String name, int size) {
        this(name, size, true);
    }
    
    
    private GUITemplate(String name, int size, boolean setDefaults) {
        this.name = name;
        this.size = size;
        this.materials = new HashMap<>();
        
        if (setDefaults) {
            setDefaults();
        }
    }
    
    private void setDefaults() {
        
        materials.put("header_border", Material.PURPLE_STAINED_GLASS_PANE);
        materials.put("footer_border", Material.GRAY_STAINED_GLASS_PANE);
        materials.put("side_border", Material.BLACK_STAINED_GLASS_PANE);
        
        
        materials.put("filler", Material.BLACK_STAINED_GLASS_PANE);
        
        
        materials.put("back_button", Material.ARROW);
        materials.put("confirm_button", Material.EMERALD_BLOCK);
        materials.put("cancel_button", Material.REDSTONE_BLOCK);
        materials.put("info_button", Material.KNOWLEDGE_BOOK);
        
        
        materials.put("title_icon", Material.ENCHANTED_BOOK);
        materials.put("category_player", Material.PLAYER_HEAD);
        materials.put("category_world", Material.GRASS_BLOCK);
        materials.put("category_mobs", Material.ZOMBIE_HEAD);
        materials.put("category_system", Material.REDSTONE);
    }
    
    public String getName() {
        return name;
    }
    
    public int getSize() {
        return size;
    }
    
    public Material getMaterial(String category) {
        return materials.getOrDefault(category, Material.STONE);
    }
    
    public void setMaterial(String category, Material material) {
        materials.put(category, material);
    }
    
    public Map<String, Material> getAllMaterials() {
        return new HashMap<>(materials);
    }
    
    
    public void save(FileConfiguration config) {
        String path = "GUITemplates." + name;
        
        config.set(path + ".Size", size);
        
        for (Map.Entry<String, Material> entry : materials.entrySet()) {
            config.set(path + ".Materials." + entry.getKey(), entry.getValue().name());
        }
    }
    
    
    public static GUITemplate load(String name, FileConfiguration config) {
        String path = "GUITemplates." + name;
        
        if (!config.contains(path)) {
            return null;
        }
        
        int size = config.getInt(path + ".Size", 54);
        
        
        GUITemplate template = new GUITemplate(name, size, false);
        
        
        template.setDefaults();
        
        
        ConfigurationSection materialsSection = config.getConfigurationSection(path + ".Materials");
        if (materialsSection != null) {
            for (String key : materialsSection.getKeys(false)) {
                String materialName = materialsSection.getString(key);
                try {
                    Material material = Material.valueOf(materialName);
                    template.setMaterial(key, material); 
                } catch (IllegalArgumentException e) {
                    
                }
            }
        }
        
        return template;
    }
    
    
    public ItemStack createItem(String category) {
        Material material = getMaterial(category);
        return new ItemStack(material);
    }
}
