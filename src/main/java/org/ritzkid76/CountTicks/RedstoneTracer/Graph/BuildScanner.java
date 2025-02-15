package org.ritzkid76.CountTicks.RedstoneTracer.Graph;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.ritzkid76.CountTicks.Commands.ThreadCommand;
import org.ritzkid76.CountTicks.Exceptions.BoundsUndefinedException;
import org.ritzkid76.CountTicks.Exceptions.PositionOutOfRegionBoundsException;
import org.ritzkid76.CountTicks.Exceptions.ThreadCanceledException;
import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.PlayerData.PlayerData;

import com.sk89q.worldedit.math.BlockVector3;

public class BuildScanner {
	private final Player player;
	private final PlayerData playerData;
	private final Plugin plugin;

	private BukkitTask task;
	private final long startTime;
	private final Consumer<Boolean> returnTo;
	
	public BuildScanner(PlayerData playerData, Consumer<Boolean> returnTo) {
		this.playerData = playerData;
		this.player = playerData.getPlayer();
		this.plugin = playerData.getPlugin();

		this.startTime = System.currentTimeMillis();
		this.returnTo = returnTo;
	}

	public boolean trySetOrigin(BlockVector3 origin, String label) {
		try {
			playerData.setGraph(new RedstoneTracerGraph(origin, playerData.getRegion()));
		} catch (PositionOutOfRegionBoundsException e) {
			MessageSender.sendMessage(player, Message.START_OUT_OF_BOUNDS);
			return false;
		} catch (BoundsUndefinedException e) {
			MessageSender.sendMessage(player, Message.NO_SCAN_REGION, label);
			return false;
		}

		return true;
	} 

	public BukkitTask scan() {
		task = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try {
				scanCallback(
					playerData.getGraph().trace(task, startTime, player),
					returnTo,
					startTime
				);
			} catch (ThreadCanceledException e) { }
		});
		return task;
	}

	private void scanCallback(boolean success, Consumer<Boolean> returnTo, long startTime) {
		if(!success) {
			MessageSender.sendMessage(player, Message.INVALID_START);
			
			if(returnTo != null)
				returnTo.accept(false);
			return;
		}

		MessageSender.sendMessage(
			player,
			Message.SCAN_COMPLETE,
			ThreadCommand.getFormattedTimer(System.currentTimeMillis() - startTime),
			String.valueOf(playerData.getGraph().totalScanned())
		);

		if(returnTo != null)
			returnTo.accept(true);
	}
}
