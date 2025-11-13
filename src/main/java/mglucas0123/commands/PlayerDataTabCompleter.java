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


public class PlayerDataTabCompleter implements TabCompleter {
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        
        if (!sender.hasPermission("mgz.playerdata")) {
            return completions;
        }
        
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("list", "restore", "backup", "info", "confirm");
            return filterSuggestions(subCommands, args[0]);
        }
        
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "list":
            case "backup":
            case "info":
                
                if (args.length == 2) {
                    return filterSuggestions(getOnlinePlayerNames(), args[1]);
                }
                break;
                
            case "restore":
                
                if (args.length == 2) {
                    return filterSuggestions(getOnlinePlayerNames(), args[1]);
                }
                
                if (args.length == 3) {
                    return filterSuggestions(Arrays.asList("1", "2", "3", "4", "5"), args[2]);
                }
                break;
                
            case "confirm":
                
                break;
        }
        
        return completions;
    }
    
    
    private List<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }
    
    
    private List<String> filterSuggestions(List<String> suggestions, String input) {
        String lowerInput = input.toLowerCase();
        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(lowerInput))
                .sorted()
                .collect(Collectors.toList());
    }
}
