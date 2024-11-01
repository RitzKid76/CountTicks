package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.block.data.BlockData;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.*;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Traceable;

import java.util.Set;

import static org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.ConnectionSetFactory.createConnectionSet;

public class Target extends Traceable {
    public static final Set<Connection> inputs = createConnectionSet(PowerType.SOFT, ConnectionDirection.AXIAL);
    public static final Set<Connection> outputs = createConnectionSet(PowerType.INPUT_DEPENDENT, ConnectionDirection.AXIAL);

    public Target(BlockData data, BlockVector3 position) {
        super(inputs, outputs, data, position);
    }
}
