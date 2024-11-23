package org.ritzkid76.CountTicks.PlayerData;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.ritzkid76.CountTicks.Exceptions.BoundsUndefinedException;
import org.ritzkid76.CountTicks.Exceptions.PositionOutOfRegionBoundsException;
import org.ritzkid76.CountTicks.Exceptions.ThreadCanceledException;
import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.RedstoneTracer.BlockGetter;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraph;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraphPath;
import org.ritzkid76.CountTicks.SyntaxHandling.ArgumentParser;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

public class PlayerData {
	private UUID uuid;
	private Plugin plugin;

	private CuboidRegion playerRegion;
	private RedstoneTracerGraph graph;

	private BukkitTask scanTask;
	private BukkitTask inspectTask;
	private BukkitTask timerTask;
	private BukkitTask pulseTask;

	public PlayerData(UUID u, Plugin p) {
		uuid = u;
		plugin = p;
	}

	class LongWrapper {
		long lOng;
		LongWrapper(long l) {
			lOng = l;
		}
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public World getWorld() {
		return getPlayer().getWorld();
	}

	public CuboidRegion getRegion() {
		return playerRegion;
	}

	public CuboidRegion updateRegion(String label) {
		Region region = WorldEditSelection.getRegion(uuid);
		if(region == null) {
			MessageSender.sendMessage(getPlayer(), Message.NO_SCAN_REGION, label);
			return null;
		}	
		if(!(region instanceof CuboidRegion cuboidRegion)) {
			MessageSender.sendMessage(getPlayer(), Message.NON_CUBOID_REGION);
			return null;
		}
		playerRegion = cuboidRegion.clone();
		return playerRegion;
	}

	public BlockVector3 getFirstPosition() {
		return WorldEditSelection.getFirstPosition(uuid);
	}

	public BlockVector3 getSecondPosition() {
		return WorldEditSelection.getSecondPosition(uuid);
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
	public boolean isTiming() {
		if(timerTask == null)
			return false;
		return !timerTask.isCancelled();
	}
	public boolean isPulsing() {
		if(pulseTask == null)
			return false;
		return !pulseTask.isCancelled();
	}

	private String getFormattedTimer(long difference) {
		double seconds = (double) difference / 1000.0;
		return String.format("%.2f", seconds);
	}
	private String getFormattedTicks(long difference) {
		return String.valueOf(difference/2);
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
			MessageSender.sendMessage(player, Message.STOP_SCAN_MODE);
	}

	public void scan(BlockVector3 origin, String label) {
		scan(origin, null, label);
	}
	private void scan(BlockVector3 origin, Runnable returnTo, String label) {
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

		MessageSender.sendMessage(player, Message.START_SCAN_MODE);

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

	public void terminateTimer() {
		terminateTimer(false);
	}
	public void terminateTimer(boolean silent) {
		Player player = getPlayer();

		if(!isTiming()) {
			if(!silent)
				MessageSender.sendMessage(player, Message.NO_ACTIVE_TIMING);
			return;
		}

		timerTask.cancel();

		if(!silent)
			MessageSender.sendMessage(player, Message.STOP_TIMER_MODE);
	}

	public void terminatePulse() {
		terminatePulse(false);
	}
	public void terminatePulse(boolean silent) {
		Player player = getPlayer();

		if(!isPulsing()) {
			if(!silent)
				MessageSender.sendMessage(player, Message.NO_ACTIVE_PULSING);
			return;
		}

		pulseTask.cancel();

		if(!silent)
			MessageSender.sendMessage(player, Message.STOP_PULSE_MODE);
	}

	public void toggleInspector(String label) {
		if(!isInspecting())
			inspect(label);
		else
			terminateInspect();
	}

	public void inspect(String label) {
		Player player = getPlayer();

		if(isScanning()) {
			MessageSender.sendMessage(player, Message.CURRENTLY_SCANNING, label);
			return;
		}
		if(isInspecting()) {
			MessageSender.sendMessage(player, Message.ALREADY_INSPECTING);
			return;
		}
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
				BlockVector3 viewedBlock = BlockGetter.getBlockLookingAt(player, 10);

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
	public void count(BlockVector3 start, BlockVector3 end, String label) {
		callbackEndpoint = end;

		if(scanValidation(start, this::countCallback, label))
			return;
		countCallback();
	}

	private boolean scanValidation(BlockVector3 origin, Runnable callback, String label) {
		if(!hasScanned()) {
			scan(origin, callback, label);
			return true;
		}
		if(graph.getOrigin() != origin) {
			MessageSender.sendMessage(getPlayer(), Message.START_CHANGED);
			scan(origin, this::countCallback, label);
			return true;
		}
		if(graph.getRegion() != playerRegion) {
			MessageSender.sendMessage(getPlayer(), Message.REGION_CHANGED);
			updateRegion(label);
			scan(origin, this::countCallback, label);
			return true;
		}

		return false;
	}

	private void timeTicks(BlockVector3 pos, BlockState startState, long startTicks) {
		LongWrapper timeProgress = new LongWrapper(startTicks);
		
		timerTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			long currentTime = System.currentTimeMillis();
			long difference = currentTime - timeProgress.lOng;
			if(difference >= 5000L) {
				timeProgress.lOng = currentTime;
				MessageSender.sendMessage(getPlayer(), Message.TIMING_PROGRESS, getFormattedTicks(getWorld().getGameTime() - startTicks));
			}

			if(startState.equals(BlockGetter.blockStateFromBlockVector3(getWorld(), pos)))
				return;

			timerTask.cancel();
			long totalTicks = getWorld().getGameTime() - startTicks;
			MessageSender.sendMessage(getPlayer(), Message.DELAY, getFormattedTicks(totalTicks));
		}, 0, 1);
	}

	public void timer(BlockVector3 startPosition, BlockVector3 endPosition, String label) {
		Player player = getPlayer();
		
		if(isTiming()) {
			MessageSender.sendMessage(player, Message.ALREADY_TIMING);
			return;
		}
		
		MessageSender.sendMessage(player, Message.TIMER_WAITING);

		BlockState startPosState = BlockGetter.blockStateFromBlockVector3(getWorld(), startPosition);
		BlockState endPosState = BlockGetter.blockStateFromBlockVector3(getWorld(), endPosition);

		long startTime = System.currentTimeMillis();
		timerTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			long currentTime = System.currentTimeMillis();
			if(currentTime - startTime > 10000L) {
				MessageSender.sendMessage(player, Message.TIMER_TIMEOUT);
				timerTask.cancel();
				return;
			}
			
			if(startPosState.equals(BlockGetter.blockStateFromBlockVector3(getWorld(), startPosition)))
				return;

			timerTask.cancel();
			MessageSender.sendMessage(getPlayer(), Message.START_TIMER_MODE);
			timeTicks(endPosition, endPosState, getWorld().getGameTime());
		}, 0, 1);
	}

