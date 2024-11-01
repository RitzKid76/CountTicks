package org.ritzkid76.CountTicks.RedstoneTracer.Traceable;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.ritzkid76.CountTicks.Debug;
import org.ritzkid76.CountTicks.RedstoneTracer.RedstoneTracer;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks.SolidBlock;

public class TraceableFactory {

    private static Block blockFromBlockVector3(World world, BlockVector3 blockVector) {
        Location location = new Location(world, blockVector.x(), blockVector.y(), blockVector.z());
        return location.getBlock();
    }
    public static Traceable traceableFromBlockVector3(BlockVector3 blockVector) {
        Block block = blockFromBlockVector3(RedstoneTracer.getTracerWorld(), blockVector);
        BlockData blockData = block.getBlockData();
        Material blockType = block.getType();

        return TraceableFactory.createTraceable(blockType.toString(), blockData, blockVector);
    }

    public static Traceable createTraceable(String blockName, BlockData blockData, BlockVector3 position) {
        String className = classNameFromBlockName(blockName);

        try {
            Class<?> clazz = Class.forName(className);
            if(!Traceable.class.isAssignableFrom(clazz)) throw new IllegalArgumentException(className + " is not a valid extension of Traceable");

            Debug.log(blockName);

            return (Traceable) clazz.getConstructor(
                BlockData.class,
                BlockVector3.class
            ).newInstance(
                blockData,
                position
            );
        } catch (Exception e) { return attemptSolidBlockCreation(blockData, position); }
    }

    private static Traceable attemptSolidBlockCreation(BlockData blockData, BlockVector3 position) {
        if(!blockData.isOccluding()) throw new RuntimeException(); // catch transparent blocks
        return new SolidBlock(blockData, position);
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
