package org.ritzkid76.CountTicks.SyntaxHandling;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.ritzkid76.CountTicks.Debug;
import org.ritzkid76.CountTicks.WorldEditSelection;
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

	private boolean help(String[] args, PlayerData playerData) {
		// Debug.log("Options are:\n" + usageGenerator.usage());
		//TODO temp remap to redstone tracer
		WorldEditSelection selection = playerData.getSelection();

		if(!playerData.hasScanned()) {
			Debug.log("no scanned build");
			return true;
		}
		RedstoneTracerGraphPath path = playerData.getFastestPath(selection.getSecondPosition());

		switch(path.result()) {
			case RedstoneTracerPathResult.PATH_FOUND -> Debug.log(path.totalGameTicks()/2 + "t");
			case RedstoneTracerPathResult.NO_PATH -> Debug.log("no path");
			case RedstoneTracerPathResult.UNSCANNED_LOCATION -> { Debug.log("unscanned location"); }
			case RedstoneTracerPathResult.OUT_OF_BOUNDS -> { Debug.log("out of bounds"); }
		}

		return true;
	}

	private boolean scan(String[] args, PlayerData playerData) {
		WorldEditSelection selection = playerData.getSelection();
		
		BlockVector3 origin = selection.getFirstPosition();
		if(origin == null) {
			Debug.log("no first position found");
			return true;
		}

		if(!playerData.scan(origin)) {
			Debug.log("could not scan at selected position");
		}

		return true;
	}
	
	private boolean inspector(String[] args, PlayerData playerData) {
		Debug.log("inspector");
		return true;
	}
	
	private boolean set_region(String[] args, PlayerData playerData) {
		Region region = playerData.updateRegion();
		if(region == null) {
			Debug.log("null region");
			return true;
		}

		return true;
	}
}