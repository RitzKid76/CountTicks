package org.ritzkid76.CountTicks.SyntaxHandling;

import java.util.ArrayList;
import java.util.List;

public class UsageGenerator {
    private SyntaxHandler syntaxHandler;
    private String usage;

    public UsageGenerator(SyntaxHandler handler) { 
        syntaxHandler = handler; 
        usage = generateUsage();
    }

    private List<String> usageBranch(SyntaxEntry path) {
        List<String> output = new ArrayList<>();

        for(String key : path.keys()) {
            output.add(key);
            List<String> subtree = usageBranch(path.get(key));
            for(String sb : subtree) {
                output.add("|   " + sb);
            }
        }

        return output;
    }
    private String generateUsage() {
        StringBuilder output = new StringBuilder();

        List<String> lines = usageBranch(syntaxHandler.getOptionsRoot());
        for(String line : lines) output.append(line).append("\n");

        return output.toString();
    }

    public String usage() { return usage; }
}
