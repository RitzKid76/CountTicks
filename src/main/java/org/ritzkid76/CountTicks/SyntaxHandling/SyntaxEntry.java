package org.ritzkid76.CountTicks.SyntaxHandling;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SyntaxEntry {
	private final Map<String, SyntaxEntry> entries = new HashMap<>();
	private boolean required = false;

	public boolean isRequired() { return required; }

	public void setRequired(boolean r) { required = r; }

    public boolean isEmpty() { return entries.isEmpty(); }

	public Set<String> keys() { return entries.keySet(); }
	public SyntaxEntry get(String string) {
		if(string.isEmpty()) return this;
		return entries.get(string);
	}

	public SyntaxEntry add(String k) { return put(k, new SyntaxEntry()); }
	public SyntaxEntry put(String k, SyntaxEntry v) {
		entries.put(k, v);
		return v;
	}

	public String toSyntaxString() {
        StringBuilder syntax = new StringBuilder();

        for(Map.Entry<String, SyntaxEntry> option : entries.entrySet()) {
            String arg = option.getKey();
            SyntaxEntry subEntry = option.getValue();

            if(subEntry.isRequired())
                syntax.append("<").append(arg);
            else
                syntax.append("[").append(arg);

            if(!subEntry.entries.isEmpty())
                syntax.append(" ").append(subEntry.toSyntaxString());

            if(subEntry.isRequired())
                syntax.append(">");
            else
                syntax.append("]");

            syntax.append(" ");
        }

        return syntax.toString().trim();
    }

    public String toSyntaxList() {
        StringBuilder output = new StringBuilder();

        for(Map.Entry<String, SyntaxEntry> option : entries.entrySet()) {
            String arg = option.getKey();
            SyntaxEntry subEntry = option.getValue();

            output.append(arg);
            if(!subEntry.isEmpty()) 
                output.append(" ").append(subEntry.toSyntaxString());
            output.append("\n");
        }

        return output.toString();
    }
}
