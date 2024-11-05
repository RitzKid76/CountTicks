package org.ritzkid76.CountTicks.RedstoneTracer;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BlockUtils {
	public static Block blockFromBlockVector3(World world, BlockVector3 blockVector) {
		Location location = new Location(world, blockVector.x(), blockVector.y(), blockVector.z());
		return location.getBlock();
	}

	public static boolean isSolidBlock(BlockData data) { return data.isOccluding(); }
	public static boolean isSolidBlock(World world, BlockVector3 position) {
		Block block = blockFromBlockVector3(world, position);
		return isSolidBlock(block.getBlockData());
	}
}
