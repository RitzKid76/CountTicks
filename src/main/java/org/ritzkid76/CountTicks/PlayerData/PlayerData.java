package org.ritzkid76.CountTicks.PlayerData;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.ritzkid76.CountTicks.Exceptions.BoundsUndefinedException;
import org.ritzkid76.CountTicks.Exceptions.PositionOutOfRegionBoundsException;
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

	private BukkitTask scanTask;
	private BukkitTask inspectTask;

	public PlayerData(UUID u) {
		uuid = u;
		selection = new WorldEditSelection(getPlayer());
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}
	public World getWorld() {
		return getPlayer().getWorld();
	}
	public WorldEditSelection getSelection() {
		return selection;
	}
	public CuboidRegion getRegion() {
		return playerRegion;
	}

	public CuboidRegion updateRegion() {
		CuboidRegion region = (CuboidRegion) selection.getRegion();
		if(region == null)
			return null;
		playerRegion = region.clone();
		return playerRegion;
	}

	public boolean isScanning() {
		if(scanTask == null)
			return false;
		return !scanTask.isCancelled();
	}
	public boolean isInspecting() {
		if(inspectTask == null)
			return false;
		return !inspectTask.isCancelled();
	}

	private String getFormattedTimer(long difference) {
		double seconds = (double) difference / 1000.0;
		return String.format("%.2f", seconds);
	}

	private void scanCallback(boolean success, Runnable returnTo, long startTime) {
		Player player = getPlayer();
		scanTask.cancel(); // has to be done since this flag is not set on task completion

		if(!success) {
			MessageSender.sendMessage(player, Message.INVALID_START);
			return;
		}

		MessageSender.sendMessage(
			player, 
			Message.SCAN_COMPLETE, 
			getFormattedTimer(System.currentTimeMillis() - startTime),
			String.valueOf(graph.totalScanned())
		);

		if(returnTo == null)
			return;
		returnTo.run();
	}
	public void terminateScan() {
		terminateScan(false);
	}
	public void terminateScan(boolean silent) {
		Player player = getPlayer();

		if(!isScanning()) {
			if(!silent)
				MessageSender.sendMessage(player, Message.NO_ACTIVE_SCAN);
			return;
		}

		scanTask.cancel();

		if(!silent)
			MessageSender.sendMessage(player, Message.STOP_SCAN);
	}

	public void scan(BlockVector3 origin, Plugin plugin, String label) {
		scan(origin, null, plugin, label);
	}
	private void scan(BlockVector3 origin, Runnable returnTo, Plugin plugin, String label) {
		Player player = getPlayer();

		try {
			graph = new RedstoneTracerGraph(origin, playerRegion);
		} catch (PositionOutOfRegionBoundsException e) {
			MessageSender.sendMessage(player, Message.OUT_OF_BOUNDS);
			return;
		} catch (BoundsUndefinedException e) {
			MessageSender.sendMessage(player, Message.NO_SCAN_REGION, label);
			return;
		}

		MessageSender.sendMessage(player, Message.START_SCAN);

		long startTime = System.currentTimeMillis();
		scanTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try {
				scanCallback(graph.trace(scanTask, startTime, player), returnTo, startTime);
			} catch (ThreadCanceledException e) {}
		});
	}

	public void terminateInspect() {
		terminateInspect(false);
	}
	public void terminateInspect(boolean silent) {
		Player player = getPlayer();

		if(!isInspecting()) {
			if(!silent)
				MessageSender.sendMessage(player, Message.NO_ACTIVE_INSPECTION);
			return;
		}

		inspectTask.cancel();

		if(!silent)
			MessageSender.sendMessage(player, Message.STOP_INSPECT_MODE);
	}

	public void inspect(Plugin plugin, String label) {
		Player player = getPlayer();

		if(!hasScanned()) {
			MessageSender.sendMessage(player, Message.NO_SCANNED_BUILD, label);
			return;
		}

		MessageSender.sendMessage(player, Message.START_INSPECT_MODE);

		class BlockVector3Wrapper {
			BlockVector3 blockVector3;
		}

		AtomicBoolean canEnterSafeZone = new AtomicBoolean(true);
		BlockVector3Wrapper wrapper = new BlockVector3Wrapper();

		inspectTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			if(!canEnterSafeZone.compareAndSet(true, false))
				return;

			try {
				BlockVector3 viewedBlock = BlockUtils.getBlockLookingAt(player, 10);

				if(viewedBlock == null || viewedBlock.equals(wrapper.blockVector3))
					return;
				wrapper.blockVector3 = viewedBlock;

				ArgumentParser.sendInspectorMessageSubtitle(player, graph.findFastestPath(viewedBlock));
			} finally {
				canEnterSafeZone.set(true);
			}
		}, 0, 1);
	}

	private BlockVector3 callbackEndpoint;
	private void countCallback() {
		ArgumentParser.sendInspectorMessage(getPlayer(), graph.findFastestPath(callbackEndpoint));
	}
	public void count(BlockVector3 start, BlockVector3 end, Plugin plugin, String label) {
		callbackEndpoint = end;

		if(scanValidation(start, this::countCallback, plugin, label))
			return;
		countCallback();
	}

	private boolean scanValidation(BlockVector3 origin, Runnable callback, Plugin plugin, String label) {
		if(!hasScanned()) {
			scan(origin, callback, plugin, label);
			return true;
		}
		if(graph.getOrigin() != origin) {
			MessageSender.sendMessage(getPlayer(), Message.START_CHANGED);
			scan(origin, this::countCallback, plugin, label);
			return true;
		}
		if(graph.getRegion() != playerRegion) {
			MessageSender.sendMessage(getPlayer(), Message.REGION_CHANGED);
			updateRegion();
			scan(origin, this::countCallback, plugin, label);
			return true;
		}

		return false;
	}

	public RedstoneTracerGraphPath getFastestPath(BlockVector3 pos) {
		return graph.findFastestPath(pos);
	}

	public boolean hasScanned() {
		if(graph == null)
			return false;
		return graph.totalScanned() > 0;
	}

	public void shutdown() {
		terminateScan(true);
		terminateInspect(true);
	}
}
