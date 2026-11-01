/*
 * Decompiled with CFR 0.152.
 */
package dev.shadowsovereign.army;

public final class BattleRules {
    private BattleRules() {
    }

    public static int wheelSector(double dx, double dy) {
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance < 34.0 || distance > 155.0) {
            return -1;
        }
        return Math.floorMod((int)Math.floor((Math.atan2(dy, dx) + 1.5707963267948966 + 0.39269908169872414) / 0.7853981633974483), 8);
    }

    public static <E extends Enum<E>> E read(Class<E> type, String value, E fallback) {
        try {
            return Enum.valueOf(type, value);
        }
        catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    public static enum Skill {
        STEP("Shadow Step", 2, 600),
        TAUNT("Taunt", 4, 400),
        BARRIER("Soul Barrier", 6, 1200),
        MEND("Soul Mend", 8, 1200);

        public final String label;
        public final int level;
        public final int cooldown;

        private Skill(String label, int level, int cooldown) {
            this.label = label;
            this.level = level;
            this.cooldown = cooldown;
        }
    }

    public static enum Priority {
        DEFAULT("Default"),
        NEAREST("Nearest"),
        WEAKEST("Weakest HP %"),
        OWNER_ATTACKER("Owner's attacker");

        public final String label;

        private Priority(String label) {
            this.label = label;
        }
    }

    public static enum Formation {
        LOOSE("Loose"),
        LINE("Line"),
        WEDGE("Wedge"),
        RING("Ring");

        public final String label;

        private Formation(String label) {
            this.label = label;
        }
    }

    public static enum Squad {
        ALL("All"),
        UNASSIGNED("Unassigned"),
        ALPHA("Alpha"),
        BETA("Beta"),
        GAMMA("Gamma"),
        DELTA("Delta");

        public final String label;

        private Squad(String label) {
            this.label = label;
        }
    }
}

