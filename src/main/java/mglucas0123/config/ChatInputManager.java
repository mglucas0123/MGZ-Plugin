package mglucas0123.config;

import mglucas0123.Principal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInputManager implements Listener {
    
    private Principal plugin;
    private ConfigEditorGUI editorGUI;
    
    // Mapa de jogadores aguardando input: UUID -> Consumer<String>
    private Map<UUID, ChatInputData> waitingForInput = new HashMap<>();
    
    public ChatInputManager(Principal plugin, ConfigEditorGUI editorGUI) {
        this.plugin = plugin;
        this.editorGUI = editorGUI;
    }
    
    /**
     * Solicita input de chat do jogador
     * @param player Jogador
     * @param inputType Tipo de input (ex: "autorestart_time")
     * @param callback Função a executar com o input recebido
     */
    public void requestInput(Player player, String inputType, Consumer<String> callback) {
        waitingForInput.put(player.getUniqueId(), new ChatInputData(inputType, callback));
    }
    
    /**
     * Cancela input pendente do jogador
     */
    public void cancelInput(Player player) {
        waitingForInput.remove(player.getUniqueId());
    }
    
    /**
     * Verifica se o jogador está aguardando input
     */
    public boolean isWaitingForInput(Player player) {
        return waitingForInput.containsKey(player.getUniqueId());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (!waitingForInput.containsKey(player.getUniqueId())) return;
        
        event.setCancelled(true);
        
        ChatInputData data = waitingForInput.remove(player.getUniqueId());
        String message = event.getMessage();
        
        // Executar no thread principal do servidor
        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
            if (message.equalsIgnoreCase("cancelar") || message.equalsIgnoreCase("cancel")) {
                player.sendMessage("§c[Config] Input cancelado!");
                editorGUI.openMainMenu(player);
                return;
            }
            
            // Validar e processar input
            data.callback.accept(message);
        });
    }
    
    /**
     * Classe interna para armazenar dados do input
     */
    private static class ChatInputData {
        String inputType;
        Consumer<String> callback;
        
        ChatInputData(String inputType, Consumer<String> callback) {
            this.inputType = inputType;
            this.callback = callback;
        }
    }
}
