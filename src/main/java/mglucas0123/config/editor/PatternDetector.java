package mglucas0123.config.editor;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Detecta padrões de itens em GUIs criados com loops (for).
 * Identifica bordas, fillers e outros elementos repetidos.
 */
public class PatternDetector {
    
    /**
     * Detecta qual padrão um slot pertence baseado na posição
     * 
     * @param slot Número do slot (0-53 para inventário de 54 slots)
     * @param size Tamanho total do inventário
     * @return Categoria do padrão (ex: "header_border", "side_border", "filler")
     */
    public static String detectPattern(int slot, int size) {
        int rows = size / 9;
        int row = slot / 9;
        int col = slot % 9;
        
        // Linha superior (header)
        if (row == 0) {
            // Slot central do header (título)
            if (col == 4) return "title_icon";
            return "header_border";
        }
        
        // Linha inferior (footer)
        if (row == rows - 1) {
            return "footer_border";
        }
        
        // Categorias específicas do GameRules (coluna 1, exceto header/footer)
        if (col == 1 && row > 0 && row < rows - 1) {
            switch (slot) {
                case 10: return "category_player";
                case 19: return "category_world";
                case 28: return "category_mobs";
                case 37: return "category_system";
            }
        }
        
        // Colunas laterais (sides)
        if (col == 0 || col == 8) {
            return "side_border";
        }
        
        // Centro vazio = filler
        return "filler";
    }
    
    /**
     * Retorna todos os slots que pertencem a um padrão específico
     * 
     * @param pattern Nome do padrão
     * @param size Tamanho do inventário
     * @return Array com números dos slots
     */
    public static int[] getSlotsForPattern(String pattern, int size) {
        int rows = size / 9;
        
        switch (pattern) {
            case "header_border":
                // Linha 0 completa (0-8), exceto o título (slot 4)
                int[] headerSlots = new int[8];
                int idx = 0;
                for (int i = 0; i < 9; i++) {
                    if (i != 4) headerSlots[idx++] = i;
                }
                return headerSlots;
                
            case "footer_border":
                // Última linha completa
                int start = (rows - 1) * 9;
                return generateRange(start, start + 8);
                
            case "side_border":
                // Colunas 0 e 8 (exceto primeira e última linha)
                int[] sides = new int[(rows - 2) * 2];
                int index = 0;
                for (int row = 1; row < rows - 1; row++) {
                    sides[index++] = row * 9;      // Coluna 0
                    sides[index++] = row * 9 + 8;  // Coluna 8
                }
                return sides;
            
            // Slots únicos
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
    
    /**
     * Gera array de inteiros sequenciais
     */
    private static int[] generateRange(int start, int end) {
        int[] range = new int[end - start + 1];
        for (int i = 0; i < range.length; i++) {
            range[i] = start + i;
        }
        return range;
    }
    
    /**
     * Verifica se um slot é editável (não é botão funcional)
     * 
     * @param slot Número do slot
     * @param inv Inventário atual
     * @return true se pode ser editado
     */
    public static boolean isEditable(int slot, Inventory inv) {
        ItemStack item = inv.getItem(slot);
        if (item == null) return true; // Slots vazios podem receber filler
        
        // Detectar padrão baseado em posição
        String pattern = detectPattern(slot, inv.getSize());
        
        // Bordas, fillers, categorias e ícone título são sempre editáveis
        if (pattern.contains("border") || 
            pattern.equals("filler") || 
            pattern.contains("category_") ||
            pattern.equals("title_icon")) {
            return true;
        }
        
        // Botões com lore específica NÃO são editáveis (são funcionais)
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            // Debug: mostrar por que foi bloqueado
            String itemName = item.getItemMeta().hasDisplayName() ? 
                item.getItemMeta().getDisplayName() : item.getType().name();
            org.bukkit.Bukkit.getLogger().info("[PatternDetector] Bloqueado slot " + slot + 
                " (tem lore - botão funcional): " + itemName);
            return false; // Preservar botões funcionais
        }
        
        return true;
    }
    
    /**
     * Cria mapa de padrões detectados no inventário
     */
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
