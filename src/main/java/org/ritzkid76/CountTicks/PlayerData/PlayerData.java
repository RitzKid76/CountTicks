package org.ritzkid76.CountTicks.PlayerData;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.ritzkid76.CountTicks.Commands.Command;
import org.ritzkid76.CountTicks.Commands.ThreadCommand;
import org.ritzkid76.CountTicks.Message.Message;
import org.ritzkid76.CountTicks.Message.MessageSender;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraph;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

public class PlayerData {
	private UUID uuid;
	private Plugin plugin;

	private CuboidRegion playerRegion;
	private RedstoneTracerGraph graph;

	private ThreadCommand threadCommand;

	public PlayerData(UUID u, Plugin p) {
		uuid = u;
		plugin = p;
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

	public Plugin getPlugin() {
		return plugin;
	}

	public RedstoneTracerGraph getGraph() {
		return graph;
	}
	public void setGraph(RedstoneTracerGraph g) {
		graph = g;
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

	public boolean isExecuting() {
		if(threadCommand != null)
			return threadCommand.isRunning();
		return false;
	}

	public Message currentlyExecuting() {
		return threadCommand.currentlyExecuting();
	}

	public boolean hasScanned() {
		if(graph == null)
			return false;
		return graph.totalScanned() > 0;
	}

	public void shutdown() {
		clearThreadCommand(true);
	}

	public void clearThreadCommand(boolean silent) {
		if(threadCommand != null) {
			threadCommand.terminate(silent);
			threadCommand = null;
		}
	}

	private boolean sameParentCommand(ThreadCommand newThread) {
		return threadCommand.getClass() == newThread.getClass();
	}

	private boolean shouldLink(ThreadCommand newThread) {
		return
			sameParentCommand(newThread) &&
			newThread.shouldLinkToCurrentThread();
	}

	public void runCommand(Command command) {
		if(command instanceof ThreadCommand newThreadCommand) {
			if(threadCommand != null) {
				if(shouldLink(newThreadCommand)) {
					threadCommand.link(newThreadCommand.getArgs());
					return;
				}
				
				if(threadCommand.isAlreadyExecuting(newThreadCommand)) {
					MessageSender.sendMessage(getPlayer(), threadCommand.alreadyExecuting());
					return;
				}
				
				if(!sameParentCommand(newThreadCommand))
					threadCommand.override();
			}
			
			threadCommand = newThreadCommand;
		}

		command.execute();
	}
}
