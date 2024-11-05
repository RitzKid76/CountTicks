package org.ritzkid76.CountTicks.SyntaxHandling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
