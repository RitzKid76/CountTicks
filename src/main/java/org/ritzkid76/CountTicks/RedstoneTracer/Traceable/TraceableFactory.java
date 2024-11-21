package org.ritzkid76.CountTicks.RedstoneTracer.Traceable;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.ritzkid76.CountTicks.RedstoneTracer.BlockGetter;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks.Comparator;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks.RedstoneTorch;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks.RedstoneWallTorch;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks.RedstoneWire;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks.Repeater;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks.SolidBlock;

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

	public static Traceable traceableFromBlockVector3(World world, BlockVector3 blockVector, BlockGetter getter) {
		BlockData blockData = getter.blockFromBlockVector3(world, blockVector);
		Material blockType = blockData.getMaterial();

		return createTraceable(blockType, blockData, blockVector, world, getter);
	}

	public static Traceable createTraceable(Material material, BlockData blockData, BlockVector3 position, World world, BlockGetter getter) {
		if(materialToTraceableClass.isEmpty())
			populateMaterials();

		Constructor<? extends Traceable> constructor = materialToTraceableClass.get(material);
		if(constructor == null)
			return attemptSolidBlockCreation(blockData, position, world, getter);

		try {
			return constructor.newInstance(blockData, position, world, getter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Constructor<? extends Traceable> getConstructor(Class<? extends Traceable> c) {
		try {
			return c.getConstructor(BlockData.class, BlockVector3.class, World.class, BlockGetter.class);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Constructor not found for " + c.getName(), e);
		}
	}

	private static Traceable attemptSolidBlockCreation(BlockData blockData, BlockVector3 position, World world, BlockGetter getter) {
		if(!BlockGetter.isSolidBlock(blockData))
			return null; // catch transparent blocks
		return new SolidBlock(blockData, position, world, getter);
	}
}
