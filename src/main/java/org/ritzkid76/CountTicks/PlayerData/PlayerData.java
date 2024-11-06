package org.ritzkid76.CountTicks.PlayerData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.ritzkid76.CountTicks.WorldEditSelection;
import org.ritzkid76.CountTicks.Exceptions.BoundsUndefinedException;
import org.ritzkid76.CountTicks.Exceptions.PositionOutOfRegionBounds;
import org.ritzkid76.CountTicks.Exceptions.ScanCanceledException;
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
	private ExecutorService scanExecutor;
	private Future<?> scanStatus;

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

	public boolean isScanning() { 
		if(scanExecutor == null) return false;
		return !scanStatus.isDone(); 
	}

	private void scanCallback(boolean success) {
		if(!success) {
			MessageSender.sendMessage(player, Message.INVALID_START);
			return;
		}

		hasScanned = true;
		MessageSender.sendMessage(player, Message.SCAN_COMPLETE, String.valueOf(graph.totalScanned()));
		return;
	}
	public void terminateScan() {
		scanExecutor.shutdownNow();
	}

	public void scan(BlockVector3 origin) {
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
		scanExecutor = Executors.newSingleThreadExecutor();
		scanStatus = scanExecutor.submit(() -> {
			try{
				scanCallback(graph.trace());
			} catch (ScanCanceledException e) {}
		});
	}

	public RedstoneTracerGraphPath getFastestPath(BlockVector3 pos) { return graph.fastestPath(pos); }

	public boolean hasScanned() { return hasScanned; }
}
