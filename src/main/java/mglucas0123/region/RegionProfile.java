package mglucas0123.region;

import java.util.Map;

public enum RegionProfile {
    
    DEFAULT("default", "Padrão - Configuração balanceada e segura", 0) {
        @Override
        public void applyFlags(Region region) {
            region.setFlag(RegionFlag.PVP, false);
            region.setFlag(RegionFlag.BUILD, false);
            region.setFlag(RegionFlag.BREAK, false);
            region.setFlag(RegionFlag.INTERACT, true);
            region.setFlag(RegionFlag.INVINCIBLE, true);
            region.setFlag(RegionFlag.MOB_SPAWN, false);
            region.setFlag(RegionFlag.EXPLOSION, false);
            region.setFlag(RegionFlag.FIRE_SPREAD, true);
            region.setFlag(RegionFlag.ITEM_DROP, true);
            region.setFlag(RegionFlag.ITEM_PICKUP, true);
        }
    },
    
    SAFEZONE("safezone", "Zona Segura - Spawn, lobby (sem PvP, invencível, sem mobs)", 100) {
        @Override
        public void applyFlags(Region region) {
            region.setFlag(RegionFlag.PVP, false);
            region.setFlag(RegionFlag.BUILD, false);
            region.setFlag(RegionFlag.BREAK, false);
            region.setFlag(RegionFlag.INTERACT, false);
            region.setFlag(RegionFlag.INVINCIBLE, true);
            region.setFlag(RegionFlag.MOB_SPAWN, false);
            region.setFlag(RegionFlag.EXPLOSION, false);
            region.setFlag(RegionFlag.FIRE_SPREAD, false);
            region.setFlag(RegionFlag.ITEM_DROP, false);
            region.setFlag(RegionFlag.ITEM_PICKUP, false);
        }
    },
    
    SPAWN("spawn", "Spawn Principal - Seguro mas permite interação básica", 90) {
        @Override
        public void applyFlags(Region region) {
            region.setFlag(RegionFlag.PVP, false);
            region.setFlag(RegionFlag.BUILD, false);
            region.setFlag(RegionFlag.BREAK, false);
            region.setFlag(RegionFlag.INTERACT, true);
            region.setFlag(RegionFlag.INVINCIBLE, false);
            region.setFlag(RegionFlag.MOB_SPAWN, false);
            region.setFlag(RegionFlag.EXPLOSION, false);
            region.setFlag(RegionFlag.FIRE_SPREAD, false);
            region.setFlag(RegionFlag.ITEM_DROP, true);
            region.setFlag(RegionFlag.ITEM_PICKUP, true);
        }
    },
    
    ARENA("arena", "Arena PvP - Permite combate, sem griefing", 50) {
        @Override
        public void applyFlags(Region region) {
            region.setFlag(RegionFlag.PVP, true);
            region.setFlag(RegionFlag.BUILD, false);
            region.setFlag(RegionFlag.BREAK, false);
            region.setFlag(RegionFlag.INTERACT, true);
            region.setFlag(RegionFlag.INVINCIBLE, false);
            region.setFlag(RegionFlag.MOB_SPAWN, false);
            region.setFlag(RegionFlag.EXPLOSION, false);
            region.setFlag(RegionFlag.FIRE_SPREAD, false);
            region.setFlag(RegionFlag.ITEM_DROP, true);
            region.setFlag(RegionFlag.ITEM_PICKUP, true);
        }
    },
    
    SHOP("shop", "Loja/Comércio - Sem PvP, permite interação", 70) {
        @Override
        public void applyFlags(Region region) {
            region.setFlag(RegionFlag.PVP, false);
            region.setFlag(RegionFlag.BUILD, false);
            region.setFlag(RegionFlag.BREAK, false);
            region.setFlag(RegionFlag.INTERACT, true);
            region.setFlag(RegionFlag.INVINCIBLE, false);
            region.setFlag(RegionFlag.MOB_SPAWN, false);
            region.setFlag(RegionFlag.EXPLOSION, false);
            region.setFlag(RegionFlag.FIRE_SPREAD, false);
            region.setFlag(RegionFlag.ITEM_DROP, true);
            region.setFlag(RegionFlag.ITEM_PICKUP, true);
        }
    },
    
    VIP("vip", "Área VIP - Permite construir, sem PvP", 60) {
        @Override
        public void applyFlags(Region region) {
            region.setFlag(RegionFlag.PVP, false);
            region.setFlag(RegionFlag.BUILD, true);
            region.setFlag(RegionFlag.BREAK, true);
            region.setFlag(RegionFlag.INTERACT, true);
            region.setFlag(RegionFlag.INVINCIBLE, false);
            region.setFlag(RegionFlag.MOB_SPAWN, true);
            region.setFlag(RegionFlag.EXPLOSION, false);
            region.setFlag(RegionFlag.FIRE_SPREAD, false);
            region.setFlag(RegionFlag.ITEM_DROP, true);
            region.setFlag(RegionFlag.ITEM_PICKUP, true);
        }
    },
    
    CREATIVE("creative", "Área Criativa - Liberdade total de construção", 40) {
        @Override
        public void applyFlags(Region region) {
            region.setFlag(RegionFlag.PVP, false);
            region.setFlag(RegionFlag.BUILD, true);
            region.setFlag(RegionFlag.BREAK, true);
            region.setFlag(RegionFlag.INTERACT, true);
            region.setFlag(RegionFlag.INVINCIBLE, false);
            region.setFlag(RegionFlag.MOB_SPAWN, false);
            region.setFlag(RegionFlag.EXPLOSION, false);
            region.setFlag(RegionFlag.FIRE_SPREAD, false);
            region.setFlag(RegionFlag.ITEM_DROP, true);
            region.setFlag(RegionFlag.ITEM_PICKUP, true);
        }
    },
    
