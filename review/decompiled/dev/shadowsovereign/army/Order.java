/*
 * Decompiled with CFR 0.152.
 */
package dev.shadowsovereign.army;

public enum Order {
    FOLLOW("Follow"),
    STAY("Stay"),
    GUARD("Guard"),
    PATROL("Patrol");

    public final String label;

    private Order(String label) {
        this.label = label;
    }

    public static Order parse(String name) {
        try {
            return Order.valueOf(name);
        }
        catch (IllegalArgumentException e) {
            return FOLLOW;
        }
    }
}

