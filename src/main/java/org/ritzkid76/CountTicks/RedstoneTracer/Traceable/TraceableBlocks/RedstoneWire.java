package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlocks;

import com.sk89q.worldedit.math.BlockVector3;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.ritzkid76.CountTicks.RedstoneTracer.BlockGetter;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.*;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Traceable;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableBlockData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RedstoneWire extends Traceable {
	public static final Set<Connection> inputs = new ConnectionSetFactory()
		.add(ConnectionDirection.ALL, PowerType.SOFT)
	.get();

	public static final Set<Connection> outputs = new ConnectionSetFactory()
		.add(ConnectionDirection.UPWARD_DIAGONAL, PowerType.SOFT)
		.add(ConnectionDirection.CARDINAL, PowerType.SOFT)
		.add(ConnectionDirection.DOWNWARD, PowerType.SOFT)
	.get();

	private Map<BlockFace, org.bukkit.block.data.type.RedstoneWire.Connection> data;

	public RedstoneWire(BlockData data, BlockVector3 position, World world, BlockGetter getter) {
		super(inputs, outputs, data, position, world, getter);
	}

	@Override
	public Material getMaterial() {
		return Material.REDSTONE_WIRE;
	}

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

	@Override
	public boolean filterOutputConnection(Connection connection) {
		org.bukkit.block.data.type.RedstoneWire.Connection blockDataConnection = data.get(connection.toBlockFace());

		if(blockDataConnection == null)
			return false; // dont bother checking downwards connection. checking null since data.get(DOWN) is always null;

		return
			noConnection(blockDataConnection) ||
			diagonalBlocked(connection.connectionDirection) ||
			diagonalDiode(connection.connectionDirection);
	}

	private static boolean noConnection(org.bukkit.block.data.type.RedstoneWire.Connection connection) {
		return switch(connection) {
			case UP, SIDE-> false;
			default -> true;
		};
	}

	private boolean diagonalBlocked(ConnectionDirection direction) {
		ConnectionDirection testDirection;

		switch(direction) {
			case ConnectionDirection c when ConnectionDirection.UPWARD_DIAGONAL.contains(c) -> testDirection = ConnectionDirection.UP;
			case ConnectionDirection c when ConnectionDirection.DOWNWARD_DIAGONAL.contains(c) -> testDirection = ConnectionDirection.toCardinalDirection(direction);
			default -> { return false; }
		}

		BlockVector3 testPosition = ConnectionDirection.destinationFromConnectionDirection(getPosition(), testDirection);

		return getter.isSolidBlock(testPosition);
	}

	private boolean diagonalDiode(ConnectionDirection direction) {
		if(!ConnectionDirection.DOWNWARD_DIAGONAL.contains(direction))
			return false;

		BlockVector3 supportBlock = getPosition().add(BlockVector3.UNIT_MINUS_Y);

		return !getter.isSolidBlock(supportBlock);
	}
}
