package mglucas0123.region;

public enum RegionFlag {
    PVP("Permite PvP na região"),
    MOB_SPAWN("Permite spawn de mobs"),
    BUILD("Permite construir"),
    BREAK("Permite quebrar blocos"),
    INTERACT("Permite interagir com blocos/entidades"),
    INVINCIBLE("Jogadores são invencíveis"),
    FIRE_SPREAD("Permite propagação de fogo"),
    EXPLOSION("Permite explosões"),
    ITEM_DROP("Permite dropar itens"),
    ITEM_PICKUP("Permite pegar itens");
    
    private final String description;
    
    RegionFlag(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static RegionFlag fromString(String name) {
        for (RegionFlag flag : values()) {
            if (flag.name().equalsIgnoreCase(name)) {
                return flag;
            }
        }
        return null;
    }
}
