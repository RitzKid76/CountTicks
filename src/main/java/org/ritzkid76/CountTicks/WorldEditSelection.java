package org.ritzkid76.CountTicks;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.session.SessionManager;

public class WorldEditSelection {
	private com.sk89q.worldedit.world.World weWorld;
	private com.sk89q.worldedit.entity.Player wePlayer;
	private LocalSession localSession;

	public WorldEditSelection(Player player) {
		weWorld = BukkitAdapter.adapt(player.getWorld());
		wePlayer = BukkitAdapter.adapt(player);

		localSession = getLocalSession();
	}

	private int getOppositeCoordinate(int min, int max, int pos1Coord) { return (min == pos1Coord)? max : min; }

	private LocalSession getLocalSession() {
		SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
		return sessionManager.get(wePlayer);
	}

	public Region getRegion() { 
		try {
			return localSession.getSelection(weWorld); 
		} catch (Exception e) {}
		return null;
	}

	public RegionSelector getRegionSelector() {
		try {
			return localSession.getRegionSelector(weWorld);
		} catch (Exception e) {}
		return null;
	}

	public BlockVector3[] getSelection() {
		return new BlockVector3[] {getFirstPosition(), getSecondPosition()};
	}

	public BlockVector3 getFirstPosition() {
		RegionSelector selector = getRegionSelector();
		try {
			return selector.getPrimaryPosition();
		} catch (IncompleteRegionException e) {}

		return null;
	}

	public BlockVector3 getSecondPosition() {
		BlockVector3 pos1 = getFirstPosition();
		if(pos1 == null)
			return null;

		Region selection = getRegion();
		if(selection == null)
			return null;

		BlockVector3 min = selection.getMinimumPoint();
		BlockVector3 max = selection.getMaximumPoint();

		return BlockVector3.at(
			getOppositeCoordinate(min.x(), max.x(), pos1.x()),
			getOppositeCoordinate(min.y(), max.y(), pos1.y()),
			getOppositeCoordinate(min.z(), max.z(), pos1.z())
		);
	}
}
