package mglucas0123.commands;

import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class Repair implements CommandExecutor {
    private final HashMap<String, Long> cooldowns;

    public Repair() {
        this.cooldowns = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        int cooldownTime = 600;

        if (cooldowns.containsKey(player.getName())) {
            long secondsLeft = (cooldowns.get(player.getName()) + cooldownTime * 1000 - System.currentTimeMillis()) / 1000;
            if (secondsLeft > 0) {
                sender.sendMessage("§6[§e!§6] §cAguarde §7" + secondsLeft + " §csegundos para usar esse comando novamente.");
                return true;
            }
        }

        if (cmd.getName().equalsIgnoreCase("Repair")) {
            if (!sender.hasPermission("mgz.repair") && !player.isOp() && !sender.hasPermission("mgz.*")) {
                sender.sendMessage("§6[§e!§6] §cVocê não tem acesso a esse comando.");
                return true;
            }

            if (args.length != 0) {
                return false;
            }

            if (item.getType() == Material.AIR) {
                sender.sendMessage("§6[§4!§6] §cVocê deve ter um item na sua mão para reparar.");
                return true;
            }

            if (item.getType().getMaxDurability() == 0) {
                sender.sendMessage("§6[§e!§6] §cEste item não pode ser reparado.");
                return true;
            }

            if (!(item.getItemMeta() instanceof Damageable)) {
                sender.sendMessage("§6[§e!§6] §cEste item não pode ser reparado.");
                return true;
            }

            Damageable damageable = (Damageable) item.getItemMeta();
            if (damageable.getDamage() == 0) {
                sender.sendMessage("§6[§e!§6] §cSeu item já está reparado.");
                return true;
            }

            if (!(sender.hasPermission("mgz.repair.cooldown") || player.isOp() || sender.hasPermission("mgz.*"))) {
                cooldowns.put(player.getName(), System.currentTimeMillis());
            }

            damageable.setDamage(0);
            item.setItemMeta(damageable);
            sender.sendMessage("§6[§a!§6] §aSeu item §e" + item.getType().name() + " §afoi reparado.");
            return true;
        }

        return false;
    }
}
