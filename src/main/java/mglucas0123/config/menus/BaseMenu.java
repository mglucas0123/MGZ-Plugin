package mglucas0123.config.menus;

import mglucas0123.Principal;
import mglucas0123.config.editor.GUITemplate;
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
    
    /**
     * Abre o menu para o jogador
     */
    public abstract void open(Player player);
    
    /**
     * Cria um item com nome e lore
     */
    protected ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Cria um item de toggle (ativado/desativado)
     */
    protected ItemStack createToggleItem(Material material, String name, boolean enabled, String... extraLore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(extraLore));
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Preenche espaços vazios com vidro cinza
     */
    protected void fillEmpty(Inventory inv) {
        ItemStack empty = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", "");
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, empty);
            }
        }
    }
    
    /**
     * Carrega template do menu (nome baseado na classe)
     * Retorna null se não existir template salvo
     */
    protected GUITemplate loadTemplate(String menuName, int size) {
        GUITemplate template = GUITemplate.load(menuName, plugin.getConfig());
        if (template == null) {
            template = new GUITemplate(menuName, size);
            plugin.getLogger().info("[" + menuName + "] Template não encontrado, usando defaults");
        } else {
            plugin.getLogger().info("[" + menuName + "] Template carregado do config.yml");
        }
        return template;
    }
}
