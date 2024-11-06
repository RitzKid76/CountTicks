package org.ritzkid76.CountTicks.PlayerData;

import java.util.UUID;
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
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraph;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraphPath;
import org.ritzkid76.CountTicks.SyntaxHandling.ArgumentParser;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;

public class PlayerData {
	private UUID uuid;
	private CuboidRegion playerRegion;
	private RedstoneTracerGraph graph;
	private WorldEditSelection selection;

	private ExecutorService scanExecutor;
	private Future<?> scanStatus;

	private ExecutorService inspectExecutor;
	private Future<?> inspectStatus;

	public PlayerData(UUID u) {
		uuid = u;
		selection = new WorldEditSelection(getPlayer());
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}
	public World getWorld() { return getPlayer().getWorld(); }
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

	private void scanCallback(boolean success, Runnable returnTo) {
		Player player = getPlayer();

		if(!success) {
			MessageSender.sendMessage(player, Message.INVALID_START);
			return;
		}

		MessageSender.sendMessage(player, Message.SCAN_COMPLETE, String.valueOf(graph.totalScanned()));
		
		if(returnTo == null) return;
		returnTo.run();
	}
	public void terminateScan() { terminateScan(false); }
	public void terminateScan(boolean silent) {
		Player player = getPlayer();

		if(!isScanning()) {
			if(!silent) MessageSender.sendMessage(player, Message.NO_ACTIVE_SCAN);
			return;
		};
		scanExecutor.shutdownNow();
		if(!silent) MessageSender.sendMessage(player, Message.STOP_SCAN);
	}

	public void scan(BlockVector3 origin) { scan(origin, null); }
	private void scan(BlockVector3 origin, Runnable returnTo) {
		Player player = getPlayer();

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
				scanCallback(graph.trace(), returnTo);
			} catch (ThreadCanceledException e) {}
		});
	}
	
	public void terminateInspect() { terminateInspect(false); }
	public void terminateInspect(boolean silent) {
		Player player = getPlayer();

		if(!isInspecting()) {
			if(!silent) MessageSender.sendMessage(player, Message.NO_ACTIVE_INSPECTION);
			return;
		};
		inspectExecutor.shutdownNow();
		if(!silent) MessageSender.sendMessage(player, Message.STOP_INSPECT_MODE);
	}

	public void inspect() {
		Player player = getPlayer();

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
				
				ArgumentParser.sendInspectorMessageSubtitle(player, graph.fastestPath(viewedBlock));
			}
		});
	}
	
	private BlockVector3 callbackEndpoint;
	private void countCallback() {
		ArgumentParser.sendInspectorMessage(getPlayer(), graph.fastestPath(callbackEndpoint));
	}
	public void count(BlockVector3 start, BlockVector3 end) {
		callbackEndpoint = end;

		if(scanValidation(start, this::countCallback)) return;
		countCallback();
	}

	private boolean scanValidation(BlockVector3 origin, Runnable callback) {
		if(!hasScanned()) {
			scan(origin, callback);
			return true;
		}
		if(graph.getOrigin() != origin) {
			MessageSender.sendMessage(getPlayer(), Message.START_CHANGED);
			scan(origin, this::countCallback);
			return true;
		}

		return false;
	}

	public RedstoneTracerGraphPath getFastestPath(BlockVector3 pos) { return graph.fastestPath(pos); }

	public boolean hasScanned() { 
		if(graph == null) return false;
		return graph.totalScanned() > 0; 
	}

	public void shutdown() {
		terminateScan(true);
		terminateInspect(true);
	}
}
