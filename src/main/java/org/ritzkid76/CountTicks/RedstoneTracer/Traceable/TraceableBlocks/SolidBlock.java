package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.ritzkid76.CountTicks.RedstoneTracer.BlockGetter;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Traceable;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlockData;
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

	private Material material;

	public SolidBlock(BlockData data, BlockVector3 position, World world, BlockGetter getter) {
		super(inputs, outputs, data, position, world, getter);
	}

	@Override
	public TraceableBlockData applyBlockData(BlockData blockData) {
		material = blockData.getMaterial();
		return new TraceableBlockData();
	}

	@Override
	public Material getMaterial() {
		return material;
	}

	private boolean isTorchConnection(Material material) {
		return (
			material == Material.REDSTONE_TORCH ||
			material == Material.REDSTONE_WALL_TORCH
		);
	}

	@Override
	protected boolean filterInputConnection(Connection connection) {
		ConnectionDirection direction = connection.connectionDirection;

		BlockVector3 sourcePos = ConnectionDirection.sourceFromConnectionDirection(getPosition(), direction);
		Material sourceMaterial = getter.materialFromBlockVector3(sourcePos);

		if(BlockGetter.isSolidBlock(sourceMaterial))
			return true; // solid blocks cant power other solid blocks

		if(!isTorchConnection(sourceMaterial))
			return false; // any connection other than a torch connection should operate as normal

		if(
			direction == ConnectionDirection.DOWN ||
			ConnectionDirection.CARDINAL.contains(direction)
		)
			return true;

		return false;
	}
}
