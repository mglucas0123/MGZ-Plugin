package mglucas0123.config.editor;

import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Gerencia o estado do modo editor para cada jogador.
 * Quando ativo, permite editar GUIs arrastando itens do inventário.
 */
public class EditorModeManager {
    
    private static final Set<UUID> editorsAtivos = new HashSet<>();
    
    /**
     * Alterna o modo editor para um jogador
     */
    public static void toggle(Player player) {
        UUID id = player.getUniqueId();
        if (editorsAtivos.contains(id)) {
            disable(player);
        } else {
            enable(player);
        }
    }
    
    /**
     * Ativa o modo editor para um jogador
     */
    public static void enable(Player player) {
        editorsAtivos.add(player.getUniqueId());
        player.sendMessage("§a§l✓ Modo Editor ATIVADO");
        player.sendMessage("§7Navegue pelos menus normalmente");
        player.sendMessage("§7Arraste itens para substituir elementos");
        player.sendMessage("§7Clique novamente no botão para desativar");
    }
    
    /**
     * Desativa o modo editor para um jogador
     */
    public static void disable(Player player) {
        editorsAtivos.remove(player.getUniqueId());
        player.sendMessage("§c§l✗ Modo Editor DESATIVADO");
        player.sendMessage("§7Voltando ao modo normal");
    }
    
    /**
     * Verifica se um jogador está em modo editor
     */
    public static boolean isActive(Player player) {
        return editorsAtivos.contains(player.getUniqueId());
    }
    
    /**
     * Remove jogador ao desconectar
     */
    public static void cleanup(UUID playerId) {
        editorsAtivos.remove(playerId);
    }
}
