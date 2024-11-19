package org.ritzkid76.CountTicks.PlayerData;

import java.util.UUID;

import org.bukkit.Bukkit;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.session.SessionManager;

public class WorldEditSelection {
	private static int getOppositeCoordinate(int min, int max, int pos1Coord) {
		return (min == pos1Coord)? max : min;
	}

	private static LocalSession getLocalSession(Player player) {
		SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
		return sessionManager.get(player);
	}

	private static Player getPlayer(UUID uuid) {
		return BukkitAdapter.adapt(Bukkit.getPlayer(uuid));
	}

	public static Region getRegion(UUID uuid) {
		Player player = getPlayer(uuid);

		try {
			return getLocalSession(player).getSelection();
		} catch (IncompleteRegionException e) {
			return null;
		}
	}

	public static RegionSelector getRegionSelector(UUID uuid) {
		Player player = getPlayer(uuid);
		return getLocalSession(player).getRegionSelector(player.getWorld());
	}

	public static BlockVector3 getFirstPosition(UUID uuid) {
		RegionSelector selector = getRegionSelector(uuid);
		try {
			return selector.getPrimaryPosition();
		} catch (IncompleteRegionException e) {
			return null;
		}
	}

	public static BlockVector3 getSecondPosition(UUID uuid) {
		BlockVector3 pos1 = getFirstPosition(uuid);
		if(pos1 == null)
			return null;

		Region selection = getRegion(uuid);
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
