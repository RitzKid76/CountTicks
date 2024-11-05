package org.ritzkid76.CountTicks.Message;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessageSender {
    private static final Map<String, String> messages = new HashMap<>();

	public static void populateOptions(File dataFolder) {
		File yamlFile = new File(dataFolder, "Messages.yaml");
		FileConfiguration yamlConfig = YamlConfiguration.loadConfiguration(yamlFile);

        Map<String, Object> msgs = yamlConfig.getValues(false);

		for(Map.Entry<String, Object> entry : msgs.entrySet()) {
            String langKey = entry.getKey();
            String message = (String) entry.getValue();

            messages.put(langKey, message);
		}
	}
    
    private static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getMessage(String message) {
        return messages.getOrDefault(message, message); 
    }

    public static void sendConsoleMessage(Message message) { sendMessage(Bukkit.getConsoleSender(), message); }
    public static void sendMessage(CommandSender sender, Message message) { sendMessage(sender, message.get()); }
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }
    public static void sendMessage(CommandSender sender, Message message, String... replacements) {
        String msg = message.get();
        
        for(String replacement : replacements) {
            msg = msg.replaceFirst("\\$", replacement);
        }

        sendMessage(sender, msg);
    }
}