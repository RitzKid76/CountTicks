package org.ritzkid76.CountTicks.RedstoneTracer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector3;

public class BlockUtils {
	public static Block blockFromBlockVector3(World world, BlockVector3 blockVector) {
		Location location = new Location(world, blockVector.getX(), blockVector.getY(), blockVector.getZ());
		return location.getBlock();
	}

	public static boolean isSolidBlock(Material material) {
		return material.isOccluding();
	}
	public static boolean isSolidBlock(BlockData data) {
		return data.isOccluding();
	}
	public static boolean isSolidBlock(World world, BlockVector3 position) {
		Block block = blockFromBlockVector3(world, position);
		return isSolidBlock(block.getBlockData());
	}

	public static BlockVector3 getBlockLookingAt(Player player, int range) {
		Block targetBlock = player.getTargetBlockExact(range);

		if(
			targetBlock == null ||
			targetBlock.getType() == Material.AIR
		) return null;

		return BlockVector3.at(
			targetBlock.getX(),
			targetBlock.getY(),
			targetBlock.getZ()
		);
	}
}
