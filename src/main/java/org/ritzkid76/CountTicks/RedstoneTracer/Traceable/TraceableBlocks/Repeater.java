package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks;

import com.sk89q.worldedit.math.BlockVector3;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.Connection;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.ConnectionDirection;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.ConnectionSetFactory;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.PowerType;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Traceable;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlockData;

import java.util.Set;

public class Repeater extends Traceable {
	public static final Set<Connection> inputs = new ConnectionSetFactory()
		.add(ConnectionDirection.NORTH, PowerType.ANY)
	.get();
	public static final Set<Connection> outputs = new ConnectionSetFactory()
		.add(ConnectionDirection.NORTH, PowerType.HARD)
	.get();

	public Repeater(BlockData data, BlockVector3 position, World world) {
		super(inputs, outputs, data, position, world);
	}

	@Override
	public Material getMaterial() {
		return Material.REPEATER;
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
