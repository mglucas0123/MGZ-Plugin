package mglucas0123.commands;

import mglucas0123.Principal;
import mglucas0123.config.ConfigEditorGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigEditorCommand implements CommandExecutor {
    
    private Principal plugin;
    private ConfigEditorGUI editorGUI;
    
    public ConfigEditorCommand(Principal plugin) {
        this.plugin = plugin;
        this.editorGUI = new ConfigEditorGUI(plugin);
    }
     
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("mgz.config")) {
            player.sendMessage("§cVocê não tem permissão para usar este comando!");
            return true;
        }
        
        editorGUI.openMainMenu(player);
        player.sendMessage("§a[MGZ] Menu de configurações aberto!");
        
        return true;
    }
}
