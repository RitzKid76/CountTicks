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
		if(args.length == 0) return help(args, playerData);

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

	//TODO check for isScanning() to block mutation of region and other scan dependencies

	private boolean help(String[] args, PlayerData playerData) {
		// Debug.log("Options are:\n" + usageGenerator.usage());
		//TODO temp remap to redstone tracer
		WorldEditSelection selection = playerData.getSelection();
		Player player = playerData.getPlayer();

		if(!playerData.hasScanned()) {
			MessageSender.sendMessage(player, Message.NO_SCANNED_BUILD);
			return true;
		}
		RedstoneTracerGraphPath path = playerData.getFastestPath(selection.getSecondPosition());
		
		switch(path.result()) {
			case RedstoneTracerPathResult.PATH_FOUND -> Debug.log(path.totalGameTicks()/2 + "t");
			case RedstoneTracerPathResult.NO_PATH -> MessageSender.sendMessage(player, Message.NO_PATH);
			case RedstoneTracerPathResult.UNSCANNED_LOCATION -> MessageSender.sendMessage(player, Message.UNSCANNED_LOCATION);
			case RedstoneTracerPathResult.OUT_OF_BOUNDS -> MessageSender.sendMessage(player, Message.OUT_OF_BOUNDS);
		}

		return true;
	}

	private boolean scan(String[] args, PlayerData playerData) {
		Player player = playerData.getPlayer();

		if(args.length > 0) {
			if(playerData.isScanning()) {
				playerData.terminateScan();
				MessageSender.sendMessage(player, Message.CANCELED_SCAN);
			} else 
				MessageSender.sendMessage(player, Message.NO_ACTIVE_SCAN);
			return true;
		}

		if(playerData.isScanning()) {
			MessageSender.sendMessage(player, Message.ALREADY_SCANNING);
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
		Debug.log("inspector");
		return true;
	}
	
	private boolean set_region(String[] args, PlayerData playerData) {
		Region region = playerData.updateRegion();
		Player player = playerData.getPlayer();

		if(region == null) {
			MessageSender.sendMessage(player, Message.NO_SCAN_REGION);
			return true;
		}

		MessageSender.sendMessage(player, Message.SET_SCAN_REGION, region.toString());
		return true;
	}
}