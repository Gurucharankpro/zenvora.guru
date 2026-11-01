/*
 * Decompiled with CFR 0.152.
 */
package dev.shadowsovereign.soulforge;

public enum SoulTrait {
    NONE("Original soul", "Existing companions keep their original traits."),
    SWIFT("Swift", "+15% movement speed."),
    STALWART("Stalwart", "+4 armor points."),
    VITAL("Vital", "Regeneration restores 2 HP instead of 1."),
    VAMPIRIC("Vampiric", "Heals 2 HP after its own confirmed kill."),
    DEVOTED("Devoted", "Earns 2 bond points per personal kill instead of 1.");

    public final String label;
    public final String description;

    private SoulTrait(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public static SoulTrait read(String name) {
        try {
            return SoulTrait.valueOf(name);
        }
        catch (IllegalArgumentException ignored) {
            return NONE;
        }
    }
}

