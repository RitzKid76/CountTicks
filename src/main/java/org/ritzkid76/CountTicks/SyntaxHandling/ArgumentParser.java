package org.ritzkid76.CountTicks.SyntaxHandling;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ritzkid76.CountTicks.Commands.Command;
import org.ritzkid76.CountTicks.Commands.CountCommand;
import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraphPath;

public class ArgumentParser {
	private final SyntaxHandler syntaxHandler;
	private final Map<String, Constructor<?>> stringToCommandClass;

	public ArgumentParser(File dataFolder) {
		syntaxHandler = new SyntaxHandler(dataFolder);
		stringToCommandClass = populateCommandClassCache();
	}

	private Map<String, Constructor<?>> populateCommandClassCache() {
		Map<String, Constructor<?>> output = new HashMap<>();

		String packageName = Command.class.getPackageName();

		for(String command : syntaxHandler.keys()) {
			String className = packageName + "." + snakeCaseToPascalCase(command) + Command.class.getSimpleName();

			try {
				Class<?> clazz = Class.forName(className);
				Constructor<?> constructor = clazz.getConstructor(
					String[].class,
					PlayerData.class,
					String.class,
					SyntaxHandler.class
				);

				output.put(command, constructor);
			} catch (
				ClassNotFoundException |
				NoSuchMethodException |
				SecurityException e
			) {
				throw new RuntimeException(e);
			}
		}

		return output;
	}

	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		return syntaxHandler.onTabComplete(sender, command, label, args);
	}

	private String snakeCaseToPascalCase(String snakeCase) {
		return Arrays.stream(snakeCase.split("_")).map(token -> {
			char startChar = token.charAt(0);
			startChar -= ('a' - 'A');

			return startChar + token.substring(1);
		}).collect(Collectors.joining());
	}

	private void getUsage(String[] args, PlayerData playerData, String label) {
		SyntaxEntry current = syntaxHandler.getOptionsRoot();
		StringBuilder output = new StringBuilder();

		for(String arg : args) {
			SyntaxEntry next = current.get(arg);

			if(next == null)
				break;

			output.append(arg).append(" ");
			current = next;
		}

		output.append(current.toSyntaxString());
		MessageSender.sendMessage(playerData.getPlayer(), Message.INVALID_SYNTAX, label, output.toString().trim());
	}

	public void run(String[] args, PlayerData playerData, String label) {
		if(!syntaxHandler.isValidSyntax(args)) {
			getUsage(args, playerData, label);
			return;
		}

		if(args.length == 0) {
			playerData.runCommand(new CountCommand(args, playerData, label, syntaxHandler));
			return;
		}

		String commandName = args[0];
		Constructor<?> commandConstructor = stringToCommandClass.get(commandName);
		args = Arrays.copyOfRange(args, 1, args.length); // remove first argument

		try {
			playerData.runCommand((Command) commandConstructor.newInstance(args, playerData, label, syntaxHandler));
		} catch (
			InstantiationException |
			IllegalAccessException |
			IllegalArgumentException |
			InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void sendInspectorMessageSubtitle(Player player, RedstoneTracerGraphPath path) {
		switch(path.result()) {
			case PATH_FOUND -> MessageSender.sendSubtitle(player, Message.DELAY_SHORT, String.valueOf(path.delay() / 2));
			case UNSCANNED_LOCATION -> MessageSender.sendSubtitle(player, Message.UNSCANNED_LOCATION_SHORT);
			case OUT_OF_BOUNDS -> MessageSender.sendSubtitle(player, Message.OUT_OF_BOUNDS_SHORT);
		}
	}

	public static void sendInspectorMessage(Player player, RedstoneTracerGraphPath path) {
		switch(path.result()) {
			case PATH_FOUND -> MessageSender.sendMessage(player, Message.DELAY, String.valueOf(path.delay() / 2));
			case UNSCANNED_LOCATION -> MessageSender.sendMessage(player, Message.UNSCANNED_LOCATION);
			case OUT_OF_BOUNDS -> MessageSender.sendMessage(player, Message.END_OUT_OF_BOUNDS);
		}
	}
}
