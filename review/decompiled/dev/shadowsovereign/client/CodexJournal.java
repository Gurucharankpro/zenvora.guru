/*
 * Decompiled with CFR 0.152.
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.client.ClientPreferences;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

final class CodexJournal {
    private final int[] positions = new int[12];
    private final SortedSet<Integer> marks = new TreeSet<Integer>();
    private final Map<String, String> changes = new HashMap<String, String>();
    int last;
    boolean touch;
    boolean contrast;
    boolean dim;

    CodexJournal() {
        Properties p = ClientPreferences.read();
        this.last = CodexJournal.number(p.getProperty("codex.last"), 1, 11, 1);
        for (int i = 0; i < 12; ++i) {
            this.positions[i] = CodexJournal.number(p.getProperty("codex.scroll." + i), 0, 1000000, 0);
        }
        for (String s : p.getProperty("codex.marks", "").split(",")) {
            int i = CodexJournal.number(s, 0, 11, -1);
            if (i < 0) continue;
            this.marks.add(i);
        }
        this.touch = Boolean.parseBoolean(p.getProperty("codex.touch", "false"));
        this.contrast = Boolean.parseBoolean(p.getProperty("codex.contrast", "false"));
        this.dim = Boolean.parseBoolean(p.getProperty("codex.dim", "false"));
    }

    private static int number(String s, int lo, int hi, int fallback) {
        try {
            int n = Integer.parseInt(s);
            return n >= lo && n <= hi ? n : fallback;
        }
        catch (RuntimeException ex) {
            return fallback;
        }
    }

    int position(int page) {
        return this.positions[page];
    }

    void remember(int page, int scroll) {
        this.positions[page] = Math.max(0, Math.min(1000000, scroll));
        this.changes.put("codex.scroll." + page, Integer.toString(this.positions[page]));
        if (page > 0) {
            this.last = page;
            this.changes.put("codex.last", Integer.toString(page));
        }
    }

    boolean marked(int page) {
        return this.marks.contains(page);
    }

    List<Integer> marks() {
        return List.copyOf(this.marks);
    }

    void bookmark(int page) {
        if (!this.marks.remove(page)) {
            this.marks.add(page);
        }
        this.changes.put("codex.marks", this.marks.stream().map(Object::toString).collect(Collectors.joining(",")));
        this.flush();
    }

    void toggle(int option) {
        if (option == 0) {
            this.touch = !this.touch;
            this.changes.put("codex.touch", String.valueOf(this.touch));
        }
        if (option == 1) {
            this.contrast = !this.contrast;
            this.changes.put("codex.contrast", String.valueOf(this.contrast));
        }
        if (option == 2) {
            this.dim = !this.dim;
            this.changes.put("codex.dim", String.valueOf(this.dim));
        }
        this.flush();
    }

    void flush() {
        if (!this.changes.isEmpty()) {
            ClientPreferences.setAll(this.changes);
            this.changes.clear();
        }
    }
}