    PLOT("plot", "Plot/Terreno - Área de construção protegida", 30) {
        @Override
        public void applyFlags(Region region) {
            region.setFlag(RegionFlag.PVP, false);
            region.setFlag(RegionFlag.BUILD, true);
            region.setFlag(RegionFlag.BREAK, true);
            region.setFlag(RegionFlag.INTERACT, true);
            region.setFlag(RegionFlag.INVINCIBLE, false);
            region.setFlag(RegionFlag.MOB_SPAWN, true);
            region.setFlag(RegionFlag.EXPLOSION, false);
            region.setFlag(RegionFlag.FIRE_SPREAD, true);
            region.setFlag(RegionFlag.ITEM_DROP, true);
            region.setFlag(RegionFlag.ITEM_PICKUP, true);
        }
    },
    
    EVENT("event", "Área de Evento - Configurável, protegida", 80) {
        @Override
        public void applyFlags(Region region) {
            region.setFlag(RegionFlag.PVP, false);
            region.setFlag(RegionFlag.BUILD, false);
            region.setFlag(RegionFlag.BREAK, false);
            region.setFlag(RegionFlag.INTERACT, true);
            region.setFlag(RegionFlag.INVINCIBLE, false);
            region.setFlag(RegionFlag.MOB_SPAWN, false);
            region.setFlag(RegionFlag.EXPLOSION, false);
            region.setFlag(RegionFlag.FIRE_SPREAD, false);
            region.setFlag(RegionFlag.ITEM_DROP, true);
            region.setFlag(RegionFlag.ITEM_PICKUP, true);
        }
    },
    
    WARZONE("warzone", "Zona de Guerra - PvP total, explosões permitidas", 20) {
        @Override
        public void applyFlags(Region region) {
            region.setFlag(RegionFlag.PVP, true);
            region.setFlag(RegionFlag.BUILD, true);
            region.setFlag(RegionFlag.BREAK, true);
            region.setFlag(RegionFlag.INTERACT, true);
            region.setFlag(RegionFlag.INVINCIBLE, false);
            region.setFlag(RegionFlag.MOB_SPAWN, true);
            region.setFlag(RegionFlag.EXPLOSION, true);
            region.setFlag(RegionFlag.FIRE_SPREAD, true);
            region.setFlag(RegionFlag.ITEM_DROP, true);
            region.setFlag(RegionFlag.ITEM_PICKUP, true);
        }
    },
    
    MINIGAME("minigame", "Mini-Game - Área customizável para jogos", 50) {
        @Override
        public void applyFlags(Region region) {
            region.setFlag(RegionFlag.PVP, true);
            region.setFlag(RegionFlag.BUILD, false);
            region.setFlag(RegionFlag.BREAK, false);
            region.setFlag(RegionFlag.INTERACT, true);
            region.setFlag(RegionFlag.INVINCIBLE, false);
            region.setFlag(RegionFlag.MOB_SPAWN, false);
            region.setFlag(RegionFlag.EXPLOSION, false);
            region.setFlag(RegionFlag.FIRE_SPREAD, false);
            region.setFlag(RegionFlag.ITEM_DROP, true);
            region.setFlag(RegionFlag.ITEM_PICKUP, true);
        }
    },
    
    FARM("farm", "Área de Farm - Permite tudo exceto PvP", 10) {
        @Override
        public void applyFlags(Region region) {
            region.setFlag(RegionFlag.PVP, false);
            region.setFlag(RegionFlag.BUILD, true);
            region.setFlag(RegionFlag.BREAK, true);
            region.setFlag(RegionFlag.INTERACT, true);
            region.setFlag(RegionFlag.INVINCIBLE, false);
            region.setFlag(RegionFlag.MOB_SPAWN, true);
            region.setFlag(RegionFlag.EXPLOSION, false);
            region.setFlag(RegionFlag.FIRE_SPREAD, false);
            region.setFlag(RegionFlag.ITEM_DROP, true);
            region.setFlag(RegionFlag.ITEM_PICKUP, true);
        }
    },
    
    MUSEUM("museum", "Museu - Apenas visualização, totalmente protegido", 75) {
        @Override
        public void applyFlags(Region region) {
            region.setFlag(RegionFlag.PVP, false);
            region.setFlag(RegionFlag.BUILD, false);
            region.setFlag(RegionFlag.BREAK, false);
            region.setFlag(RegionFlag.INTERACT, false);
            region.setFlag(RegionFlag.INVINCIBLE, true);
            region.setFlag(RegionFlag.MOB_SPAWN, false);
            region.setFlag(RegionFlag.EXPLOSION, false);
            region.setFlag(RegionFlag.FIRE_SPREAD, false);
            region.setFlag(RegionFlag.ITEM_DROP, false);
            region.setFlag(RegionFlag.ITEM_PICKUP, false);
        }
    };
    
    private final String name;
    private final String description;
    private final int defaultPriority;
    
    RegionProfile(String name, String description, int defaultPriority) {
        this.name = name;
        this.description = description;
        this.defaultPriority = defaultPriority;
    }
    
    public abstract void applyFlags(Region region);
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getDefaultPriority() {
        return defaultPriority;
    }
    
    public Map<RegionFlag, Boolean> getFlags() {
        
        Region temp = new Region("temp", "world", null, null);
        applyFlags(temp);
        return temp.getFlags();
    }
    
    public static RegionProfile fromString(String name) {
        for (RegionProfile profile : values()) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }
        return null;
    }
    
    public static String[] getProfileNames() {
        RegionProfile[] profiles = values();
        String[] names = new String[profiles.length];
        for (int i = 0; i < profiles.length; i++) {
            names[i] = profiles[i].getName();
        }
        return names;
    }
}
