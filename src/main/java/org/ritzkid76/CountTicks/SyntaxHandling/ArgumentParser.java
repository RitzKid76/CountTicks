package org.ritzkid76.CountTicks.SyntaxHandling;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.bukkit.entity.Player;
import org.ritzkid76.CountTicks.Debug;
import org.ritzkid76.CountTicks.WorldEditSelection;
import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;
import org.ritzkid76.CountTicks.RedstoneTracer.RedstoneTracerPathResult;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraphPath;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

public class ArgumentParser {
	private SyntaxHandler syntaxHandler;
	private UsageGenerator usageGenerator;

	public ArgumentParser(SyntaxHandler handler, UsageGenerator usage) {
		syntaxHandler = handler;
		usageGenerator = usage;
	}

	public boolean run(String[] args, PlayerData playerData) {
		if(args.length == 0) return count(args, playerData);

		if(!syntaxHandler.isValidSyntax(args)) return false;

		String methodName = args[0];
		args = Arrays.copyOfRange(args, 1, args.length);

		Method method;
		try {
			method = getClass().getDeclaredMethod(
				methodName,
				String[].class,
				PlayerData.class
			);

			return (boolean) method.invoke(this, (Object) args, playerData);
		} 
		catch (NoSuchMethodException e) { 
			Debug.log("syntax validator said yes, but it should say no");
			return false;
		} 
		catch (Exception e) { throw new RuntimeException(e); }
	}

	private boolean count(String[] args, PlayerData playerData) {
		WorldEditSelection selection = playerData.getSelection();
		Player player = playerData.getPlayer();

		if(playerData.isScanning()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_SCANNING);
			return true;
		}
		if(playerData.isInspecting()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_INSPECTING);
			return true;
		}

		BlockVector3 startPosition = selection.getFirstPosition();
		if(startPosition == null) {
			MessageSender.sendMessage(player, Message.NO_START_SELECTED);
			return true;
		}
		BlockVector3 endPosition = selection.getSecondPosition();
		if(endPosition == null) {
			MessageSender.sendMessage(player, Message.NO_END_SELECTED);
			return true;
		}

		playerData.count(startPosition, endPosition);

		return true;
	}

	private boolean scan(String[] args, PlayerData playerData) {
		Player player = playerData.getPlayer();

		if(args.length > 0) {
			playerData.terminateScan();
			return true;
		}

		if(playerData.isScanning()) {
			MessageSender.sendMessage(player, Message.ALREADY_SCANNING);
			return true;
		}
		if(playerData.isInspecting()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_INSPECTING);
			return true;
		}

		WorldEditSelection selection = playerData.getSelection();
		
		BlockVector3 origin = selection.getFirstPosition();
		if(origin == null) {
			MessageSender.sendMessage(player, Message.NO_START_SELECTED);
			return true;
		}

		playerData.scan(origin);

		return true;
	}
	
	private boolean inspector(String[] args, PlayerData playerData) {
		if(args.length == 0) return false;

		switch(args[0]) {
			case "start" -> {
				Player player = playerData.getPlayer();
				if(playerData.isScanning()) {
					MessageSender.sendMessage(player, Message.CURRENTLY_SCANNING);
					return true;
				}
				if(playerData.isInspecting()) {
					MessageSender.sendMessage(player, Message.ALREADY_INSPECTING);
					return true;
				}

				playerData.inspect();
			}
			case "stop" -> playerData.terminateInspect();
		}
		return true;
	}
	
	private boolean define_region(String[] args, PlayerData playerData) {
		Player player = playerData.getPlayer();
		if(playerData.isScanning()) {
			MessageSender.sendMessage(player, Message.SCAN_IN_PROGRESS);
		}

		Region region = playerData.updateRegion();

		if(region == null) {
			MessageSender.sendMessage(player, Message.NO_SCAN_REGION);
			return true;
		}

		MessageSender.sendMessage(player, Message.SET_SCAN_REGION, region.toString());
		return true;
	}

	
	public static void sendInspectorMessageSubtitle(Player player, RedstoneTracerGraphPath path) {
		switch(path.result()) {
			case RedstoneTracerPathResult.PATH_FOUND -> MessageSender.sendSubtitle(player, Message.DELAY_SHORT, path.delay()/2 + "");
			case RedstoneTracerPathResult.UNSCANNED_LOCATION -> MessageSender.sendSubtitle(player, Message.UNSCANNED_LOCATION_SHORT);
			case RedstoneTracerPathResult.OUT_OF_BOUNDS -> MessageSender.sendSubtitle(player, Message.OUT_OF_BOUNDS_SHORT);
		}
	}

	public static void sendInspectorMessage(Player player, RedstoneTracerGraphPath path) {
		switch(path.result()) {
			case RedstoneTracerPathResult.PATH_FOUND -> MessageSender.sendMessage(player, Message.DELAY, path.delay()/2 + "");
			case RedstoneTracerPathResult.UNSCANNED_LOCATION -> MessageSender.sendMessage(player, Message.UNSCANNED_LOCATION);
			case RedstoneTracerPathResult.OUT_OF_BOUNDS -> MessageSender.sendMessage(player, Message.OUT_OF_BOUNDS);
		}
	}
}