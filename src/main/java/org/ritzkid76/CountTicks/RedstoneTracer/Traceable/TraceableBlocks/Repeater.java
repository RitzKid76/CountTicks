package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.block.data.BlockData;
import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.Connection;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.ConnectionDirection;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.PowerType;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Traceable;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlockData;

import java.util.Set;

public class Repeater extends Traceable {
    public static final Set<Connection> inputs = Set.of(
        new Connection(ConnectionDirection.NORTH, PowerType.ANY)
    );
    public static final Set<Connection> outputs = Set.of(
        new Connection(ConnectionDirection.NORTH, PowerType.HARD, new GameTickDelay(2))
    );

    public Repeater(BlockData data, BlockVector3 position) {
        super(inputs, outputs, data, position);
    }

    @Override
    public TraceableBlockData applyBlockData(BlockData blockData) {
        org.bukkit.block.data.type.Repeater repeater = (org.bukkit.block.data.type.Repeater) blockData;

        return new TraceableBlockData(
            repeater.getFacing().getOppositeFace(),
            new GameTickDelay(repeater.getDelay() * 2)
        );
    }
}
