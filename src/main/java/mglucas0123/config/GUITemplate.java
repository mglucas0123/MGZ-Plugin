package mglucas0123.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class GUITemplate {
    
    private String menuName;
    private int size;
    private Map<String, Material> materials;
    
    public GUITemplate(String menuName, int size) {
        this.menuName = menuName;
        this.size = size;
        this.materials = getDefaultMaterials();
    }
    
    private Map<String, Material> getDefaultMaterials() {
        Map<String, Material> defaults = new HashMap<>();
        defaults.put("header_border", Material.BLACK_STAINED_GLASS_PANE);
        defaults.put("footer_border", Material.BLACK_STAINED_GLASS_PANE);
        defaults.put("side_border", Material.GRAY_STAINED_GLASS_PANE);
        defaults.put("filler", Material.GRAY_STAINED_GLASS_PANE);
        defaults.put("back_button", Material.ARROW);
        defaults.put("confirm_button", Material.EMERALD);
        defaults.put("cancel_button", Material.BARRIER);
        defaults.put("info_button", Material.KNOWLEDGE_BOOK);
        defaults.put("title_icon", Material.BOOK);
        defaults.put("category_player", Material.PLAYER_HEAD);
        defaults.put("category_world", Material.GRASS_BLOCK);
        defaults.put("category_mobs", Material.ZOMBIE_HEAD);
        defaults.put("category_system", Material.REDSTONE);
        return defaults;
    }
    
    public static GUITemplate load(String menuName, ConfigurationSection config) {
        if (config == null) return null;
        
        ConfigurationSection templateSection = config.getConfigurationSection("GUI.Templates." + menuName);
        if (templateSection == null) return null;
        
        int size = templateSection.getInt("size", 54);
        GUITemplate template = new GUITemplate(menuName, size);
        
        ConfigurationSection materialsSection = templateSection.getConfigurationSection("materials");
        if (materialsSection != null) {
            for (String key : materialsSection.getKeys(false)) {
                String materialName = materialsSection.getString(key);
                try {
                    Material material = Material.valueOf(materialName.toUpperCase());
                    template.materials.put(key, material);
                } catch (IllegalArgumentException e) {
                }
            }
        }
        
        return template;
    }
    
    public Material getMaterial(String key) {
        return materials.getOrDefault(key, Material.GRAY_STAINED_GLASS_PANE);
    }
    
    public int getSize() {
        return size;
    }
    
    public String getMenuName() {
        return menuName;
    }
}
