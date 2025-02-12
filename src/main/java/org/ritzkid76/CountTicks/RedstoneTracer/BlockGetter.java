package org.ritzkid76.CountTicks.RedstoneTracer;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector3;

public class BlockGetter {
	private Map<BlockVector3, BlockData> cache;
	private World world;

	public BlockGetter(World wld) {
		cache = new HashMap<>();
		world = wld;
	}

	public BlockData blockFromBlockVector3(BlockVector3 blockVector) {
		BlockData cached = cache.get(blockVector);
		if(cached != null)
			return cached;

		BlockData newBlock = world.getBlockAt(blockVector.x(), blockVector.y(), blockVector.z()).getBlockData();
		cache.put(blockVector, newBlock);

		return newBlock;
	}

	public boolean isSolidBlock(BlockVector3 position) {
		BlockData block = blockFromBlockVector3(position);
		return isSolidBlock(block);
	}

	public Material materialFromBlockVector3(BlockVector3 pos) {
		return blockFromBlockVector3(pos).getMaterial();
	}

	public static BlockState blockStateFromBlockVector3(World world, BlockVector3 pos) {
		return blockFromBlockVector3Uncached(world, pos).createBlockState();
	}

	public static boolean isSolidBlock(Material material) {
		return material.isOccluding();
	}
	public static boolean isSolidBlock(BlockData data) {
		return data.isOccluding();
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

	public static BlockData blockFromBlockVector3Uncached(World world, BlockVector3 blockVector) {
		return world.getBlockAt(blockVector.x(), blockVector.y(), blockVector.z()).getBlockData();
	}
}
