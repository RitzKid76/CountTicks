package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Traceable;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.Connection;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.ConnectionDirection;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.ConnectionSetFactory;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.PowerType;

import com.sk89q.worldedit.math.BlockVector3;

public class SolidBlock extends Traceable {
	public static final Set<Connection> inputs = new ConnectionSetFactory()
		.add(ConnectionDirection.AXIAL, PowerType.SOFT)
	.get();
	public static final Set<Connection> outputs = new ConnectionSetFactory()
		.add(ConnectionDirection.AXIAL, PowerType.INPUT_DEPENDENT)
	.get();

	public SolidBlock(BlockData data, BlockVector3 position, World world) {
		super(inputs, outputs, data, position, world);
	}
}
