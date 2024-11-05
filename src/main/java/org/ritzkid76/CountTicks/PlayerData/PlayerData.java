package org.ritzkid76.CountTicks.PlayerData;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.ritzkid76.CountTicks.Debug;
import org.ritzkid76.CountTicks.WorldEditSelection;
import org.ritzkid76.CountTicks.Exceptions.PositionOutOfRegionBounds;
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

	public boolean scan(BlockVector3 origin) {
		try {
			graph = new RedstoneTracerGraph(origin, playerRegion);
			if(!graph.trace()) {
				Debug.log("start position is not traceable");
				return true;
			}
		} catch (NullPointerException e) {
			Debug.log("no scan region defined");
			return true;
		} catch (PositionOutOfRegionBounds e) {
			Debug.log("start position outside of defined region");
			return true;
		}
		
		hasScanned = true;
		return true;
	}

	public RedstoneTracerGraphPath getFastestPath(BlockVector3 pos) { return graph.fastestPath(pos); }

	public boolean hasScanned() { return hasScanned; }
}
