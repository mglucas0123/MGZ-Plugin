package mglucas0123.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tab completer para o comando /playerdata
 * Fornece autocomplete inteligente baseado no contexto
 */
public class PlayerDataTabCompleter implements TabCompleter {
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        // Verificar permissão
        if (!sender.hasPermission("mgz.playerdata")) {
            return completions;
        }
        
        // Arg 1: Subcomandos
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("list", "restore", "backup", "info", "confirm");
            return filterSuggestions(subCommands, args[0]);
        }
        
        // Args 2+: Depende do subcomando
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "list":
            case "backup":
            case "info":
                // Arg 2: Nome do jogador (opcional)
                if (args.length == 2) {
                    return filterSuggestions(getOnlinePlayerNames(), args[1]);
                }
                break;
                
            case "restore":
                // Arg 2: Nome do jogador (obrigatório)
                if (args.length == 2) {
                    return filterSuggestions(getOnlinePlayerNames(), args[1]);
                }
                // Arg 3: Número do backup
                if (args.length == 3) {
                    return filterSuggestions(Arrays.asList("1", "2", "3", "4", "5"), args[2]);
                }
                break;
                
            case "confirm":
                // Sem argumentos adicionais
                break;
        }
        
        return completions;
    }
    
    /**
     * Retorna lista de nomes de jogadores online
     */
    private List<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }
    
    /**
     * Filtra sugestões baseado no que o jogador já digitou
     */
    private List<String> filterSuggestions(List<String> suggestions, String input) {
        String lowerInput = input.toLowerCase();
        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(lowerInput))
                .sorted()
                .collect(Collectors.toList());
    }
}
