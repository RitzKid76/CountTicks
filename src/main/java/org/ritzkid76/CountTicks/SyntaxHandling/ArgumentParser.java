package org.ritzkid76.CountTicks.SyntaxHandling;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

	public static void setDataFolder(File dataFolder) {
		syntaxHandler = new SyntaxHandler(dataFolder);
	}

	public static List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return syntaxHandler.onTabComplete(sender, command, label, args);
	}

	private static void getUsage(String[] args, PlayerData playerData) {
		SyntaxEntry current = syntaxHandler.getOptionsRoot();
		StringBuilder output = new StringBuilder("/countticks ");

		for(String arg : args) {
			SyntaxEntry next = current.get(arg);

			if(next == null)
				break;

			output.append(arg).append(" ");
			current = next;
		}

		output.append(current.toSyntaxString());
		MessageSender.sendMessage(playerData.getPlayer(), Message.INVALID_SYNTAX, output.toString());
	}

	public static void run(String[] args, PlayerData playerData) {
		if(!syntaxHandler.isValidSyntax(args)) {
			getUsage(args, playerData);
			return;
		}

		if(args.length == 0) {
			count(args, playerData);
			return;
		}

		String methodName = args[0];
		args = Arrays.copyOfRange(args, 1, args.length);

		try {
			ArgumentParser.class.getDeclaredMethod(
				methodName,
				String[].class,
				PlayerData.class
			).invoke(null, args, playerData);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void count(String[] args, PlayerData playerData) {
		WorldEditSelection selection = playerData.getSelection();
		Player player = playerData.getPlayer();

		if(playerData.isScanning()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_SCANNING);
			return;
		}
		if(playerData.isInspecting()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_INSPECTING);
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

		playerData.count(startPosition, endPosition);
	}

	private static void scan(String[] args, PlayerData playerData) {
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
			MessageSender.sendMessage(player, Message.CURRENTLY_INSPECTING);
			return;
		}

		WorldEditSelection selection = playerData.getSelection();

		BlockVector3 origin = selection.getFirstPosition();
		if(origin == null) {
			MessageSender.sendMessage(player, Message.NO_START_SELECTED);
			return;
		}

		playerData.scan(origin);
	}

	private static void inspector(String[] args, PlayerData playerData) {
		switch(args[0]) {
			case "start" -> {
				Player player = playerData.getPlayer();
				if(playerData.isScanning()) {
					MessageSender.sendMessage(player, Message.CURRENTLY_SCANNING);
					return;
				}
				if(playerData.isInspecting()) {
					MessageSender.sendMessage(player, Message.ALREADY_INSPECTING);
					return;
				}

				playerData.inspect();
			}
			case "stop" -> playerData.terminateInspect();
		}
	}

	private static void define_region(String[] args, PlayerData playerData) {
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

	private static void help(String[] args, PlayerData playerData) {
		MessageSender.sendMessage(playerData.getPlayer(), Message.HELP, syntaxHandler.getOptionsRoot().toSyntaxList());
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
}