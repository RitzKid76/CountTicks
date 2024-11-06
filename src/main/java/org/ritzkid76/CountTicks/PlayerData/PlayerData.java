package org.ritzkid76.CountTicks.PlayerData;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.ritzkid76.CountTicks.WorldEditSelection;
import org.ritzkid76.CountTicks.Exceptions.BoundsUndefinedException;
import org.ritzkid76.CountTicks.Exceptions.NonTraceableStartPositionException;
import org.ritzkid76.CountTicks.Exceptions.PositionOutOfRegionBounds;
import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraph;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraphPath;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;

public class PlayerData {
	private Player player;
	private CuboidRegion playerRegion;
	private RedstoneTracerGraph graph;
	private WorldEditSelection selection;

	private boolean hasScanned;
	private boolean isScanning;

	public PlayerData(Player p) {
		player = p;
		selection = new WorldEditSelection(p);
	}

	public Player getPlayer() { return player; }
	public World getWorld() { return player.getWorld(); }
	public WorldEditSelection getSelection() { return selection; }
	public CuboidRegion getRegion() { return playerRegion; }

	public CuboidRegion updateRegion() {
		CuboidRegion region = (CuboidRegion) selection.getRegion();
		if(region == null)
			return null;
		playerRegion = region.clone();
		return playerRegion;
	}

	public boolean isScanning() { return isScanning; }

	private void executeScan() {
		isScanning = true;

		CompletableFuture.supplyAsync(() -> {
			return graph.trace();
		}).thenAccept(traceResult -> {
			executeScanCallback(traceResult);
		}).exceptionally(e -> {
			throw new RuntimeException(e);
		});
	}
	private void executeScanCallback(boolean success) {
		isScanning = false;

		if(!success) {
			MessageSender.sendMessage(player, Message.INVALID_START);
			return;
		}

		hasScanned = true;
		MessageSender.sendMessage(player, Message.SCAN_COMPLETE, String.valueOf(graph.totalScanned()));
		return;
	}

	public void scan(BlockVector3 origin) {
		if(isScanning) return;

		try {
			graph = new RedstoneTracerGraph(origin, playerRegion);
		} catch (PositionOutOfRegionBounds e) {
			MessageSender.sendMessage(player, Message.OUT_OF_BOUNDS);
			return;
		} catch (BoundsUndefinedException e) {
			MessageSender.sendMessage(player, Message.NO_SCAN_REGION);
			return;
		}

		MessageSender.sendMessage(player, Message.ATTEMPTING_SCAN);
		executeScan();
	}
	// public void scan(BlockVector3 origin) {
	// 	try {
	// 		graph = new RedstoneTracerGraph(origin, playerRegion);

	// 		MessageSender.sendMessage(player, Message.ATTEMPTING_SCAN);
	// 		executeScan();

	// 		if(!graph.trace()) {
	// 			MessageSender.sendMessage(player, Message.INVALID_START);
	// 			return;
	// 		}
	// 	} catch (NullPointerException e) {
	// 		MessageSender.sendMessage(player, Message.NO_SCAN_REGION);
	// 		return;
	// 	} catch (PositionOutOfRegionBounds e) {
	// 		MessageSender.sendMessage(player, Message.OUT_OF_BOUNDS);
	// 		return;
	// 	} catch (Exception e) {
	// 		throw new RuntimeException(e);
	// 	}
	// 	hasScanned = true;
	// 	MessageSender.sendMessage(player, Message.SCAN_COMPLETE, String.valueOf(graph.totalScanned()));
	// 	return;
	// }

	public RedstoneTracerGraphPath getFastestPath(BlockVector3 pos) { return graph.fastestPath(pos); }

	public boolean hasScanned() { return hasScanned; }
}
