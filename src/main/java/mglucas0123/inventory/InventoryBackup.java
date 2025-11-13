package mglucas0123.inventory;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class InventoryBackup implements Serializable {

    private static final long serialVersionUID = 3L; 
    
    
    private final String playerName;
    private final String playerUUID;
    private final long timestamp;
    
    
    private final Map<Integer, Map<String, Object>> serializedContents;
    private final List<Map<String, Object>> serializedArmorContents;
    private final Map<String, Object> serializedOffHand;
    private final int heldItemSlot;
    
    
    private final Map<Integer, Map<String, Object>> serializedEnderChestContents;
    
    
    private final int level;
    private final float exp;
    private final int totalExperience;
    
    
    private final double health;
    private final double maxHealth;
    private final int foodLevel;
    private final float saturation;
    private final float exhaustion;
    
    
    private final List<Map<String, Object>> serializedPotionEffects;
    
    
    private final String worldName;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    
    
    private final GameMode gameMode;
    
    
    private final boolean allowFlight;
    private final boolean isFlying;
    
    public InventoryBackup(Player player) {
        
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId().toString();
        this.timestamp = System.currentTimeMillis();
        
        PlayerInventory inventory = player.getInventory();
        
        
        this.serializedContents = serializeItemMap(inventory.getContents());

        
        this.serializedArmorContents = serializeItemList(inventory.getArmorContents());

        
        this.serializedOffHand = serializeItem(inventory.getItemInOffHand());
        
        
        this.heldItemSlot = inventory.getHeldItemSlot();
        
        
        this.serializedEnderChestContents = serializeItemMap(player.getEnderChest().getContents());
        
        
        this.level = player.getLevel();
        this.exp = player.getExp();
        this.totalExperience = player.getTotalExperience();
        
        
        this.health = player.getHealth();
    AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
    this.maxHealth = attribute != null ? attribute.getValue() : player.getHealth();
        this.foodLevel = player.getFoodLevel();
        this.saturation = player.getSaturation();
        this.exhaustion = player.getExhaustion();
        
        
    this.serializedPotionEffects = serializePotionEffects(player.getActivePotionEffects());
        
        
        Location loc = player.getLocation();
        this.worldName = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        
        
        this.gameMode = player.getGameMode();
        this.allowFlight = player.getAllowFlight();
        this.isFlying = player.isFlying();
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public String getPlayerUUID() {
        return playerUUID;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public Map<Integer, ItemStack> getContents() {
        Map<Integer, ItemStack> deserialized = new HashMap<>();
        for (Map.Entry<Integer, Map<String, Object>> entry : serializedContents.entrySet()) {
            ItemStack item = deserializeItem(entry.getValue());
            if (item != null) {
                deserialized.put(entry.getKey(), item);
            }
        }
        return deserialized;
    }
    
    public ItemStack[] getArmorContents() {
        return deserializeItemList(serializedArmorContents);
    }
    
    public ItemStack getOffHand() {
    return serializedOffHand != null ? deserializeItem(serializedOffHand) : null;
    }
    
    public int getHeldItemSlot() {
        return heldItemSlot;
    }
    
    
    public boolean isEmpty() {
        if (!serializedContents.isEmpty()) {
            return false;
        }

        for (Map<String, Object> armor : serializedArmorContents) {
            if (armor != null) {
                return false;
            }
        }

        return serializedOffHand == null;
    }
    
    
    public int getItemCount() {
        int count = serializedContents.size();

        for (Map<String, Object> armor : serializedArmorContents) {
            if (armor != null) {
                count++;
            }
        }

        if (serializedOffHand != null) {
            count++;
        }

        return count;
    }
    
    
    public void restore(Player player) {
        PlayerInventory inventory = player.getInventory();
        
        
        inventory.clear();
        
        
        for (Map.Entry<Integer, Map<String, Object>> entry : serializedContents.entrySet()) {
            ItemStack item = deserializeItem(entry.getValue());
            if (item != null) {
                inventory.setItem(entry.getKey(), item);
            }
        }

        
        inventory.setArmorContents(deserializeItemList(serializedArmorContents));

        
        if (serializedOffHand != null) {
            ItemStack offHandItem = deserializeItem(serializedOffHand);
            if (offHandItem != null) {
                inventory.setItemInOffHand(offHandItem);
            }
        }
        
        
        inventory.setHeldItemSlot(heldItemSlot);
        
        
        player.getEnderChest().clear();
        for (Map.Entry<Integer, Map<String, Object>> entry : serializedEnderChestContents.entrySet()) {
            ItemStack item = deserializeItem(entry.getValue());
            if (item != null) {
                player.getEnderChest().setItem(entry.getKey(), item);
            }
        }
        
        
        player.setLevel(level);
        player.setExp(exp);
        player.setTotalExperience(totalExperience);
        
        
    AttributeInstance playerAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
    double max = playerAttribute != null ? playerAttribute.getValue() : maxHealth;
    player.setHealth(Math.min(health, max));
        player.setFoodLevel(foodLevel);
        player.setSaturation(saturation);
        player.setExhaustion(exhaustion);
        
        
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        
        for (Map<String, Object> effectData : serializedPotionEffects) {
            PotionEffect effect = deserializePotionEffect(effectData);
            if (effect != null) {
                player.addPotionEffect(effect);
            }
        }
        
        
        player.setGameMode(gameMode);
        player.setAllowFlight(allowFlight);
        player.setFlying(isFlying);
    }
    
    
    @Deprecated
    public void restore(PlayerInventory inventory) {
        
        inventory.clear();
        
        
        for (Map.Entry<Integer, Map<String, Object>> entry : serializedContents.entrySet()) {
            ItemStack item = deserializeItem(entry.getValue());
            if (item != null) {
                inventory.setItem(entry.getKey(), item);
            }
        }

        
        inventory.setArmorContents(deserializeItemList(serializedArmorContents));

        
        if (serializedOffHand != null) {
            ItemStack offHandItem = deserializeItem(serializedOffHand);
            if (offHandItem != null) {
                inventory.setItemInOffHand(offHandItem);
            }
        }
        
        
        inventory.setHeldItemSlot(heldItemSlot);
    }
    
    
    public int getLevel() {
        return level;
    }
    
    public double getHealth() {
        return health;
    }
    
    public int getFoodLevel() {
        return foodLevel;
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    public String getLocation() {
        return String.format("%s (%.1f, %.1f, %.1f)", worldName, x, y, z);
    }
    
    public GameMode getGameMode() {
        return gameMode;
    }
    
    public int getEnderChestItemCount() {
        return serializedEnderChestContents.size();
    }
    
    public int getPotionEffectCount() {
        return serializedPotionEffects.size();
    }

    public boolean hasSameState(InventoryBackup other) {
        if (other == null) {
            return false;
        }
        if (!serializedContents.equals(other.serializedContents)) {
            return false;
        }
        if (!serializedArmorContents.equals(other.serializedArmorContents)) {
            return false;
        }
        if (!Objects.equals(serializedOffHand, other.serializedOffHand)) {
            return false;
        }
        if (!serializedEnderChestContents.equals(other.serializedEnderChestContents)) {
            return false;
        }
        if (heldItemSlot != other.heldItemSlot) {
            return false;
        }
        if (level != other.level || totalExperience != other.totalExperience) {
            return false;
        }
        if (Float.compare(exp, other.exp) != 0) {
            return false;
        }
        if (Double.compare(health, other.health) != 0) {
            return false;
        }
        if (Double.compare(maxHealth, other.maxHealth) != 0) {
            return false;
        }
        if (foodLevel != other.foodLevel) {
            return false;
        }
        if (Float.compare(saturation, other.saturation) != 0 || Float.compare(exhaustion, other.exhaustion) != 0) {
            return false;
        }
        if (!serializedPotionEffects.equals(other.serializedPotionEffects)) {
            return false;
        }
        if (gameMode != other.gameMode) {
            return false;
        }
        if (allowFlight != other.allowFlight || isFlying != other.isFlying) {
            return false;
        }
        if (!Objects.equals(worldName, other.worldName)) {
            return false;
        }
        if (Double.compare(x, other.x) != 0 || Double.compare(y, other.y) != 0 || Double.compare(z, other.z) != 0) {
            return false;
        }
        if (Float.compare(yaw, other.yaw) != 0 || Float.compare(pitch, other.pitch) != 0) {
            return false;
        }
        return true;
    }

    private static Map<Integer, Map<String, Object>> serializeItemMap(ItemStack[] items) {
        Map<Integer, Map<String, Object>> serialized = new HashMap<>();
        for (int i = 0; i < items.length; i++) {
            Map<String, Object> data = serializeItem(items[i]);
            if (data != null) {
                serialized.put(i, data);
            }
        }
        return serialized;
    }

    private static List<Map<String, Object>> serializeItemList(ItemStack[] items) {
        List<Map<String, Object>> serialized = new ArrayList<>(items.length);
        for (ItemStack item : items) {
            serialized.add(serializeItem(item));
        }
        return serialized;
    }

    private static Map<String, Object> serializeItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }
        return new HashMap<>(item.serialize());
    }

    private static ItemStack deserializeItem(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        return ItemStack.deserialize(data);
    }

    private static ItemStack[] deserializeItemList(List<Map<String, Object>> serialized) {
        ItemStack[] items = new ItemStack[serialized.size()];
        for (int i = 0; i < serialized.size(); i++) {
            items[i] = deserializeItem(serialized.get(i));
        }
        return items;
    }

    private static List<Map<String, Object>> serializePotionEffects(Collection<PotionEffect> effects) {
        List<Map<String, Object>> serialized = new ArrayList<>();
        for (PotionEffect effect : effects) {
            serialized.add(new HashMap<>(effect.serialize()));
        }
        return serialized;
    }

    private static PotionEffect deserializePotionEffect(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return null;
        }

        Object typeObject = data.get("type");
        if (typeObject == null) {
            typeObject = data.get("effect");
        }

        PotionEffectType type = null;
        if (typeObject instanceof PotionEffectType) {
            type = (PotionEffectType) typeObject;
        } else if (typeObject instanceof String) {
            type = PotionEffectType.getByName((String) typeObject);
        }

        if (type == null) {
            return null;
        }

        int duration = getNumber(data.get("duration")).intValue();
        int amplifier = getNumber(data.get("amplifier")).intValue();
        boolean ambient = getBoolean(data.get("ambient"), false);
        boolean particles = getBoolean(data.get("particles"), true);
        boolean icon = getBoolean(data.getOrDefault("icon", data.getOrDefault("has-icon", particles)), particles);

        return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
    }

    private static Number getNumber(Object value) {
        if (value instanceof Number) {
            return (Number) value;
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    private static boolean getBoolean(Object value, boolean defaultValue) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return defaultValue;
    }
}
