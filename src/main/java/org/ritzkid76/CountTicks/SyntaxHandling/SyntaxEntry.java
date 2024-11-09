package org.ritzkid76.CountTicks.SyntaxHandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SyntaxEntry {
	private final Map<String, SyntaxEntry> entries = new HashMap<>();
	private boolean requiresParameter = false;

	public boolean requiresParameter() {
		return requiresParameter;
	}

	public void setRequiresParameter(boolean r) {
		requiresParameter = r;
	}

	public boolean isEmpty() {
		return entries.isEmpty();
	}

	public Set<String> keys() {
		return entries.keySet();
	}

	public int size() {
		return entries.size();
	}

	public Set<Entry<String, SyntaxEntry>> entrySet() {
		return entries.entrySet();
	}

	public SyntaxEntry get(String string) {
		if(string.isEmpty())
			return this;
		return entries.get(string);
	}

	public SyntaxEntry add(String k) {
		return put(k, new SyntaxEntry());
	}
	public SyntaxEntry put(String k, SyntaxEntry v) {
		entries.put(k, v);
		return v;
	}

	//TODO fix with new parameter requirement shift
	public String toSyntaxString() {
		if(entries.isEmpty())
			return "";

		StringBuilder syntax = new StringBuilder();

		if(requiresParameter)
			syntax.append("<");
		else
			syntax.append("[");

		int remaining = entries.size();
		for(Map.Entry<String, SyntaxEntry> option : entries.entrySet()) {
			remaining--;

			String arg = option.getKey();
			SyntaxEntry subEntry = option.getValue();

			syntax.append(arg);
			if(!subEntry.entries.isEmpty())
				syntax.append(" ").append(subEntry.toSyntaxString());
			if(remaining > 0)
				syntax.append(" | ");
		}

		if(requiresParameter)
			syntax.append(">");
		else
			syntax.append("]");

		return syntax.toString().trim();
	}
}
