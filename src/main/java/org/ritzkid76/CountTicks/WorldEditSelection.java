package org.ritzkid76.CountTicks;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;

public class WorldEditSelection {
    private com.sk89q.worldedit.world.World weWorld;
    private com.sk89q.worldedit.entity.Player wePlayer;

    public WorldEditSelection(World world, Player player) {
        weWorld = BukkitAdapter.adapt(world);
        wePlayer = BukkitAdapter.adapt(player);
    }

    private int getOppositeCoordinate(int min, int max, int pos1Coord) { return (min == pos1Coord)? max : min; }

    public BlockVector3[] getSelection() throws IncompleteRegionException {
        SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = sessionManager.get(wePlayer);

        BlockVector3 pos1 = localSession.getRegionSelector(weWorld).getPrimaryPosition();

        // have to extract the second position since i am either mega beans retard, or sk89q is mega sadge ape
        Region selection = localSession.getSelection(weWorld);
        BlockVector3 min = selection.getMinimumPoint();
        BlockVector3 max = selection.getMaximumPoint();

        BlockVector3 pos2 = BlockVector3.at(
                getOppositeCoordinate(min.x(), max.x(), pos1.x()),
                getOppositeCoordinate(min.y(), max.y(), pos1.y()),
                getOppositeCoordinate(min.z(), max.z(), pos1.z())
        );

        return new BlockVector3[] {pos1, pos2};
    }
}
