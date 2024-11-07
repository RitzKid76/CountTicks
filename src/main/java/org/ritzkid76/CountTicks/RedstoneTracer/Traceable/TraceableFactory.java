package org.ritzkid76.CountTicks.RedstoneTracer.Traceable;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.ritzkid76.CountTicks.RedstoneTracer.BlockUtils;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks.*;

import com.sk89q.worldedit.math.BlockVector3;

public class TraceableFactory {
	private static final Map<Material, Constructor<? extends Traceable>> materialToTraceableClass = new HashMap<>();
	private static void populateMaterials() {
		materialToTraceableClass.put(Material.COMPARATOR, getConstructor(Comparator.class));
		materialToTraceableClass.put(Material.REDSTONE_TORCH, getConstructor(RedstoneTorch.class));
		materialToTraceableClass.put(Material.REDSTONE_WALL_TORCH, getConstructor(RedstoneWallTorch.class));
		materialToTraceableClass.put(Material.REDSTONE_WIRE, getConstructor(RedstoneWire.class));
		materialToTraceableClass.put(Material.REPEATER, getConstructor(Repeater.class));
	}

	public static Traceable traceableFromBlockVector3(World world, BlockVector3 blockVector) {
		Block block = BlockUtils.blockFromBlockVector3(world, blockVector);
		BlockData blockData = block.getBlockData();
		Material blockType = block.getType();

		return createTraceable(blockType, blockData, blockVector, world);
	}

	public static Traceable createTraceable(Material material, BlockData blockData, BlockVector3 position, World world) {
		if(materialToTraceableClass.isEmpty())
			populateMaterials();

		Constructor<? extends Traceable> constructor = materialToTraceableClass.get(material);
		if(constructor == null)
			return attemptSolidBlockCreation(blockData, position, world);

		try {
			return (Traceable) constructor.newInstance(blockData, position, world);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Constructor<? extends Traceable> getConstructor(Class<? extends Traceable> c) {
		try {
			return c.getConstructor(BlockData.class, BlockVector3.class, World.class);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Constructor not found for " + c.getName(), e);
		}
	}

	private static Traceable attemptSolidBlockCreation(BlockData blockData, BlockVector3 position, World world) {
		if(!BlockUtils.isSolidBlock(blockData)) return null; // catch transparent blocks
		return new SolidBlock(blockData, position, world);
	}
}
