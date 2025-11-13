package mglucas0123.commands;

import mglucas0123.Principal;
import mglucas0123.region.Region;
import mglucas0123.region.RegionFlag;
import mglucas0123.region.RegionManager;
import mglucas0123.region.RegionProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RegionTabCompleter implements TabCompleter {

    private RegionManager regionManager;

    public RegionTabCompleter() {
        this.regionManager = Principal.plugin.getRegionManager();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        Player player = (Player) sender;

        if (!player.hasPermission("mgz.region.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return completeMainCommand(args[0]);
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "expand":
                return completeExpand(args);

            case "create":
            case "criar":
                return completeCreate(args);

            case "delete":
            case "deletar":
            case "info":
                return completeRegionName(args[args.length - 1]);

            case "flag":
                return completeFlag(args);

            case "priority":
                return completePriority(args);

            case "blockitem":
                return completeBlockItem(args);

            case "profileinfo":
                return completeProfileName(args[args.length - 1]);

            default:
                return new ArrayList<>();
        }
    }

    private List<String> completeMainCommand(String partial) {
        List<String> commands = Arrays.asList(
            "wand",
            "expand",
            "create",
            "criar",
            "delete",
            "deletar",
            "flag",
            "info",
            "list",
            "listar",
            "priority",
            "blockitem",
            "flags",
            "profiles",
            "profileinfo"
        );
        return filter(commands, partial);
    }

    private List<String> completeExpand(String[] args) {
        if (args.length == 2) {
            List<String> directions = Arrays.asList("vert", "vertical");
            return filter(directions, args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> completeCreate(String[] args) {
        if (args.length == 2) {
            return new ArrayList<>();
        } else if (args.length == 3) {
            return completeProfileName(args[2]);
        }
        return new ArrayList<>();
    }

    private List<String> completeFlag(String[] args) {
        if (args.length == 2) {
            List<String> regions = new ArrayList<>(getRegionNames());
            regions.add("__global__");
            return filter(regions, args[1]);
        } else if (args.length == 3) {
            List<String> flags = Arrays.stream(RegionFlag.values())
                    .map(flag -> flag.name().toLowerCase())
                    .collect(Collectors.toList());
            return filter(flags, args[2]);
        } else if (args.length == 4) {
            List<String> values = Arrays.asList("true", "false");
            return filter(values, args[3]);
        }
        return new ArrayList<>();
    }

    private List<String> completePriority(String[] args) {
        if (args.length == 2) {
            return filter(getRegionNames(), args[1]);
        } else if (args.length == 3) {
            List<String> priorities = Arrays.asList("10", "20", "30", "40", "50", "60", "70", "80", "90", "100");
            return filter(priorities, args[2]);
        }
        return new ArrayList<>();
    }

    private List<String> completeBlockItem(String[] args) {
        if (args.length == 2) {
            List<String> regions = new ArrayList<>(getRegionNames());
            regions.add("__global__");
            return filter(regions, args[1]);
        } else if (args.length == 3) {
            List<String> actions = Arrays.asList("add", "remove", "list", "whitelist");
            return filter(actions, args[2]);
        } else if (args.length == 4) {
            if (args[2].equalsIgnoreCase("add")) {
                return new ArrayList<>();
            } else if (args[2].equalsIgnoreCase("whitelist")) {
                List<String> whitelistActions = Arrays.asList("add", "remove", "list");
                return filter(whitelistActions, args[3]);
            }
        } else if (args.length == 5 && args[2].equalsIgnoreCase("whitelist") && 
                   args[3].equalsIgnoreCase("add")) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    private List<String> completeRegionName(String partial) {
        return filter(getRegionNames(), partial);
    }

    private List<String> completeProfileName(String partial) {
        List<String> profiles = Arrays.stream(RegionProfile.values())
                .map(RegionProfile::getName)
                .collect(Collectors.toList());
        return filter(profiles, partial);
    }

    private List<String> getRegionNames() {
        Collection<Region> regions = regionManager.getAllRegions();
        return regions.stream()
                .map(Region::getName)
                .collect(Collectors.toList());
    }

    private List<String> filter(List<String> list, String partial) {
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(partial.toLowerCase()))
                .collect(Collectors.toList());
    }
}
