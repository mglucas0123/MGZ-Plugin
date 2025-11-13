package mglucas0123.commands;

import mglucas0123.Principal;
import mglucas0123.region.*;
import mglucas0123.region.SelectionManager.Selection;
import org.bukkit.Location;

import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class RegionCommand implements CommandExecutor {
    
    private RegionManager regionManager;
    private SelectionManager selectionManager;
    
    public RegionCommand() {
        this.regionManager = Principal.plugin.getRegionManager();
        this.selectionManager = Principal.plugin.getSelectionManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("mgz.region.admin")) {
            player.sendMessage("§cVocê não tem permissão para usar este comando!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "wand":
                giveWand(player);
                break;
                
            case "expand":
                if (args.length < 2) {
                    player.sendMessage("§cUso: /region expand <direção>");
                    player.sendMessage("§7Direções: vert (vertical - bedrock ao limite)");
                    return true;
                }
                expandSelection(player, args[1]);
                break;
                
            case "create":
            case "criar":
                if (args.length < 2) {
                    player.sendMessage("§cUso: /region create <nome> [profile]");
                    player.sendMessage("§7Exemplo: /region create spawn safezone");
                    player.sendMessage("§7Use /region profiles para ver profiles disponíveis");
                    return true;
                }
                String profileName = args.length >= 3 ? args[2] : null;
                createRegion(player, args[1], profileName);
                break;
                
            case "delete":
            case "deletar":
                if (args.length < 2) {
                    player.sendMessage("§cUso: /region delete <nome>");
                    return true;
                }
                deleteRegion(player, args[1]);
                break;
                
            case "flag":
                if (args.length < 4) {
                    player.sendMessage("§cUso: /region flag <região|__global__> <flag> <true/false>");
                    player.sendMessage("§7Exemplo: /region flag __global__ pvp false");
                    return true;
                }
                setFlag(player, args[1], args[2], args[3]);
                break;
                
            case "info":
                if (args.length < 2) {
                    player.sendMessage("§cUso: /region info <nome>");
                    return true;
                }
                showInfo(player, args[1]);
                break;
                
            case "list":
            case "listar":
                listRegions(player);
                break;
                
            case "priority":
                if (args.length < 3) {
                    player.sendMessage("§cUso: /region priority <região> <prioridade>");
                    return true;
                }
                setPriority(player, args[1], args[2]);
                break;
                
            case "flags":
                listFlags(player);
                break;
                
            case "profiles":
            case "templates":
                listProfiles(player);
                break;
                
            case "profileinfo":
            case "template":
                if (args.length < 2) {
                    player.sendMessage("§cUso: /region profileinfo <profile>");
                    return true;
                }
                showProfileInfo(player, args[1]);
                break;
                
            case "blockitem":
                if (args.length < 3) {
                    player.sendMessage("§cUso: /region blockitem <região|__global__> <add|remove|list|whitelist> [item] [all]");
                    player.sendMessage("§7Exemplos:");
                    player.sendMessage("§7  /region blockitem spawn add DIAMOND_SWORD");
                    player.sendMessage("§7  /region blockitem __global__ add hand §7(bloqueia item da mão)");
                    player.sendMessage("§7  /region blockitem __global__ add hand all §c(bloqueio total - não pode nem segurar)");
                    player.sendMessage("§7  /region blockitem spawn remove DIAMOND_SWORD");
                    player.sendMessage("§7  /region blockitem spawn list");
                    player.sendMessage("§7  /region blockitem spawn whitelist add DIAMOND_PICKAXE");
                    return true;
                }
                handleBlockItemSubcommand(player, args);
                break;
                
            case "unblockitem":
            case "allowitem":
                if (args.length < 3) {
                    player.sendMessage("§cUso: /region unblockitem <região|__global__> <item>");
                    return true;
                }
                unblockItemInRegion(player, args[1], args[2]);
                break;
                
            case "blockedlist":
            case "listitems":
                if (args.length < 2) {
                    player.sendMessage("§cUso: /region blockedlist <região|__global__>");
                    return true;
                }
                listBlockedItems(player, args[1]);
                break;
                
            case "clearitems":
                if (args.length < 2) {
                    player.sendMessage("§cUso: /region clearitems <região|__global__>");
                    return true;
                }
                clearBlockedItems(player, args[1]);
                break;
                
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void giveWand(Player player) {
        ItemStack wand = new ItemStack(Material.STICK);
        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName("§6§lVarinha de Seleção");
        meta.setLore(Arrays.asList(
            "§7Clique esquerdo para definir posição 1",
            "§7Clique direito para definir posição 2"
        ));
        wand.setItemMeta(meta);
        
        player.getInventory().addItem(wand);
        player.sendMessage("§aVocê recebeu a varinha de seleção!");
        player.sendMessage("§7Clique esquerdo = Posição 1 | Clique direito = Posição 2");
    }
    
    private void expandSelection(Player player, String direction) {
        if (!selectionManager.hasCompleteSelection(player)) {
            player.sendMessage("§cVocê precisa selecionar uma área primeiro!");
            player.sendMessage("§7Use /region wand para obter a varinha de seleção");
            return; 
        }
        
        if (!direction.equalsIgnoreCase("vert") && !direction.equalsIgnoreCase("vertical")) {
            player.sendMessage("§cDireção inválida!");
            player.sendMessage("§7Use: /region expand vert");
            return;
        }
        
        Selection selection = selectionManager.getSelection(player);
        Location pos1Before = selection.getPos1().clone();
        Location pos2Before = selection.getPos2().clone();
        
        selectionManager.expandVertical(player);
        
        Selection updatedSelection = selectionManager.getSelection(player);
        
        player.sendMessage("§a§l✔ Seleção expandida verticalmente!");
        player.sendMessage("§7De: §fY " + (int)Math.min(pos1Before.getY(), pos2Before.getY()) + 
                          " §7até §fY " + (int)Math.max(pos1Before.getY(), pos2Before.getY()));
        player.sendMessage("§7Para: §fY " + updatedSelection.getPos1().getWorld().getMinHeight() + 
                          " §7(bedrock) até §fY " + (updatedSelection.getPos1().getWorld().getMaxHeight() - 1) + " §7(limite)");
        player.sendMessage("§7Novo volume: §f" + updatedSelection.getVolume() + " blocos");
    }
    
    private void createRegion(Player player, String name, String profileName) {
        if (!selectionManager.hasCompleteSelection(player)) {
            player.sendMessage("§cVocê precisa selecionar uma área primeiro!");
            player.sendMessage("§7Use /region wand para obter a varinha de seleção");
            return;
        }
        
        if (regionManager.regionExists(name)) {
            player.sendMessage("§cJá existe uma região com este nome!");
            return;
        }
        
        Selection selection = selectionManager.getSelection(player);
        Location pos1 = selection.getPos1();
        Location pos2 = selection.getPos2();
        
        regionManager.createRegion(name, pos1.getWorld().getName(), pos1, pos2);
        Region region = regionManager.getRegion(name);
        
        RegionProfile profile = null;
        if (profileName != null) {
            profile = RegionProfile.fromString(profileName);
            if (profile == null) {
                player.sendMessage("§eAviso: Profile '§f" + profileName + "§e' não encontrado!");
                player.sendMessage("§7Use /region profiles para ver profiles disponíveis");
            } else {
                profile.applyFlags(region);
                region.setPriority(profile.getDefaultPriority());
                regionManager.saveRegions();
            }
        }
        
        player.sendMessage("§a§l✔ Região criada com sucesso!");
        player.sendMessage("§7Nome: §f" + name);
        player.sendMessage("§7Mundo: §f" + pos1.getWorld().getName());
        player.sendMessage("§7Volume: §f" + selection.getVolume() + " blocos");
        
        if (profile != null) {
            player.sendMessage("§7Profile: §a" + profile.getName() + " §7- " + profile.getDescription());
            player.sendMessage("§7Prioridade: §f" + profile.getDefaultPriority());
            player.sendMessage("§aFlags do profile aplicadas automaticamente!");
        } else {
            player.sendMessage("§7Use §f/region flag " + name + " <flag> <valor> §7para configurar");
        }
        
        selectionManager.clearSelection(player);
    }
    
    private void deleteRegion(Player player, String name) {
        if (!regionManager.regionExists(name)) {
            player.sendMessage("§cRegião não encontrada!");
            return;
        }
        
        regionManager.deleteRegion(name);
        player.sendMessage("§aRegião §f" + name + " §adeletada com sucesso!");
    }
    
    private void setFlag(Player player, String regionName, String flagName, String value) {
        Region region;
        
        if (regionName.equalsIgnoreCase("__global__")) {
            String worldName = player.getWorld().getName();
            region = regionManager.getGlobalRegion(worldName);
            
            if (region == null) {
                regionManager.createGlobalRegion(worldName);
                region = regionManager.getGlobalRegion(worldName);
                player.sendMessage("§aRegião global criada para o mundo §f" + worldName);
            }
        } else {
            region = regionManager.getRegion(regionName);
            if (region == null) {
                player.sendMessage("§cRegião não encontrada!");
                return;
            }
        }
        
        RegionFlag flag = RegionFlag.fromString(flagName);
        if (flag == null) {
            player.sendMessage("§cFlag inválida! Use /region flags para ver as flags disponíveis");
            return;
        }
        
        boolean flagValue;
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("allow") || value.equalsIgnoreCase("sim")) {
            flagValue = true;
        } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("deny") || value.equalsIgnoreCase("nao")) {
            flagValue = false;
        } else {
            player.sendMessage("§cValor inválido! Use true ou false");
            return;
        }
        
        region.setFlag(flag, flagValue);
        regionManager.saveRegions();
        
        player.sendMessage("§aFlag atualizada!");
        if (regionName.equalsIgnoreCase("__global__")) {
            player.sendMessage("§7Região: §f__global__ §7(Mundo: §f" + player.getWorld().getName() + "§7)");
        } else {
            player.sendMessage("§7Região: §f" + regionName);
        }
        player.sendMessage("§7Flag: §f" + flag.name());
        player.sendMessage("§7Valor: " + (flagValue ? "§aPermitido" : "§cNegado"));
    }
    
    private void showInfo(Player player, String name) {
        Region region;
        
        // Verificar se é região global
        if (name.equalsIgnoreCase("__global__")) {
            String worldName = player.getWorld().getName();
            region = regionManager.getGlobalRegion(worldName);
            if (region == null) {
                player.sendMessage("§cNenhuma região global configurada para este mundo!");
                player.sendMessage("§7Use §f/region flag __global__ <flag> <valor> §7para criar");
                return;
            }
        } else {
            region = regionManager.getRegion(name);
            if (region == null) {
                player.sendMessage("§cRegião não encontrada!");
                return;
            }
        }
        
        player.sendMessage("§6§l========== Informações da Região ==========");
        player.sendMessage("§eNome: §f" + region.getName());
        player.sendMessage("§eMundo: §f" + region.getWorldName());
        player.sendMessage("§eTipo: §f" + (region.isGlobal() ? "Global (mundo inteiro)" : "Área específica"));
        player.sendMessage("§ePrioridade: §f" + region.getPriority());
        if (!region.isGlobal()) {
            player.sendMessage("§eVolume: §f" + region.getVolume() + " blocos");
        }
        player.sendMessage("");
        player.sendMessage("§6§lFlags:");
        
        for (RegionFlag flag : RegionFlag.values()) {
            boolean value = region.getFlag(flag);
            String status = value ? "§aPermitido" : "§cNegado";
            player.sendMessage("  §7" + flag.name() + ": " + status);
        }
        player.sendMessage("§6§l==========================================");
    }
    
    private void listRegions(Player player) {
        if (regionManager.getAllRegions().isEmpty()) {
            player.sendMessage("§cNenhuma região criada ainda!");
            return;
        }
        
        player.sendMessage("§6§l========== Regiões ==========");
        for (Region region : regionManager.getAllRegions()) {
            player.sendMessage("§e" + region.getName() + " §7(Mundo: " + region.getWorldName() + 
                              ", Prioridade: " + region.getPriority() + ")");
        }
        player.sendMessage("§6§l============================");
    }
    
    private void setPriority(Player player, String regionName, String priorityStr) {
        Region region = regionManager.getRegion(regionName);
        if (region == null) {
            player.sendMessage("§cRegião não encontrada!");
            return;
        }
        
        try {
            int priority = Integer.parseInt(priorityStr);
            region.setPriority(priority);
            regionManager.saveRegions();
            player.sendMessage("§aPrioridade da região §f" + regionName + " §adefinida para §f" + priority);
        } catch (NumberFormatException e) {
            player.sendMessage("§cPrioridade inválida! Use um número inteiro");
        }
    }
    
    private void listFlags(Player player) {
        player.sendMessage("§6§l========== Flags Disponíveis ==========");
        for (RegionFlag flag : RegionFlag.values()) {
            player.sendMessage("§e" + flag.name() + " §7- " + flag.getDescription());
        }
        player.sendMessage("§6§l======================================");
    }
    
    private void listProfiles(Player player) {
        player.sendMessage("§6§l========== Profiles de Região ==========");
        player.sendMessage("§7Use: §f/region create <nome> <profile>");
        player.sendMessage("");
        
        for (RegionProfile profile : RegionProfile.values()) {
            player.sendMessage("§e" + profile.getName() + " §7(Prioridade: " + profile.getDefaultPriority() + ")");
            player.sendMessage("  §8▪ §7" + profile.getDescription());
        }
        
        player.sendMessage("");
        player.sendMessage("§7Use §f/region profileinfo <profile> §7para detalhes");
        player.sendMessage("§6§l=======================================");
    }
    
    private void showProfileInfo(Player player, String profileName) {
        RegionProfile profile = RegionProfile.fromString(profileName);
        
        if (profile == null) {
            player.sendMessage("§cProfile não encontrado!");
            player.sendMessage("§7Use /region profiles para ver profiles disponíveis");
            return;
        }
        
        player.sendMessage("§6§l========== Profile: " + profile.getName() + " ==========");
        player.sendMessage("§7Descrição: §f" + profile.getDescription());
        player.sendMessage("§7Prioridade Padrão: §f" + profile.getDefaultPriority());
        player.sendMessage("");
        player.sendMessage("§6§lFlags Configuradas:");
        
        Map<RegionFlag, Boolean> flags = profile.getFlags();
        for (Map.Entry<RegionFlag, Boolean> entry : flags.entrySet()) {
            String status = entry.getValue() ? "§aPermitido" : "§cNegado";
            player.sendMessage("  §7" + entry.getKey().name() + ": " + status);
        }
        
        player.sendMessage("§6§l==========================================");
    }
    
    private void handleBlockItemSubcommand(Player player, String[] args) {
        // args[0] = "blockitem"
        // args[1] = região
        // args[2] = subcomando (add/remove/list/whitelist)
        // args[3] = item (opcional para add/remove) OU subcomando da whitelist (add/remove/list)
        // args[4] = item (para whitelist add/remove)
        
        String regionName = args[1];
        String subCommand = args[2].toLowerCase();
        
        switch (subCommand) {
            case "add":
                if (args.length < 4) {
                    player.sendMessage("§cUso: /region blockitem <região> add <item|hand> [all]");
                    player.sendMessage("§7Adicione 'all' no final para bloquear até mesmo segurar o item");
                    player.sendMessage("§7Exemplo: /region blockitem __global__ add hand all");
                    return;
                }
                boolean totalBlock = args.length >= 5 && args[4].equalsIgnoreCase("all");
                blockItemInRegion(player, regionName, args[3], totalBlock);
                break;
                
            case "remove":
                if (args.length < 4) {
                    player.sendMessage("§cUso: /region blockitem <região> remove <item>");
                    return;
                }
                unblockItemInRegion(player, regionName, args[3]);
                break;
                
            case "list":
                listBlockedItems(player, regionName);
                break;
                
            case "whitelist":
                if (args.length < 4) {
                    player.sendMessage("§cUso: /region blockitem <região> whitelist <add|remove|list> [item]");
                    player.sendMessage("§7Exemplos:");
                    player.sendMessage("§7  /region blockitem spawn whitelist add DIAMOND_PICKAXE");
                    player.sendMessage("§7  /region blockitem spawn whitelist add hand");
                    player.sendMessage("§7  /region blockitem spawn whitelist remove DIAMOND_PICKAXE");
                    player.sendMessage("§7  /region blockitem spawn whitelist list");
                    return;
                }
                handleWhitelistSubcommand(player, regionName, args);
                break;
                
            default:
                player.sendMessage("§cSubcomando inválido! Use: add, remove, list ou whitelist");
                player.sendMessage("§7Exemplo: /region blockitem spawn add DIAMOND_SWORD");
                break;
        }
    }
    
    private void blockItemInRegion(Player player, String regionName, String itemName, boolean totalBlock) {
        Region region = getRegionOrGlobal(player, regionName);
        if (region == null) return;
        
        String material;
        if (itemName.equalsIgnoreCase("hand")) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage("§c§l✖ §cSua mão está vazia!");
                player.sendMessage("§7Segure um item na mão ou especifique o nome do material");
                return;
            }
            material = item.getType().name();
        } else {
            material = itemName.toUpperCase();
            // Validar se o material existe
            try {
                Material.valueOf(material);
            } catch (IllegalArgumentException e) {
                player.sendMessage("§c§l✖ §cO item '§f" + itemName + "§c' não existe!");
                player.sendMessage("§7Use um material válido do Minecraft");
                player.sendMessage("§7Exemplo: DIAMOND_SWORD, STICK, CHEST, etc.");
                return;
            }
        }
        
        region.addBlockedItem(material, totalBlock);
        regionManager.saveRegions();
        
        player.sendMessage("§a§lItem bloqueado!");
        player.sendMessage("§7Região: §f" + (regionName.equalsIgnoreCase("__global__") ? "__global__ (" + player.getWorld().getName() + ")" : regionName));
        player.sendMessage("§7Item: §f" + material);
        if (totalBlock) {
            player.sendMessage("§c§lBLOQUEIO TOTAL: §cJogadores não poderão nem segurar este item");
        } else {
            player.sendMessage("§7Jogadores não poderão interagir (clicar direito) com este item");
        }
    }
    
    private void unblockItemInRegion(Player player, String regionName, String itemName) {
        Region region = getRegionOrGlobal(player, regionName);
        if (region == null) return;
        
        String material = itemName.toUpperCase();
        
        if (!region.isItemBlocked(material)) {
            player.sendMessage("§cEste item não está bloqueado nesta região!");
            return;
        }
        
        region.removeBlockedItem(material);
        regionManager.saveRegions();
        
        player.sendMessage("§a§lItem desbloqueado!");
        player.sendMessage("§7Região: §f" + (regionName.equalsIgnoreCase("__global__") ? "__global__ (" + player.getWorld().getName() + ")" : regionName));
        player.sendMessage("§7Item: §f" + material);
    }
    
    private void listBlockedItems(Player player, String regionName) {
        Region region = getRegionOrGlobal(player, regionName);
        if (region == null) return;
        
        Set<String> blocked = region.getBlockedItems();
        Set<String> totalBlocked = region.getTotalBlockedItems();
        
        if (blocked.isEmpty()) {
            player.sendMessage("§eNenhum item bloqueado nesta região.");
            return;
        }
        
        player.sendMessage("§6§l========== Itens Bloqueados ==========");
        player.sendMessage("§7Região: §f" + (regionName.equalsIgnoreCase("__global__") ? "__global__ (" + player.getWorld().getName() + ")" : regionName));
        player.sendMessage("");
        
        for (String item : blocked) {
            if (totalBlocked.contains(item)) {
                player.sendMessage("  §4§l✖ §c" + item + " §7(bloqueio total)");
            } else {
                player.sendMessage("  §c✖ §f" + item);
            }
        }
        
        player.sendMessage("");
        player.sendMessage("§7Total: §f" + blocked.size() + " itens bloqueados");
        if (!totalBlocked.isEmpty()) {
            player.sendMessage("§7Bloqueio total: §c" + totalBlocked.size() + " itens");
        }
        player.sendMessage("§6§l=====================================");
    }
    
    private void clearBlockedItems(Player player, String regionName) {
        Region region = getRegionOrGlobal(player, regionName);
        if (region == null) return;
        
        int count = region.getBlockedItems().size();
        
        if (count == 0) {
            player.sendMessage("§eNenhum item bloqueado nesta região.");
            return;
        }
        
        region.clearBlockedItems();
        regionManager.saveRegions();
        
        player.sendMessage("§a§lItens desbloqueados!");
        player.sendMessage("§7Região: §f" + (regionName.equalsIgnoreCase("__global__") ? "__global__ (" + player.getWorld().getName() + ")" : regionName));
        player.sendMessage("§7Total de §f" + count + " §7itens foram desbloqueados");
    }
    
    private void handleWhitelistSubcommand(Player player, String regionName, String[] args) {
        // args[3] = subcomando (add/remove/list)
        // args[4] = item (para add/remove)
        
        String whitelistCommand = args[3].toLowerCase();
        
        switch (whitelistCommand) {
            case "add":
                if (args.length < 5) {
                    player.sendMessage("§cUso: /region blockitem <região> whitelist add <item|hand>");
                    return;
                }
                addItemToWhitelist(player, regionName, args[4]);
                break;
                
            case "remove":
                if (args.length < 5) {
                    player.sendMessage("§cUso: /region blockitem <região> whitelist remove <item>");
                    return;
                }
                removeItemFromWhitelist(player, regionName, args[4]);
                break;
                
            case "list":
                listWhitelistedItems(player, regionName);
                break;
                
            default:
                player.sendMessage("§cSubcomando inválido! Use: add, remove ou list");
                player.sendMessage("§7Exemplo: /region blockitem spawn whitelist add DIAMOND_PICKAXE");
                break;
        }
    }
    
    private void addItemToWhitelist(Player player, String regionName, String itemName) {
        Region region = getRegionOrGlobal(player, regionName);
        if (region == null) return;
        
        String material;
        if (itemName.equalsIgnoreCase("hand")) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage("§c§l✖ §cSua mão está vazia!");
                player.sendMessage("§7Segure um item na mão ou especifique o nome do material");
                return;
            }
            material = item.getType().name();
        } else {
            material = itemName.toUpperCase();
            // Validar se o material existe
            try {
                Material.valueOf(material);
            } catch (IllegalArgumentException e) {
                player.sendMessage("§c§l✖ §cO item '§f" + itemName + "§c' não existe!");
                player.sendMessage("§7Use um material válido do Minecraft");
                player.sendMessage("§7Exemplo: DIAMOND_PICKAXE, CHEST, FURNACE, etc.");
                return;
            }
        }
        
        region.addWhitelistedItem(material);
        regionManager.saveRegions();
        
        player.sendMessage("§a§lItem adicionado à whitelist!");
        player.sendMessage("§7Região: §f" + (regionName.equalsIgnoreCase("__global__") ? "__global__ (" + player.getWorld().getName() + ")" : regionName));
        player.sendMessage("§7Item: §f" + material);
        player.sendMessage("");
        player.sendMessage("§e⚠ Comportamento da Whitelist:");
        player.sendMessage("§7• Flag INTERACT = §cfalse §7→ Item §apermitido");
        player.sendMessage("§7• Flag INTERACT = §atrue §7→ Item §cbloqueado");
    }
    
    private void removeItemFromWhitelist(Player player, String regionName, String itemName) {
        Region region = getRegionOrGlobal(player, regionName);
        if (region == null) return;
        
        String material = itemName.toUpperCase();
        
        if (!region.isItemWhitelisted(material)) {
            player.sendMessage("§cEste item não está na whitelist desta região!");
            return;
        }
        
        region.removeWhitelistedItem(material);
        regionManager.saveRegions();
        
        player.sendMessage("§a§lItem removido da whitelist!");
        player.sendMessage("§7Região: §f" + (regionName.equalsIgnoreCase("__global__") ? "__global__ (" + player.getWorld().getName() + ")" : regionName));
        player.sendMessage("§7Item: §f" + material);
    }
    
    private void listWhitelistedItems(Player player, String regionName) {
        Region region = getRegionOrGlobal(player, regionName);
        if (region == null) return;
        
        Set<String> whitelisted = region.getWhitelistedItems();
        
        if (whitelisted.isEmpty()) {
            player.sendMessage("§eNenhum item na whitelist desta região.");
            return;
        }
        
        player.sendMessage("§a§l========== Whitelist de Itens ==========");
        player.sendMessage("§7Região: §f" + (regionName.equalsIgnoreCase("__global__") ? "__global__ (" + player.getWorld().getName() + ")" : regionName));
        player.sendMessage("");
        player.sendMessage("§e⚠ Comportamento da Whitelist:");
        player.sendMessage("§7• Flag INTERACT = §cfalse §7→ Itens permitidos");
        player.sendMessage("§7• Flag INTERACT = §atrue §7→ Itens bloqueados");
        player.sendMessage("");
        
        for (String item : whitelisted) {
            player.sendMessage("  §a✔ §f" + item);
        }
        
        player.sendMessage("");
        player.sendMessage("§7Total: §f" + whitelisted.size() + " itens na whitelist");
        player.sendMessage("§a§l========================================");
    }
    
    private Region getRegionOrGlobal(Player player, String regionName) {
        if (regionName.equalsIgnoreCase("__global__")) {
            String worldName = player.getWorld().getName();
            Region region = regionManager.getGlobalRegion(worldName);
            
            if (region == null) {
                regionManager.createGlobalRegion(worldName);
                region = regionManager.getGlobalRegion(worldName);
                player.sendMessage("§aRegião global criada para o mundo §f" + worldName);
            }
            return region;
        } else {
            Region region = regionManager.getRegion(regionName);
            if (region == null) {
                player.sendMessage("§cRegião não encontrada!");
                return null;
            }
            return region;
        }
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6§l========== Sistema de Regiões MGZ ==========");
        player.sendMessage("§e/region wand §7- Recebe a varinha de seleção");
        player.sendMessage("§e/region expand vert §7- Expande seleção verticalmente");
        player.sendMessage("§e/region create <nome> [profile] §7- Cria região");
        player.sendMessage("§e/region delete <nome> §7- Deleta uma região");
        player.sendMessage("§e/region flag <região> <flag> <valor> §7- Define flag");
        player.sendMessage("§e/region flag __global__ <flag> <valor> §7- Flag global");
        player.sendMessage("§e/region info <nome|__global__> §7- Informações");
        player.sendMessage("§e/region list §7- Lista todas as regiões");
        player.sendMessage("§e/region priority <região> <número> §7- Prioridade");
        player.sendMessage("§e/region blockitem <região> add/remove/list §7- Itens");
        player.sendMessage("§e/region blockedlist <região> §7- Lista bloqueados");
        player.sendMessage("§e/region clearitems <região> §7- Limpa bloqueados");
        player.sendMessage("§e/region profiles §7- Lista profiles");
        player.sendMessage("§e/region flags §7- Lista flags");
        player.sendMessage("§6§l============================================");
        player.sendMessage("§a§lNovo! §7Expanda seleção: §f/region expand vert");
    }
}
