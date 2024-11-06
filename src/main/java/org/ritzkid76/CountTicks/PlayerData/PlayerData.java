package org.ritzkid76.CountTicks.PlayerData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.ritzkid76.CountTicks.WorldEditSelection;
import org.ritzkid76.CountTicks.Exceptions.BoundsUndefinedException;
import org.ritzkid76.CountTicks.Exceptions.PositionOutOfRegionBounds;
import org.ritzkid76.CountTicks.Exceptions.ThreadCanceledException;
import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.RedstoneTracer.BlockUtils;
import org.ritzkid76.CountTicks.RedstoneTracer.RedstoneTracerPathResult;
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

	private ExecutorService inspectExecutor;
	private Future<?> inspectStatus;

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
	public boolean isInspecting() {
		if(inspectStatus == null) return false;
		return !inspectStatus.isDone();
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
	public void terminateScan() { terminateScan(false); }
	public void terminateScan(boolean silent) {
		if(!isScanning()) {
			if(!silent) MessageSender.sendMessage(player, Message.NO_ACTIVE_SCAN);
			return;
		};
		scanExecutor.shutdownNow();
		if(!silent) MessageSender.sendMessage(player, Message.STOP_SCAN);
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
		
		MessageSender.sendMessage(player, Message.START_SCAN);
		scanExecutor = Executors.newSingleThreadExecutor();
		scanStatus = scanExecutor.submit(() -> {
			try{
				scanCallback(graph.trace());
			} catch (ThreadCanceledException e) {}
		});
	}
	
	public void terminateInspect() { terminateInspect(false); }
	public void terminateInspect(boolean silent) {
		if(!isInspecting()) {
			if(!silent) MessageSender.sendMessage(player, Message.NO_ACTIVE_INSPECTION);
			return;
		};
		inspectExecutor.shutdownNow();
		if(!silent) MessageSender.sendMessage(player, Message.STOP_INSPECT_MODE);
	}
	public void inspect() {
		if(!hasScanned) {
			MessageSender.sendMessage(player, Message.NO_SCANNED_BUILD);
			return;
		}
		
		MessageSender.sendMessage(player, Message.START_INSPECT_MODE);
		
		inspectExecutor = Executors.newSingleThreadExecutor();
		inspectStatus = inspectExecutor.submit(() -> {
			BlockVector3 lastViewBlock = null;

			while(!Thread.currentThread().isInterrupted()) {
				BlockVector3 viewedBlock = BlockUtils.getBlockLookingAt(player, 10);

				if(
					viewedBlock == null ||
					viewedBlock.equals(lastViewBlock)
				) {
					continue;
				}
				lastViewBlock = viewedBlock;
				
				RedstoneTracerGraphPath path = graph.fastestPath(viewedBlock);
				
				switch(path.result()) {
					case RedstoneTracerPathResult.PATH_FOUND -> MessageSender.sendSubtitle(player, Message.DELAY, path.delay()/2 + "");
					case RedstoneTracerPathResult.NO_PATH -> MessageSender.sendSubtitle(player, Message.NO_PATH);
					case RedstoneTracerPathResult.UNSCANNED_LOCATION -> MessageSender.sendSubtitle(player, Message.UNSCANNED_LOCATION);
					case RedstoneTracerPathResult.OUT_OF_BOUNDS -> MessageSender.sendSubtitle(player, Message.OUT_OF_BOUNDS);
				}
					
			}
		});
	}

	public RedstoneTracerGraphPath getFastestPath(BlockVector3 pos) { return graph.fastestPath(pos); }

	public boolean hasScanned() { return hasScanned; }

	public void shutdown() {
		terminateScan(true);
		terminateInspect(true);
	}
}
