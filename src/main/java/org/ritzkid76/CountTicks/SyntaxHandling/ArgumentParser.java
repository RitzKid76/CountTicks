package org.ritzkid76.CountTicks.SyntaxHandling;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraphPath;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraphPathResult;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

public class ArgumentParser {
	private final SyntaxHandler syntaxHandler;
	private final Plugin plugin;

	public ArgumentParser(File dataFolder, Plugin p) {
		syntaxHandler = new SyntaxHandler(dataFolder);
		plugin = p;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return syntaxHandler.onTabComplete(sender, command, label, args);
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
			).invoke(this, args, playerData, label);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void count(String[] args, PlayerData playerData, String label) {
		Player player = playerData.getPlayer();

		if(playerData.isScanning()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_SCANNING, label);
			return;
		}
		if(playerData.isInspecting()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_INSPECTING, label);
			return;
		}

		BlockVector3 startPosition = playerData.getFirstPosition();
		if(startPosition == null) {
			MessageSender.sendMessage(player, Message.NO_START_SELECTED);
			return;
		}
		BlockVector3 endPosition = playerData.getSecondPosition();
		if(endPosition == null) {
			MessageSender.sendMessage(player, Message.NO_END_SELECTED);
			return;
		}

		playerData.count(startPosition, endPosition, plugin, label);
	}

	@SuppressWarnings("unused")
	private void scan(String[] args, PlayerData playerData, String label) {
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

		BlockVector3 origin = playerData.getFirstPosition();
		if(origin == null) {
			MessageSender.sendMessage(player, Message.NO_START_SELECTED);
			return;
		}

		playerData.scan(origin, plugin, label);
	}

	@SuppressWarnings("unused")
	private void inspector(String[] args, PlayerData playerData, String label) {
		if(args.length == 0) {
			playerData.toggleInspector(plugin, label);
			return;
		}
		
		switch(args[0]) {
			case "start" -> playerData.inspect(plugin, label);
			case "stop" -> playerData.terminateInspect();
		}
	}

	@SuppressWarnings("unused")
	private void define_region(String[] args, PlayerData playerData, String label) {
		Player player = playerData.getPlayer();
		if(playerData.isScanning()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_SCANNING, label);
			return;
		}
		if(playerData.isInspecting()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_INSPECTING, label);
			return;
		}

		Region region = playerData.updateRegion(label);
		if(region == null)
			return;

		BlockVector3 min = region.getMinimumPoint();
		BlockVector3 max = region.getMaximumPoint();
		MessageSender.sendMessage(
			player, Message.SET_SCAN_REGION,
			String.valueOf(min.x()), String.valueOf(min.y()), String.valueOf(min.z()),
			String.valueOf(max.x()), String.valueOf(max.y()), String.valueOf(max.z())
		);
	}

	@SuppressWarnings("unused")
	private void stop(String[] args, PlayerData playerData, String label) {
		playerData.shutdown();
		MessageSender.sendMessage(playerData.getPlayer(), Message.STOPPED_ALL);
	}

	@SuppressWarnings("unused")
	private void help(String[] args, PlayerData playerData, String label) {
		MessageSender.sendHelpMessage(playerData.getPlayer(), syntaxHandler.getOptionsRoot(), label);
	}

	public static void sendInspectorMessageSubtitle(Player player, RedstoneTracerGraphPath path) {
		switch(path.result()) {
			case RedstoneTracerGraphPathResult.PATH_FOUND -> MessageSender.sendSubtitle(player, Message.DELAY_SHORT, String.valueOf(path.delay() / 2));
			case RedstoneTracerGraphPathResult.UNSCANNED_LOCATION -> MessageSender.sendSubtitle(player, Message.UNSCANNED_LOCATION_SHORT);
			case RedstoneTracerGraphPathResult.OUT_OF_BOUNDS -> MessageSender.sendSubtitle(player, Message.OUT_OF_BOUNDS_SHORT);
		}
	}

	public static void sendInspectorMessage(Player player, RedstoneTracerGraphPath path) {
		switch(path.result()) {
			case RedstoneTracerGraphPathResult.PATH_FOUND -> MessageSender.sendMessage(player, Message.DELAY, String.valueOf(path.delay() / 2));
			case RedstoneTracerGraphPathResult.UNSCANNED_LOCATION -> MessageSender.sendMessage(player, Message.UNSCANNED_LOCATION);
			case RedstoneTracerGraphPathResult.OUT_OF_BOUNDS -> MessageSender.sendMessage(player, Message.OUT_OF_BOUNDS);
		}
	}
}