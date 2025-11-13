package mglucas0123.config.menus;

import mglucas0123.Principal;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class BaseMenu {
    
    protected Principal plugin;
    
    public BaseMenu(Principal plugin) {
        this.plugin = plugin;
    }
    
    public abstract void open(Player player);
    
    protected ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    
    protected ItemStack createToggleItem(Material material, String name, boolean enabled, String... extraLore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(extraLore));
        item.setItemMeta(meta);
        return item;
    }
    
    protected void fillEmpty(Inventory inv) {
        ItemStack empty = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", "");
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, empty);
            }
        }
    }
}
