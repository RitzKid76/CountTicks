package org.ritzkid76.CountTicks.SyntaxHandling;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.ritzkid76.CountTicks.WorldEditSelection;
import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraphPath;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraphPathResult;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

public class ArgumentParser {
	private static SyntaxHandler syntaxHandler;
	private static Plugin plugin;

	public static void setDataFolder(File dataFolder) {
		syntaxHandler = new SyntaxHandler(dataFolder);
	}
	public static void setPlugin(Plugin p) {
		plugin = p;
	}

	public static List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return syntaxHandler.onTabComplete(sender, command, label, args);
	}

	private static void getUsage(String[] args, PlayerData playerData, String label) {
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

	public static void run(String[] args, PlayerData playerData, String label) {
		if(!syntaxHandler.isValidSyntax(args)) {
			getUsage(args, playerData, label);
			return;
		}

		if(args.length == 0) {
			count(args, playerData, label);
			return;
		}

		String methodName = args[0];
		args = Arrays.copyOfRange(args, 1, args.length);

		try {
			ArgumentParser.class.getDeclaredMethod(
				methodName,
				String[].class,
				PlayerData.class,
				String.class
			).invoke(null, args, playerData, label);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void count(String[] args, PlayerData playerData, String label) {
		WorldEditSelection selection = playerData.getSelection();
		Player player = playerData.getPlayer();

		if(playerData.isScanning()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_SCANNING, label);
			return;
		}
		if(playerData.isInspecting()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_INSPECTING, label);
			return;
		}

		BlockVector3 startPosition = selection.getFirstPosition();
		if(startPosition == null) {
			MessageSender.sendMessage(player, Message.NO_START_SELECTED);
			return;
		}
		BlockVector3 endPosition = selection.getSecondPosition();
		if(endPosition == null) {
			MessageSender.sendMessage(player, Message.NO_END_SELECTED);
			return;
		}

		playerData.count(startPosition, endPosition, plugin, label);
	}

	private static void scan(String[] args, PlayerData playerData, String label) {
		Player player = playerData.getPlayer();

		if(args.length > 0) {
			playerData.terminateScan();
			return;
		}

		if(playerData.isScanning()) {
			MessageSender.sendMessage(player, Message.ALREADY_SCANNING);
			return;
		}
		if(playerData.isInspecting()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_INSPECTING, label);
			return;
		}

		WorldEditSelection selection = playerData.getSelection();

		BlockVector3 origin = selection.getFirstPosition();
		if(origin == null) {
			MessageSender.sendMessage(player, Message.NO_START_SELECTED);
			return;
		}

		playerData.scan(origin, plugin, label);
	}

	private static void inspector(String[] args, PlayerData playerData, String label) {
		switch(args[0]) {
			case "start" -> {
				Player player = playerData.getPlayer();
				if(playerData.isScanning()) {
					MessageSender.sendMessage(player, Message.CURRENTLY_SCANNING, label);
					return;
				}
				if(playerData.isInspecting()) {
					MessageSender.sendMessage(player, Message.ALREADY_INSPECTING);
					return;
				}

				playerData.inspect(plugin);
			}
			case "stop" -> playerData.terminateInspect();
		}
	}

	private static void define_region(String[] args, PlayerData playerData, String label) {
		Player player = playerData.getPlayer();
		if(playerData.isScanning()) {
			MessageSender.sendMessage(player, Message.SCAN_IN_PROGRESS);
			return;
		}

		Region region = playerData.updateRegion();

		if(region == null) {
			MessageSender.sendMessage(player, Message.NO_SCAN_REGION);
			return;
		}

		MessageSender.sendMessage(player, Message.SET_SCAN_REGION, formatRegion(region));
	}

	private static void help(String[] args, PlayerData playerData, String label) {
		MessageSender.sendHelpMessage(playerData.getPlayer(), syntaxHandler.getOptionsRoot(), label);
	}

	public static void sendInspectorMessageSubtitle(Player player, RedstoneTracerGraphPath path) {
		switch(path.result()) {
			case RedstoneTracerGraphPathResult.PATH_FOUND -> MessageSender.sendSubtitle(player, Message.DELAY_SHORT, (path.delay() / 2) + "");
			case RedstoneTracerGraphPathResult.UNSCANNED_LOCATION -> MessageSender.sendSubtitle(player, Message.UNSCANNED_LOCATION_SHORT);
			case RedstoneTracerGraphPathResult.OUT_OF_BOUNDS -> MessageSender.sendSubtitle(player, Message.OUT_OF_BOUNDS_SHORT);
		}
	}

	public static void sendInspectorMessage(Player player, RedstoneTracerGraphPath path) {
		switch(path.result()) {
			case RedstoneTracerGraphPathResult.PATH_FOUND -> MessageSender.sendMessage(player, Message.DELAY, (path.delay() / 2) + "");
			case RedstoneTracerGraphPathResult.UNSCANNED_LOCATION -> MessageSender.sendMessage(player, Message.UNSCANNED_LOCATION);
			case RedstoneTracerGraphPathResult.OUT_OF_BOUNDS -> MessageSender.sendMessage(player, Message.OUT_OF_BOUNDS);
		}
	}

	private static String formatRegion(Region region) {
		BlockVector3 min = region.getMinimumPoint();
		BlockVector3 max = region.getMaximumPoint();
		return 
			"&3[&b" + 
			min.x()+ "&3, &b" + min.y() + "&3, &b" + min.z() + 
			"&3]&b &3-> [&b" + 
			max.x() + "&3, &b" + max.y() + "&3, &b" + max.z() + 
			"&3]&r";
	}
}