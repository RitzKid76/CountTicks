package org.ritzkid76.CountTicks.TabCompletion;

import java.util.*;

public class SyntaxEntry {
    private final Map<String, SyntaxEntry> entries;
    private final List<String> keys;

    public SyntaxEntry() {
        entries = new HashMap<>();
        keys = new ArrayList<>();
    }

    public List<String> keys() { return keys; }
    public SyntaxEntry get(String string) {
        if(string.isEmpty()) return this;
        return entries.get(string);
    }

    public SyntaxEntry add(String k) { return put(k, new SyntaxEntry()); }
    public SyntaxEntry put(String k, SyntaxEntry v) {
        keys.add(k);
        entries.put(k, v);
        return v;
    }
}
