package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks;

import static org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.ConnectionSetFactory.createConnectionSet;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Traceable;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.Connection;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.ConnectionDirection;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.PowerType;

import com.sk89q.worldedit.math.BlockVector3;

public class SolidBlock extends Traceable {
    public static final Set<Connection> inputs = createConnectionSet(PowerType.SOFT, ConnectionDirection.AXIAL);
    public static final Set<Connection> outputs = createConnectionSet(PowerType.INPUT_DEPENDENT, ConnectionDirection.AXIAL);

    public SolidBlock(BlockData data, BlockVector3 position, World world) {
        super(inputs, outputs, data, position, world);
    }
}
