package org.ritzkid76.CountTicks.RedstoneTracer.Traceable;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.ritzkid76.CountTicks.Exceptions.NonTraceableTypeException;
import org.ritzkid76.CountTicks.RedstoneTracer.BlockUtils;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks.SolidBlock;

import com.sk89q.worldedit.math.BlockVector3;

public class TraceableFactory {
    public static Traceable traceableFromBlockVector3(World world, BlockVector3 blockVector) {
        Block block = BlockUtils.blockFromBlockVector3(world, blockVector);
        BlockData blockData = block.getBlockData();
        Material blockType = block.getType();

        try {
            return TraceableFactory.createTraceable(blockType.toString(), blockData, blockVector, world);
        } catch(NonTraceableTypeException e) { return null; }
    }

    public static Traceable createTraceable(String blockName, BlockData blockData, BlockVector3 position, World world) {
        String className = classNameFromBlockName(blockName);

        try {
            Class<?> clazz = Class.forName(className);

            return (Traceable) clazz.getConstructor(
                BlockData.class,
                BlockVector3.class,
                World.class
            ).newInstance(
                blockData,
                position,
                world
            );
        } 
        catch (ClassNotFoundException e) { return attemptSolidBlockCreation(blockData, position, world); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    private static Traceable attemptSolidBlockCreation(BlockData blockData, BlockVector3 position, World world) {
        if(!BlockUtils.isSolidBlock(blockData)) throw new NonTraceableTypeException(); // catch transparent blocks
        return new SolidBlock(blockData, position, world);
    }

    private static String classNameFromBlockName(String blockName) {
        String fileName = getClassFileName(blockName);
        String packageName = Traceable.class.getPackage().getName();
        return packageName + ".TraceableBlocks." + fileName;
    }

    private static String getClassFileName(String blockName) {
        String[] segments = blockName.split("_");
        StringBuilder output = new StringBuilder();

        for (String segment : segments) {
            if(!segment.isEmpty()) {
                output.append(Character.toUpperCase(segment.charAt(0)));
                output.append(segment.substring(1).toLowerCase());
            }
        }

        return output.toString();
    }
}