	private void timePulse(BlockVector3 pos, BlockState startState, long startTicks) {
		LongWrapper timeProgress = new LongWrapper(startTicks);

		pulseTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			long currentTime = System.currentTimeMillis();
			long difference = currentTime - timeProgress.lOng;
			if(difference > 5000L) {
				timeProgress.lOng = currentTime;
				MessageSender.sendMessage(getPlayer(), Message.PULSING_PROGRESS, getFormattedTicks(getWorld().getGameTime() - startTicks));
			}

			if(startState.equals(BlockGetter.blockStateFromBlockVector3(getWorld(), pos)))
				return;

			pulseTask.cancel();
			long totalTicks = getWorld().getGameTime() - startTicks;
			MessageSender.sendMessage(getPlayer(), Message.DELAY, getFormattedTicks(totalTicks));
		}, 0, 1);
		
	}

	public void pulse(BlockVector3 pos, String label) {
		Player player = getPlayer();

		if(isPulsing()) {
			MessageSender.sendMessage(player, Message.PULSE_WAITING);
			return;
		}

		MessageSender.sendMessage(player, Message.PULSE_WAITING);

		BlockState startState = BlockGetter.blockStateFromBlockVector3(getWorld(), pos);

		long startTime = System.currentTimeMillis();
		pulseTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			long currentTime = System.currentTimeMillis();
			if(currentTime - startTime > 10000L) {
				MessageSender.sendMessage(player, Message.PULSE_TIMEOUT);
				pulseTask.cancel();
				return;
			}

			BlockState newState = BlockGetter.blockStateFromBlockVector3(getWorld(), pos);
			if(startState.equals(newState))
				return;

			pulseTask.cancel();
			MessageSender.sendMessage(getPlayer(), Message.START_PULSE_MODE);
			timePulse(pos, newState, getWorld().getGameTime());
		}, 0, 1);
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
		terminateTimer(true);
		terminatePulse(true);
	}
}
