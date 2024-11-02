package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.block.data.BlockData;
import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.*;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Traceable;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlockData;

import java.util.Set;

public class RedstoneTorch extends Traceable {
    public static final Set<Connection> inputs = new ConnectionSetFactory()
        .add(ConnectionDirection.UP, PowerType.ANY)
    .get();
    public static final Set<Connection> outputs = new ConnectionSetFactory()
        .add(ConnectionDirection.CARDINAL, PowerType.SOFT, SignalStrength.MAX)
        .add(ConnectionDirection.UP, PowerType.HARD, SignalStrength.MAX)
    .get();

    public RedstoneTorch(BlockData data, BlockVector3 position) {
        super(inputs, outputs, data, position);
    }

    @Override
    public TraceableBlockData applyBlockData(BlockData blockData) {
        return new TraceableBlockData(new GameTickDelay(2));
    }
}