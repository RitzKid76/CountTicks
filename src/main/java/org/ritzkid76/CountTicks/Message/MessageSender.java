package org.ritzkid76.CountTicks.Message;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ritzkid76.CountTicks.Exceptions.InvalidMessageParameterLengthException;
import org.ritzkid76.CountTicks.SyntaxHandling.SyntaxEntry;

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
		return messages.getOrDefault(message, "&cunformatted: " + message);
	}

	private static String getReplacementString(Message message, String... replacements) {
		return replace(message.get(), message.parameterCount(), replacements);
	}
	private static String getCleanReplacementString(Message message, String... replacements) {
		return replace(message.getClean(), message.parameterCount(), replacements);
	}
	private static String replace(String message, int parameters, String... replacements) {
		if(replacements.length != parameters)
			throw new InvalidMessageParameterLengthException();

		for(String replacement : replacements) {
			message = message.replaceFirst("\\$", replacement);
		}

		return message;
	}

	public static void sendConsoleMessage(Message message) {
		sendConsoleMessage(message.get());
	}
	public static void sendConsoleMessage(String message) {
		sendMessage(Bukkit.getConsoleSender(), message);
	}

	public static void sendCleanMessage(CommandSender sender, Message message) {
		if(message.parameterCount() > 0)
			throw new InvalidMessageParameterLengthException();
		sendMessage(sender, message.getClean());
	}
	public static void sendCleanMessage(CommandSender sender, Message message, String... replacements) {
		sendMessage(sender, getCleanReplacementString(message, replacements));
	}

	public static void sendMessage(CommandSender sender, Message message) {
		if(message.parameterCount() > 0)
			throw new InvalidMessageParameterLengthException();
		sendMessage(sender, message.get());
	}
	public static void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(colorize(message));
	}
	public static void sendMessage(CommandSender sender, Message message, String... replacements) {
		sendMessage(sender, getReplacementString(message, replacements));
	}

	public static void sendSubtitle(CommandSender sender, Message message) {
		sendSubtitle(sender, message.getClean());
	}
	public static void sendSubtitle(CommandSender sender, String message) {
		Player player = (Player) sender;
		player.resetTitle();
		player.sendTitle("", colorize(message), 0, 40, 20);
	}
	public static void sendSubtitle(CommandSender sender, Message message, String... replacements) {
		sendSubtitle(sender, getCleanReplacementString(message, replacements));
	}

	public static void sendHelpMessage(CommandSender sender, SyntaxEntry options, String label) {
		sendMessage(sender, Message.HELP);

		int remaining = options.size();
		for(Map.Entry<String, SyntaxEntry> option : options.entrySet()) {
			remaining--;

			String arg = option.getKey();
			SyntaxEntry subEntry = option.getValue();

			sendCleanMessage(sender, Message.HELP_LISTING, label, arg + " " + subEntry.toSyntaxString());
		}
	}
}