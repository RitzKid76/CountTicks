package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.*;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Traceable;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlockData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.ConnectionSetFactory.createConnectionSet;

public class RedstoneWire extends Traceable {
    public static final Set<Connection> inputs = createConnectionSet(PowerType.SOFT, ConnectionDirection.ALL);

    public static final Set<Connection> outputs = new ConnectionSetFactory()
        .add(createConnectionSet(PowerType.SOFT, ConnectionDirection.UPWARD_DIAGONAL))
        .add(createConnectionSet(PowerType.SOFT, ConnectionDirection.CARDINAL))
        .add(createConnectionSet(PowerType.SOFT, ConnectionDirection.DOWNWARD))
    .get();

    private Map<BlockFace, org.bukkit.block.data.type.RedstoneWire.Connection> data;

    public RedstoneWire(BlockData data, BlockVector3 position) { super(inputs, outputs, data, position); }

    @Override
    public TraceableBlockData applyBlockData(BlockData blockData) {
        org.bukkit.block.data.type.RedstoneWire redstoneWire = (org.bukkit.block.data.type.RedstoneWire) blockData;
        data = new HashMap<>();

        data.put(BlockFace.NORTH, redstoneWire.getFace(BlockFace.NORTH));
        data.put(BlockFace.EAST, redstoneWire.getFace(BlockFace.EAST));
        data.put(BlockFace.SOUTH, redstoneWire.getFace(BlockFace.SOUTH));
        data.put(BlockFace.WEST, redstoneWire.getFace(BlockFace.WEST));

        return new TraceableBlockData();
    }

    // TODO filter invalid connections through blocks on diagonals

    @Override
    public boolean filterConnection(Connection connection, ConnectionType type) {
        if(type == ConnectionType.OUTPUTS) {
            org.bukkit.block.data.type.RedstoneWire.Connection blockDataConnection = data.get(connection.toBlockFace());

            if(blockDataConnection == null) return false; // keep downward connection

            switch(blockDataConnection) {
                case UP, SIDE-> { return false; }
                default -> { return true; }
            }
        }

        return false; // dont need to filter inputs since the redstone shape isnt always correlated
    }
}
