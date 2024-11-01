package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.block.data.BlockData;
import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.Connection;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.ConnectionDirection;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.ConnectionSetFactory;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.PowerType;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Traceable;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlockData;

import java.util.Set;

public class Comparator extends Traceable {
    public static final Set<Connection> inputs = new ConnectionSetFactory()
        .add(ConnectionDirection.NORTH, PowerType.ANY)
        .add(ConnectionDirection.EAST, PowerType.SOFT)
        .add(ConnectionDirection.WEST, PowerType.SOFT)
    .get();
    public static final Set<Connection> outputs = new ConnectionSetFactory()
        .add(ConnectionDirection.NORTH, PowerType.HARD)
    .get();

    public Comparator(BlockData data, BlockVector3 position) {
        super(inputs, outputs, data, position);
    }

    @Override
    public TraceableBlockData applyBlockData(BlockData blockData) {
        org.bukkit.block.data.type.Comparator comparator = (org.bukkit.block.data.type.Comparator) blockData;

        return new TraceableBlockData(
            comparator.getFacing().getOppositeFace(),
            new GameTickDelay(2)
        );
    }
}
