package org.ritzkid76.CountTicks.TabCompletion;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ritzkid76.CountTicks.Debug;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SyntaxHandler implements TabCompleter {
    private final SyntaxEntry options;

    public SyntaxHandler(File dataFolder) {
        options = new SyntaxEntry();

        File yamlFile = new File(dataFolder, "Commands.yaml");
        FileConfiguration yamlConfig = YamlConfiguration.loadConfiguration(yamlFile);

        populateOptions(yamlConfig.getValues(false), options);
    }

    @SuppressWarnings("unchecked")
    private void populateOptions(Map<String, Object> commands, SyntaxEntry entry) {
        SyntaxEntry focus = entry;

        for(Object value : commands.values()) {
            switch(value) {
                case ArrayList<?> list -> {
                    for(Object o : list) {
                        populateOptions((Map<String, Object>) o, focus);
                    }
                }
                case String s -> focus = entry.add(s);
                default -> {}
            }
        }
    }

    private List<String> getTabCompletionList (String[] args) {
        try {
            SyntaxEntry tree = options;

            for(int i = 0; i < args.length - 1; i++) {
                tree = tree.get(args[i]);
            }

            String current = args[args.length - 1];
            List<String> candidates = tree.keys();
            List<String> output = new ArrayList<>();

            for(String candidate : candidates) {
                if(candidate.toLowerCase().startsWith(current.toLowerCase())) output.add(candidate);
            }

            return output;
        } catch (Exception ignored) {}
        return null;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) { return getTabCompletionList(args); }

    public boolean isValidSyntax(String[] args) {
        List<String> tabCompletion = getTabCompletionList(args);

        if(tabCompletion == null) return false;
        if(tabCompletion.isEmpty()) return false;

        return true;
    }
}
