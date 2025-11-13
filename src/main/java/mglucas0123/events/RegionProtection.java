package mglucas0123.events;

import mglucas0123.Principal;
import mglucas0123.region.Region;
import mglucas0123.region.RegionFlag;
import mglucas0123.region.RegionManager;
import mglucas0123.region.SelectionManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
// import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
// import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class RegionProtection implements Listener {
    
    private RegionManager regionManager;
    private SelectionManager selectionManager;
    
    public RegionProtection() {
        this.regionManager = Principal.plugin.getRegionManager();
        this.selectionManager = Principal.plugin.getSelectionManager();
    }
    
    private void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        
        if (player.hasPermission("mgz.region.bypass")) {
            return;
        }
        
        if (!regionManager.isAllowed(location, RegionFlag.BREAK)) {
            event.setCancelled(true);
            sendActionBar(player, "§c§l⚠ §cVocê não pode quebrar blocos aqui §c§l⚠");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        
        if (player.hasPermission("mgz.region.bypass")) {
            return;
        }
        
        if (!regionManager.isAllowed(location, RegionFlag.BUILD)) {
            event.setCancelled(true);
            sendActionBar(player, "§c§l⚠ §cVocê não pode construir aqui §c§l⚠");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityPlace(EntityPlaceEvent event) {
        Player player = event.getPlayer();
        Location location = event.getEntity().getLocation();
        
        if (player == null) {
            return;
        }

        if (event.getEntity() instanceof ArmorStand) {
            boolean armorStandEnabled = Principal.plugin.getConfig().getBoolean("ArmorStand.Habilitado", false);
            if (!armorStandEnabled) {
                event.setCancelled(true);
                sendActionBar(player, "§c§l✖ §cArmor stands estão desativadas neste servidor §c§l✖");
                return;
            }
        }
        
        if (player.hasPermission("mgz.region.bypass")) {
            return;
        }
        
        if (!regionManager.isAllowed(location, RegionFlag.BUILD)) {
            event.setCancelled(true);
            sendActionBar(player, "§c§l⚠ §cVocê não pode colocar entidades aqui §c§l⚠");
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // PRIORIDADE MÁXIMA: Verificar itens bloqueados ANTES de qualquer outra verificação
        if (event.getItem() != null && event.getItem().getType() != Material.AIR) {
            Location location = event.getClickedBlock() != null ? 
                event.getClickedBlock().getLocation() : player.getLocation();
            
            if (!player.hasPermission("mgz.region.bypass")) {
                Region region = regionManager.getHighestPriorityRegion(location);
                
                if (region != null) {
                    String itemType = event.getItem().getType().name();
                    boolean isWhitelisted = region.isItemWhitelisted(itemType);
                    boolean interactFlag = region.getFlag(RegionFlag.INTERACT);
                    
                    // Verificar whitelist primeiro
                    if (isWhitelisted) {
                        if (interactFlag) {
                            // INTERACT = true, item na whitelist → BLOQUEAR
                            event.setCancelled(true);
                            event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                            event.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                            sendActionBar(player, "§c§l✖ §cItem na whitelist bloqueado (INTERACT ativo) §c§l✖");
                            return;
                        } else {
                            // INTERACT = false, item na whitelist → PERMITIR
                            return; // Permite o uso
                        }
                    }
                    
                    // Sistema de bloqueio normal (para itens não whitelistados)
                    if (region.isItemBlocked(itemType)) {
                        // PRIMEIRO: Remover/trocar o item da mão IMEDIATAMENTE
                        org.bukkit.inventory.PlayerInventory inv = player.getInventory();
                        ItemStack blockedItem = event.getItem().clone();
                        int mainHandSlot = inv.getHeldItemSlot();
                        boolean isMainHand = (event.getHand() == org.bukkit.inventory.EquipmentSlot.HAND);
                        
                        // Procurar primeiro slot vazio para mover o item bloqueado
                        boolean foundEmpty = false;
                        for (int i = 0; i < 36; i++) {
                            if (i == mainHandSlot) continue;
                            
                            ItemStack slotItem = inv.getItem(i);
                            if (slotItem == null || slotItem.getType() == Material.AIR) {
                                inv.setItem(i, blockedItem);
                                if (isMainHand) {
                                    inv.setItemInMainHand(new ItemStack(Material.AIR));
                                } else {
                                    inv.setItemInOffHand(new ItemStack(Material.AIR));
                                }
                                foundEmpty = true;
                                break;
                            }
                        }
                        
                        // Se não encontrou slot vazio, trocar com o primeiro item disponível
                        if (!foundEmpty) {
                            for (int i = 0; i < 36; i++) {
                                if (i == mainHandSlot) continue;
                                
                                ItemStack slotItem = inv.getItem(i);
                                if (slotItem != null && slotItem.getType() != Material.AIR) {
                                    inv.setItem(i, blockedItem);
                                    if (isMainHand) {
                                        inv.setItemInMainHand(slotItem);
                                    } else {
                                        inv.setItemInOffHand(slotItem);
                                    }
                                    break;
                                }
                            }
                        }
                        
                        // DEPOIS: Cancelar o evento completamente
                        event.setCancelled(true);
                        event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                        event.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                        
                        sendActionBar(player, "§c§l✖ §cItem bloqueado nesta região §c§l✖");
                        return;
                    }
                }
            }
        }
        
        if (event.getItem() != null && event.getItem().getType() == Material.STICK &&
            event.getItem().hasItemMeta() && 
            event.getItem().getItemMeta().getDisplayName().equals("§6§lVarinha de Seleção")) {
            
            if (!player.hasPermission("mgz.region.admin")) {
                return;
            }
            
            if (event.getClickedBlock() == null) {
                return;
            }
            
            Location location = event.getClickedBlock().getLocation();
            
            if (event.getAction().name().contains("LEFT")) {
                selectionManager.setPos1(player, location);
                player.sendMessage("§aPosição 1 definida em: §f" + 
                    (int)location.getX() + ", " + (int)location.getY() + ", " + (int)location.getZ());
                event.setCancelled(true);
            } else if (event.getAction().name().contains("RIGHT")) {
                selectionManager.setPos2(player, location);
                player.sendMessage("§aPosição 2 definida em: §f" + 
                    (int)location.getX() + ", " + (int)location.getY() + ", " + (int)location.getZ());
                event.setCancelled(true);
                
                if (selectionManager.hasCompleteSelection(player)) {
                    SelectionManager.Selection selection = selectionManager.getSelection(player);
                    player.sendMessage("§aÁrea selecionada! Volume: §f" + selection.getVolume() + " blocos");
                    player.sendMessage("§7Use §f/region create <nome> §7para criar a região");
                }
            }
            return;
        }
        
        Location location = event.getClickedBlock() != null ? 
            event.getClickedBlock().getLocation() : player.getLocation();
        
        if (player.hasPermission("mgz.region.bypass")) {
            return;
        }
        
        Region region = regionManager.getHighestPriorityRegion(location);
        
        // Verificar spawn eggs
        if (event.getItem() != null && event.getItem().getType().name().contains("_SPAWN_EGG")) {
            if (!regionManager.isAllowed(location, RegionFlag.BUILD)) {
                event.setCancelled(true);
                event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                event.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                sendActionBar(player, "§c§l⚠ §cVocê não pode usar spawn eggs aqui §c§l⚠");
                return;
            }
        }
        
        // Verificar whitelist para BLOCO CLICADO (baús, portas, etc.)
        if (event.getClickedBlock() != null) {
            String blockType = event.getClickedBlock().getType().name();
            
            if (region != null) {
                boolean isBlockWhitelisted = region.isItemWhitelisted(blockType);
                boolean interactFlag = region.getFlag(RegionFlag.INTERACT);
                
                // Lógica da whitelist para blocos:
                // Se bloco está na whitelist e INTERACT = false → Permitir
                // Se bloco está na whitelist e INTERACT = true → Bloquear
                if (isBlockWhitelisted) {
                    if (interactFlag) {
                        // INTERACT = true, bloco na whitelist → BLOQUEAR
                        event.setCancelled(true);
                        sendActionBar(player, "§c§l✖ §cBloco na whitelist bloqueado (INTERACT ativo) §c§l✖");
                        return;
                    } else {
                        // INTERACT = false, bloco na whitelist → PERMITIR
                        return; // Permite a interação com o bloco
                    }
                }
            }
            
            // Verificação normal do flag INTERACT (para blocos não whitelistados)
            if (!regionManager.isAllowed(location, RegionFlag.INTERACT)) {
                event.setCancelled(true);
                sendActionBar(player, "§c§l⚠ §cVocê não pode interagir aqui §c§l⚠");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getDamager();
        Location location = victim.getLocation();
        
        if (attacker.hasPermission("mgz.region.bypass")) {
            return;
        }
        
        if (!regionManager.isAllowed(location, RegionFlag.PVP)) {
            event.setCancelled(true);
            sendActionBar(attacker, "§c§l⚔ §cPvP desabilitado aqui §c§l⚔");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onEntityDamage(EntityDamageEvent event) {
        Location location = event.getEntity().getLocation();
        
        if (event.getEntity() instanceof Player) {
            Region region = regionManager.getHighestPriorityRegion(location);
            if (region != null && region.getFlag(RegionFlag.INVINCIBLE)) {
                event.setCancelled(true);
            }
        }
        
        if (event.getEntity() instanceof Hanging) {
            Region region = regionManager.getHighestPriorityRegion(location);
            boolean protect = (region == null) || region.getFlag(RegionFlag.INVINCIBLE) || !region.getFlag(RegionFlag.BREAK);
            if (protect) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        Location location = event.getLocation();
        
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            Player nearestPlayer = null;
            double minDistance = 10.0;
            
            for (Player player : location.getWorld().getPlayers()) {
                double distance = player.getLocation().distance(location);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestPlayer = player;
                }
            }
            
            if (nearestPlayer != null && !nearestPlayer.hasPermission("mgz.region.bypass")) {
                if (!regionManager.isAllowed(location, RegionFlag.BUILD)) {
                    event.setCancelled(true);
                    sendActionBar(nearestPlayer, "§c§l⚠ §cVocê não pode usar spawn eggs aqui §c§l⚠");
                    return;
                }
            }
        }
        
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM &&
            event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            
            if (!regionManager.isAllowed(location, RegionFlag.MOB_SPAWN)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        Location location = event.getBlock().getLocation();
        
        if (!regionManager.isAllowed(location, RegionFlag.FIRE_SPREAD)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBurn(BlockBurnEvent event) {
        Location location = event.getBlock().getLocation();
        
        if (!regionManager.isAllowed(location, RegionFlag.FIRE_SPREAD)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onExplosion(EntityExplodeEvent event) {
        Location location = event.getLocation();
        
        if (!regionManager.isAllowed(location, RegionFlag.EXPLOSION)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        
        if (player.hasPermission("mgz.region.bypass")) {
            return;
        }
        
        if (!regionManager.isAllowed(location, RegionFlag.ITEM_DROP)) {
            event.setCancelled(true);
            sendActionBar(player, "§c§l✖ §cVocê não pode dropar itens aqui §c§l✖");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        Location location = event.getItem().getLocation();
        
        if (player.hasPermission("mgz.region.bypass")) {
            return;
        }
        
        // Verificar flag ITEM_PICKUP primeiro
        if (!regionManager.isAllowed(location, RegionFlag.ITEM_PICKUP)) {
            event.setCancelled(true);
            return;
        }
        
        // Verificar se o item tem bloqueio total
        Region region = regionManager.getHighestPriorityRegion(location);
        if (region != null) {
            ItemStack item = event.getItem().getItemStack();
            if (item != null && item.getType() != Material.AIR) {
                String itemType = item.getType().name();
                if (region.isItemTotalBlocked(itemType)) {
                    org.bukkit.inventory.PlayerInventory inv = player.getInventory();
                    
                    // Verificar se há espaço no inventário (slots 9-35)
                    boolean hasInventorySpace = false;
                    for (int i = 9; i < 36; i++) {
                        ItemStack slotItem = inv.getItem(i);
                        if (slotItem == null || slotItem.getType() == Material.AIR) {
                            hasInventorySpace = true;
                            break;
                        }
                    }
                    
                    // Se tiver espaço no inventário, permitir pickup
                    if (hasInventorySpace) {
                        // O sistema periódico vai mover automaticamente se cair na hotbar
                        return;
                    }
                    
                    // Se não tiver espaço no inventário, tentar trocar item da hotbar com inventário
                    boolean canSwap = false;
                    for (int i = 0; i < 9; i++) {
                        ItemStack hotbarItem = inv.getItem(i);
                        if (hotbarItem != null && hotbarItem.getType() != Material.AIR) {
                            // Verificar se tem algum item NÃO bloqueado no inventário que possa ir pra hotbar
                            for (int j = 9; j < 36; j++) {
                                ItemStack invItem = inv.getItem(j);
                                if (invItem != null && invItem.getType() != Material.AIR) {
                                    String invItemType = invItem.getType().name();
                                    if (!region.isItemTotalBlocked(invItemType)) {
                                        // Pode trocar - tem item do inventário que pode ir pra hotbar
                                        canSwap = true;
                                        // Fazer a troca ANTES do pickup
                                        inv.setItem(i, invItem);
                                        inv.setItem(j, hotbarItem);
                                        return; // Permitir pickup agora que há espaço
                                    }
                                }
                            }
                        }
                    }
                    
                    // Se não conseguiu fazer troca, bloquear pickup
                    if (!canSwap && !hasInventorySpace) {
                        event.setCancelled(true);
                        sendActionBar(player, "§4§l⛔ §cInventário cheio! Não é possível pegar este item bloqueado. §4§l⛔");
                        return;
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getRemover();
        Location location = event.getEntity().getLocation();
        
        if (player.hasPermission("mgz.region.bypass")) {
            return;
        }
        
        if (!regionManager.isAllowed(location, RegionFlag.BREAK)) {
            event.setCancelled(true);
            sendActionBar(player, "§c§l⚠ §cVocê não pode quebrar isso aqui §c§l⚠");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        Location location = entity.getLocation();
        
        if (player.hasPermission("mgz.region.bypass")) {
            return;
        }
        
        if (entity instanceof ItemFrame) {
            ItemFrame frame = (ItemFrame) entity;
            
            if (frame.getItem() == null || frame.getItem().getType() == Material.AIR) {
                if (!regionManager.isAllowed(location, RegionFlag.BUILD)) {
                    event.setCancelled(true);
                    sendActionBar(player, "§c§l⚠ §cVocê não pode modificar molduras aqui §c§l⚠");
                    return;
                }
            } else {
                if (!regionManager.isAllowed(location, RegionFlag.INTERACT)) {
                    event.setCancelled(true);
                    sendActionBar(player, "§c§l⚠ §cVocê não pode modificar molduras aqui §c§l⚠");
                    return;
                }
            }
        }
        
        // Interação com ArmorStand não é tratada aqui pois a entidade será banida (não deve existir no mundo)
    }

    // (Removido) Manipulação e interação específica com ArmorStand foram desabilitadas pois ArmorStand será banida
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageHanging(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Hanging) {
            Location location = event.getEntity().getLocation();
            Region region = regionManager.getHighestPriorityRegion(location);
            boolean protect = (region == null) || region.getFlag(RegionFlag.INVINCIBLE) || !region.getFlag(RegionFlag.BREAK);
            if (protect) {
                event.setCancelled(true);
                if (event.getDamager() instanceof Player) {
                    Player player = (Player) event.getDamager();
                    if (!player.hasPermission("mgz.region.bypass")) {
                        sendActionBar(player, "§c§l⚠ §cVocê não pode quebrar isso aqui §c§l⚠");
                    }
                }
                return;
            }
        }
        
        // ArmorStand não tem proteção específica pois está banida
    }
    
    // Proteção adicional contra quebra de Hanging (molduras, pinturas)
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onHangingBreak(org.bukkit.event.hanging.HangingBreakEvent event) {
        Location location = event.getEntity().getLocation();
        Region region = regionManager.getHighestPriorityRegion(location);
        
        if (region == null) {
            return;
        }
        
        // Se INVINCIBLE ou BREAK false, cancelar QUALQUER quebra
        if (region.getFlag(RegionFlag.INVINCIBLE) || !region.getFlag(RegionFlag.BREAK)) {
            event.setCancelled(true);
        }
    }
    
    // Proteção adicional contra morte de entidades (apenas Hanging)
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onEntityDeath(org.bukkit.event.entity.EntityDeathEvent event) {
        Entity entity = event.getEntity();
        
        if (entity instanceof Hanging) {
            Location location = entity.getLocation();
            Region region = regionManager.getHighestPriorityRegion(location);
            
            if (region == null) {
                return;
            }
            
            // Se INVINCIBLE ou BREAK false, cancelar drops (entidade não deveria morrer)
            if (region.getFlag(RegionFlag.INVINCIBLE) || !region.getFlag(RegionFlag.BREAK)) {
                event.getDrops().clear();
                event.setDroppedExp(0);
            }
        }
    }
    
    // PROTEÇÃO MÁXIMA: Evento com prioridade LOWEST (executa PRIMEIRO)
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onEntityDamageLowest(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Hanging)) {
            return;
        }

        Location location = entity.getLocation();
        Region region = regionManager.getHighestPriorityRegion(location);

    // Por padrão (sem região) também proteger Hanging
    boolean protect = (region == null) || region.getFlag(RegionFlag.INVINCIBLE) || !region.getFlag(RegionFlag.BREAK);

        if (protect) {
            event.setCancelled(true);
            event.setDamage(0);
            entity.setInvulnerable(true);
        } else {
            entity.setInvulnerable(false);
        }
    }
    
    // BLOQUEIO TOTAL: Impede o jogador de até mesmo SEGURAR o item
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        
        if (player.hasPermission("mgz.region.bypass")) {
            return;
        }
        
        // Pega o item que o jogador vai segurar
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        
        if (newItem == null || newItem.getType() == Material.AIR) {
            return;
        }
        
        Location location = player.getLocation();
        Region region = regionManager.getHighestPriorityRegion(location);
        
        if (region != null) {
            String itemType = newItem.getType().name();
            
            // Verificar se o item tem bloqueio TOTAL
            if (region.isItemTotalBlocked(itemType)) {
                event.setCancelled(true);
                
                // Trocar para um slot vazio
                org.bukkit.inventory.PlayerInventory inv = player.getInventory();
                for (int i = 0; i < 9; i++) {
                    ItemStack slotItem = inv.getItem(i);
                    if (slotItem == null || slotItem.getType() == Material.AIR) {
                        // Encontrou slot vazio na hotbar
                        player.getInventory().setHeldItemSlot(i);
                        sendActionBar(player, "§4§l⛔ §cVocê não pode segurar este item nesta região! §4§l⛔");
                        return;
                    }
                }
                
                // Se não houver slot vazio na hotbar, manter no slot atual
                sendActionBar(player, "§4§l⛔ §cVocê não pode segurar este item nesta região! §4§l⛔");
            }
        }
    }
    
    // BLOQUEIO TOTAL: Impede jogador de trocar item para offhand (tecla F)
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        
        if (player.hasPermission("mgz.region.bypass")) {
            return;
        }
        
        Location location = player.getLocation();
        Region region = regionManager.getHighestPriorityRegion(location);
        
        if (region != null) {
            // Verificar item que vai para a offhand
            ItemStack offHandItem = event.getOffHandItem();
            if (offHandItem != null && offHandItem.getType() != Material.AIR) {
                String itemType = offHandItem.getType().name();
                if (region.isItemTotalBlocked(itemType)) {
                    event.setCancelled(true);
                    sendActionBar(player, "§4§l⛔ §cVocê não pode segurar este item nesta região! §4§l⛔");
                    return;
                }
            }
            
            // Verificar item que vai para a mainhand
            ItemStack mainHandItem = event.getMainHandItem();
            if (mainHandItem != null && mainHandItem.getType() != Material.AIR) {
                String itemType = mainHandItem.getType().name();
                if (region.isItemTotalBlocked(itemType)) {
                    event.setCancelled(true);
                    sendActionBar(player, "§4§l⛔ §cVocê não pode segurar este item nesta região! §4§l⛔");
                    return;
                }
            }
        }
    }
    
    // BLOQUEIO TOTAL: Impede jogador de colocar item bloqueado na hotbar (slots 0-8) e offhand (slot 40)
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        
        if (player.hasPermission("mgz.region.bypass")) {
            return;
        }
        
        Location location = player.getLocation();
        Region region = regionManager.getHighestPriorityRegion(location);
        
        if (region != null) {
            int slot = event.getSlot();
            
            // Verificar se está tentando colocar item na hotbar (slots 0-8) ou offhand (slot 40)
            if ((slot >= 0 && slot <= 8) || slot == 40) {
                // Verificar item no cursor (arrastando para o slot)
                ItemStack cursorItem = event.getCursor();
                if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                    String itemType = cursorItem.getType().name();
                    if (region.isItemTotalBlocked(itemType)) {
                        event.setCancelled(true);
                        sendActionBar(player, "§4§l⛔ §cVocê não pode colocar este item na hotbar! §4§l⛔");
                        return;
                    }
                }
            }
            
            // Verificar se está usando shift-click para mover item bloqueado
            if (event.isShiftClick()) {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                    String itemType = clickedItem.getType().name();
                    if (region.isItemTotalBlocked(itemType)) {
                        // Shift-click pode mover para hotbar ou offhand
                        event.setCancelled(true);
                        sendActionBar(player, "§4§l⛔ §cVocê não pode mover este item para a hotbar! §4§l⛔");
                        return;
                    }
                }
            }
            
            // Verificar teclas de atalho (1-9 para trocar com hotbar)
            if (event.getHotbarButton() >= 0 && event.getHotbarButton() <= 8) {
                ItemStack hotbarItem = player.getInventory().getItem(event.getHotbarButton());
                if (hotbarItem != null && hotbarItem.getType() != Material.AIR) {
                    String itemType = hotbarItem.getType().name();
                    if (region.isItemTotalBlocked(itemType)) {
                        event.setCancelled(true);
                        sendActionBar(player, "§4§l⛔ §cVocê não pode mover este item! §4§l⛔");
                        return;
                    }
                }
            }
            
            // Verificar se está usando número para trocar item do inventário com hotbar
            if (event.getClick().name().contains("NUMBER_KEY")) {
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                    String itemType = clickedItem.getType().name();
                    if (region.isItemTotalBlocked(itemType)) {
                        event.setCancelled(true);
                        sendActionBar(player, "§4§l⛔ §cVocê não pode mover este item para a hotbar! §4§l⛔");
                        return;
                    }
                }
            }
        }
    }
}

