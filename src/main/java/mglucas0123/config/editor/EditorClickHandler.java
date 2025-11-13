package mglucas0123.config.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mglucas0123.Principal;

import java.util.List;

/**
 * Intercepta cliques em GUIs quando o modo editor está ativo.
 * Permite substituir itens arrastando do inventário.
 */
public class EditorClickHandler {
    
    private Principal plugin;
    
    public EditorClickHandler(Principal plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Processa clique no modo editor
     * @return true se foi processado como edição, false se deve seguir fluxo normal
     */
    public boolean handleEditorClick(InventoryClickEvent event, Player player) {
        
        // Só processa se modo editor estiver ativo
        if (!EditorModeManager.isActive(player)) {
            return false;
        }
        
        Inventory clickedInv = event.getClickedInventory();
        if (clickedInv == null) return false;
        
        Inventory topInv = event.getView().getTopInventory();
        String title = event.getView().getTitle();
        
        // Extrair nome do menu do título
        String menuName = extractMenuName(title);
        if (menuName == null) return false; // Não é um menu editável
        
        // Se clicou no inventário do jogador (bottom), PERMITIR para pegar itens
        if (clickedInv.equals(player.getInventory())) {
            // Permite pegar itens do próprio inventário
            return false; // Não cancela, deixa pegar normalmente
        }
        
        // Clicou no menu (top inventory) - verificar se está editando
        int slot = event.getSlot();
        ItemStack cursor = event.getCursor();
        
        // Debug
        plugin.getLogger().info("[Editor] Click detectado: slot=" + slot + ", cursor=" + (cursor != null ? cursor.getType() : "null"));
        
        // Se não tem item no cursor, jogador está apenas navegando -> deixar fluxo normal
        if (cursor == null || cursor.getType() == Material.AIR) {
            plugin.getLogger().info("[Editor] Cursor vazio, navegação normal");
            return false;
        }
        
        // Tem item no cursor - está tentando SUBSTITUIR
        // Verificar se o slot é editável
        boolean editable = PatternDetector.isEditable(slot, topInv);
        String pattern = PatternDetector.detectPattern(slot, topInv.getSize());
        
        plugin.getLogger().info("[Editor] Slot " + slot + " | Pattern: " + pattern + " | Editável: " + editable);
        
        if (!editable) {
            player.sendMessage("§c✗ Este item é funcional e não pode ser editado");
            event.setCancelled(true);
            return true;
        }
        
        // Aplicar mudança
        Material newMaterial = cursor.getType();
    applyMaterialChange(topInv, menuName, pattern, newMaterial, player, slot);
        
        // Cancelar evento para não mover o item do cursor
        event.setCancelled(true);
        
        return true;
    }
    
    /**
     * Aplica mudança de material em um padrão
     */
    private void applyMaterialChange(Inventory inv, String menuName, String pattern, Material newMaterial, Player player, int clickedSlot) {
        
        // Carregar template
        GUITemplate template = GUITemplate.load(menuName, plugin.getConfig());
        if (template == null) {
            template = new GUITemplate(menuName, inv.getSize());
        }
        
        // Obter item original para preservar título/lore
        int[] patternSlots = PatternDetector.getSlotsForPattern(pattern, inv.getSize());
        if (patternSlots.length == 0) {
            patternSlots = new int[]{clickedSlot};
        }
        ItemStack originalItem = inv.getItem(patternSlots[0]);
        String displayName = " ";
        List<String> lore = null;
        
        if (originalItem != null && originalItem.hasItemMeta()) {
            ItemMeta meta = originalItem.getItemMeta();
            if (meta.hasDisplayName()) {
                displayName = meta.getDisplayName();
            }
            if (meta.hasLore()) {
                lore = meta.getLore();
            }
        }
        
        // Criar novo item com material atualizado
        ItemStack newItem = new ItemStack(newMaterial);
        ItemMeta meta = newItem.getItemMeta();
        meta.setDisplayName(displayName);
        if (lore != null) {
            meta.setLore(lore);
        }
        newItem.setItemMeta(meta);
        
        // Aplicar em todos os slots do padrão
        for (int slot : patternSlots) {
            inv.setItem(slot, newItem.clone());
        }
        
        // Salvar no template
        template.setMaterial(pattern, newMaterial);
        template.save(plugin.getConfig());
        plugin.saveConfig();
        
        // Debug log
        plugin.getLogger().info("[Editor] Template salvo: " + menuName + " | " + pattern + " = " + newMaterial.name());
        
        // Feedback visual
        String patternName = getPatternDisplayName(pattern);
        player.sendMessage("§a§l✓ " + patternName + " §aatualizado para §f" + formatMaterialName(newMaterial));
        player.sendMessage("§7└─ §7Aplicado em §e" + patternSlots.length + " slots");
        player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
    }
    
    /**
     * Extrai nome do menu do título
     */
    private String extractMenuName(String title) {
        // Detectar menus conhecidos
        if (title.contains("GameRules")) return "GameRulesMenu";
        if (title.contains("AutoSave")) return "AutoSaveMenu";
        if (title.contains("AutoRestart")) return "AutoRestartMenu";
        if (title.contains("Random TP") || title.contains("RTP")) return "RandomTPMenu";
        if (title.contains("Backup") || title.contains("Inventory Backup")) return "InventoryBackupMenu";
        if (title.contains("ArmorStand") || title.contains("Armor Stand")) return "ArmorStandMenu";
        if (title.contains("Chat")) return "ChatControlMenu";
        if (title.contains("Clima") || title.contains("Chuva")) return "ClimaMenu";
        if (title.contains("Placas")) return "PlacasMenu";
        if (title.contains("Entrada") || title.contains("Saída") || title.contains("Join") || title.contains("Quit")) return "EntradaSaidaMenu";
        if (title.contains("MGZ") || title.contains("Config")) return "MainMenu";
        
        return null; // Menu não editável
    }
    
    /**
     * Nome amigável do padrão
     */
    private String getPatternDisplayName(String pattern) {
        switch (pattern) {
            case "header_border": return "§dBorda Superior";
            case "footer_border": return "§dBorda Inferior";
            case "side_border": return "§dBordas Laterais";
            case "filler": return "§dPreenchimento";
            case "title_icon": return "§6Ícone do Título";
            case "category_player": return "§eCategoria Jogador";
            case "category_world": return "§aCategoria Mundo";
            case "category_mobs": return "§cCategoria Mobs";
            case "category_system": return "§dCategoria Sistema";
            default: return "§dElemento";
        }
    }
    
    /**
     * Formata nome do material (GRASS_BLOCK -> Grass Block)
     */
    private String formatMaterialName(Material material) {
        String name = material.name().replace("_", " ");
        String[] words = name.split(" ");
        StringBuilder formatted = new StringBuilder();
        
        for (String word : words) {
            if (formatted.length() > 0) formatted.append(" ");
            formatted.append(word.substring(0, 1).toUpperCase());
            formatted.append(word.substring(1).toLowerCase());
        }
        
        return formatted.toString();
    }
}
