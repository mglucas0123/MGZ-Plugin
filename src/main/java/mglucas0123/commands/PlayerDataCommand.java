package mglucas0123.commands;

import mglucas0123.Principal;
import mglucas0123.inventory.InventoryBackup;
import mglucas0123.inventory.InventoryBackupManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlayerDataCommand implements CommandExecutor {
    
    private final Principal plugin;
    private final InventoryBackupManager backupManager;
    
    
    private final Map<String, String> pendingRestores = new HashMap<>();
    
    public PlayerDataCommand(Principal plugin, InventoryBackupManager backupManager) {
        this.plugin = plugin;
        this.backupManager = backupManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (!sender.hasPermission("mgz.playerdata")) {
            sender.sendMessage("§cVocê não tem permissão para usar este comando!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "list":
                handleList(sender, args);
                break;
                
            case "restore":
                handleRestore(sender, args);
                break;
                
            case "backup":
                handleBackup(sender, args);
                break;
                
            case "info":
                handleInfo(sender, args);
                break;
                
            case "confirm":
                handleConfirm(sender, args);
                break;
                
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l========== PlayerData Backup ==========");
        sender.sendMessage("§7Gerencia backups §lCOMPLETOS§7 de player data");
        sender.sendMessage("");
        sender.sendMessage("§e/playerdata list §7[jogador]");
        sender.sendMessage("  §8→ Lista backups (seu ou de outro jogador)");
        sender.sendMessage("");
        sender.sendMessage("§e/playerdata backup §7[jogador]");
        sender.sendMessage("  §8→ Cria backup forçado (seu ou de outro jogador)");
        sender.sendMessage("");
        sender.sendMessage("§e/playerdata restore §7<jogador> §7<número>");
        sender.sendMessage("  §8→ Restaura backup específico");
        sender.sendMessage("");
        sender.sendMessage("§e/playerdata info §7[jogador]");
        sender.sendMessage("  §8→ Mostra informações do sistema de backup");
        sender.sendMessage("");
        sender.sendMessage("§e/playerdata confirm");
        sender.sendMessage("  §8→ Confirma restauração pendente");
        sender.sendMessage("");
        sender.sendMessage("§7Aliases: §f/pd, /pdata, /playerbackup");
        sender.sendMessage("§6§l======================================");
    }
    
    
    private void handleList(CommandSender sender, String[] args) {
        Player target;
        
        
        if (args.length >= 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cJogador §f" + args[1] + " §cnão encontrado ou offline!");
                return;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cVocê deve especificar um jogador! §7/playerdata list <jogador>");
                return;
            }
            target = (Player) sender;
        }
        
        List<InventoryBackup> history = backupManager.getBackupHistory(target.getUniqueId());
        
        if (history.isEmpty()) {
            sender.sendMessage("§cO jogador §f" + target.getName() + " §cnão tem backups disponíveis!");
            return;
        }
        
        sender.sendMessage("§6§l========== Backups: " + target.getName() + " ==========");
        sender.sendMessage("");
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        for (int i = 0; i < history.size(); i++) {
            InventoryBackup backup = history.get(i);
            String time = sdf.format(new Date(backup.getTimestamp()));
            long minutesAgo = (System.currentTimeMillis() - backup.getTimestamp()) / 60000;
            
            sender.sendMessage(String.format("§e#%d §7- §f%s §7(%d min atrás)",
                i + 1, time, minutesAgo));
            sender.sendMessage(String.format("    §7Inv: §f%d §7| EnderChest: §f%d §7| Lvl: §f%d §7| HP: §f%.1f §7| GM: §f%s",
                backup.getItemCount(), 
                backup.getEnderChestItemCount(),
                backup.getLevel(),
                backup.getHealth(),
                backup.getGameMode().name().substring(0, 1)));
        }
        
        sender.sendMessage("");
        sender.sendMessage("§7Use §f/playerdata restore " + target.getName() + " <número> §7para restaurar");
        sender.sendMessage("§6§l================================================");
    }
    
    
    private void handleRestore(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUso correto: §f/playerdata restore <jogador> <número>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cJogador §f" + args[1] + " §cnão encontrado ou offline!");
            return;
        }
        
        int backupIndex;
        try {
            backupIndex = Integer.parseInt(args[2]) - 1;
        } catch (NumberFormatException e) {
            sender.sendMessage("§cNúmero inválido! Use um número de backup válido.");
            return;
        }
        
        List<InventoryBackup> history = backupManager.getBackupHistory(target.getUniqueId());
        
        if (history.isEmpty()) {
            sender.sendMessage("§cO jogador §f" + target.getName() + " §cnão tem backups disponíveis!");
            return;
        }
        
        if (backupIndex < 0 || backupIndex >= history.size()) {
            sender.sendMessage("§cBackup #" + (backupIndex + 1) + " não existe! Use §f/playerdata list " + target.getName());
            return;
        }
        
        InventoryBackup backup = history.get(backupIndex);
        
        
        sender.sendMessage("§e§l⚠ CONFIRMAÇÃO NECESSÁRIA!");
        sender.sendMessage("§7Você está prestes a restaurar §lTODA A PLAYER DATA§7 de §f" + target.getName() + "§7!");
        sender.sendMessage("§7Backup de: §f" + getFormattedTime(backup.getTimestamp()));
        sender.sendMessage("");
        sender.sendMessage("§7O que será restaurado:");
        sender.sendMessage("  §7• Inventário: §f" + backup.getItemCount() + " itens");
        sender.sendMessage("  §7• Ender Chest: §f" + backup.getEnderChestItemCount() + " itens");
        sender.sendMessage("  §7• Nível: §f" + backup.getLevel());
        sender.sendMessage("  §7• Vida: §f" + String.format("%.1f", backup.getHealth()) + " ❤");
        sender.sendMessage("  §7• Fome: §f" + backup.getFoodLevel() + " 🍖");
        sender.sendMessage("  §7• Game Mode: §f" + backup.getGameMode().name());
        sender.sendMessage("  §7• Efeitos: §f" + backup.getPotionEffectCount() + " ativos");
        sender.sendMessage("  §7• Local: §f" + backup.getLocation());
        sender.sendMessage("");
        sender.sendMessage("§cTODOS os dados atuais de §f" + target.getName() + " §cserão §lSOBRESCRITOS§c!");
        sender.sendMessage("§aDigite §f/playerdata confirm §apara confirmar");
        sender.sendMessage("§7Ou espere 30 segundos para cancelar");
        
        
        String senderKey = sender.getName();
        pendingRestores.put(senderKey, target.getName() + ":" + backupIndex);
        
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            pendingRestores.remove(senderKey);
        }, 600L);
    }
    
    
    private void handleConfirm(CommandSender sender, String[] args) {
        String senderKey = sender.getName();
        
        if (!pendingRestores.containsKey(senderKey)) {
            sender.sendMessage("§cVocê não tem nenhuma restauração pendente!");
            return;
        }
        
        String data = pendingRestores.get(senderKey);
        String[] parts = data.split(":");
        String targetName = parts[0];
        int backupIndex = Integer.parseInt(parts[1]);
        
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cO jogador §f" + targetName + " §cdesconectou!");
            pendingRestores.remove(senderKey);
            return;
        }
        
        
        boolean success = backupManager.restoreBackup(target, backupIndex);
        
        if (success) {
            sender.sendMessage("§a§l✔ Player data de §f" + target.getName() + " §a§lrestaurada com sucesso!");
            sender.sendMessage("§7Backup #" + (backupIndex + 1) + " aplicado.");
            
            target.sendMessage("§a§l⚠ SUA PLAYER DATA FOI RESTAURADA!");
            target.sendMessage("§7Um administrador (§f" + sender.getName() + "§7) restaurou seus dados.");
            target.sendMessage("§7Backup aplicado: §f#" + (backupIndex + 1));
            
            
            Bukkit.getConsoleSender().sendMessage(
                String.format("§c[§6MGZ-PlayerData§c] §e%s §7restaurou player data de §e%s §7(backup #%d)",
                    sender.getName(), target.getName(), backupIndex + 1)
            );
        } else {
            sender.sendMessage("§cErro ao restaurar player data!");
        }
        
        pendingRestores.remove(senderKey);
    }
    
    
    private void handleBackup(CommandSender sender, String[] args) {
        Player target;
        
        
        if (args.length >= 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cJogador §f" + args[1] + " §cnão encontrado ou offline!");
                return;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cVocê deve especificar um jogador! §7/playerdata backup <jogador>");
                return;
            }
            target = (Player) sender;
        }
        
        
        backupManager.createBackup(target, "Manual (" + sender.getName() + ")");
        
        sender.sendMessage("§a§l✔ Backup criado com sucesso!");
        sender.sendMessage("§7Jogador: §f" + target.getName());
        sender.sendMessage("§7Tipo: §fManual");
        
        if (!target.equals(sender)) {
            target.sendMessage("§a§l⚠ Um administrador (§f" + sender.getName() + "§a§l) criou um backup de seus dados!");
        }
        
        
        Bukkit.getConsoleSender().sendMessage(
            String.format("§c[§6MGZ-PlayerData§c] §e%s §7criou backup manual de §e%s",
                sender.getName(), target.getName())
        );
    }
    
    
    private void handleInfo(CommandSender sender, String[] args) {
        Player target;
        
        
        if (args.length >= 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cJogador §f" + args[1] + " §cnão encontrado ou offline!");
                return;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cVocê deve especificar um jogador! §7/playerdata info <jogador>");
                return;
            }
            target = (Player) sender;
        }
        
        List<InventoryBackup> history = backupManager.getBackupHistory(target.getUniqueId());
        
        sender.sendMessage("§6§l========== PlayerData Backup Info ==========");
        sender.sendMessage("§7Jogador: §f" + target.getName());
        sender.sendMessage("§7Status: §aAtivo");
        sender.sendMessage("§7Backups salvos: §f" + history.size() + "/" + 
                          plugin.getConfig().getInt("InventoryBackup.MaxBackupsPerPlayer", 5));
        
        if (!history.isEmpty()) {
            InventoryBackup last = history.get(history.size() - 1);
            long minutesAgo = (System.currentTimeMillis() - last.getTimestamp()) / 60000;
            sender.sendMessage("§7Último backup: §f" + minutesAgo + " minutos atrás");
            sender.sendMessage("");
            sender.sendMessage("§7Dados no último backup:");
            sender.sendMessage("  §7• Inventário: §f" + last.getItemCount() + " itens");
            sender.sendMessage("  §7• Ender Chest: §f" + last.getEnderChestItemCount() + " itens");
            sender.sendMessage("  §7• Nível: §f" + last.getLevel());
            sender.sendMessage("  §7• Vida: §f" + String.format("%.1f", last.getHealth()) + " ❤");
            sender.sendMessage("  §7• Game Mode: §f" + last.getGameMode().name());
            sender.sendMessage("  §7• Mundo: §f" + last.getWorldName());
        }
        
        sender.sendMessage("");
        sender.sendMessage("§7Sistema: §fCache + Debounce + Async");
        sender.sendMessage("§7Intervalo auto-backup: §f" + 
            plugin.getConfig().getInt("InventoryBackup.AutoBackupInterval", 300) + "s");
        sender.sendMessage("§7Debounce: §f" + 
            plugin.getConfig().getInt("InventoryBackup.DebounceTime", 10) + "s");
        sender.sendMessage("§6§l============================================");
    }
    
    
    private String getFormattedTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }
}
