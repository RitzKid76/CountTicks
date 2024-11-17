package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection;

import java.util.EnumSet;

import org.bukkit.block.BlockFace;

import com.sk89q.worldedit.math.BlockVector3;

public enum ConnectionDirection {
	UP, DOWN, NORTH, EAST, SOUTH, WEST,
	NORTH_UP, EAST_UP, SOUTH_UP, WEST_UP,
	NORTH_DOWN, EAST_DOWN, SOUTH_DOWN, WEST_DOWN;

	public static final EnumSet<ConnectionDirection> ALL = EnumSet.of(UP, DOWN, NORTH, EAST, SOUTH, WEST, NORTH_UP, EAST_UP, SOUTH_UP, WEST_UP, NORTH_DOWN, EAST_DOWN, SOUTH_DOWN, WEST_DOWN);

	public static final EnumSet<ConnectionDirection> AXIAL = EnumSet.of(UP, DOWN, NORTH, EAST, SOUTH, WEST);
	public static final EnumSet<ConnectionDirection> CARDINAL = EnumSet.of(NORTH, EAST, SOUTH, WEST);
	public static final EnumSet<ConnectionDirection> VERTICAL = EnumSet.of(UP, DOWN);

	public static final EnumSet<ConnectionDirection> UPWARD_DIAGONAL = EnumSet.of(NORTH_UP, EAST_UP, SOUTH_UP, WEST_UP);
	public static final EnumSet<ConnectionDirection> DOWNWARD_DIAGONAL = EnumSet.of(NORTH_DOWN, EAST_DOWN, SOUTH_DOWN, WEST_DOWN);

	public static final EnumSet<ConnectionDirection> UPWARD = EnumSet.of(UP, NORTH_UP, EAST_UP, SOUTH_UP, WEST_UP);
	public static final EnumSet<ConnectionDirection> DOWNWARD = EnumSet.of(DOWN, NORTH_DOWN, EAST_DOWN, SOUTH_DOWN, WEST_DOWN);
	public static final EnumSet<ConnectionDirection> NORTHERN = EnumSet.of(NORTH, NORTH_UP, NORTH_DOWN);
	public static final EnumSet<ConnectionDirection> EASTERN = EnumSet.of(EAST, EAST_UP, EAST_DOWN);
	public static final EnumSet<ConnectionDirection> SOUTHERN = EnumSet.of(SOUTH, SOUTH_UP, SOUTH_DOWN);
	public static final EnumSet<ConnectionDirection> WESTERN = EnumSet.of(WEST, WEST_UP, WEST_DOWN);

	public static BlockFace toBlockFace(ConnectionDirection direction) {
		if(NORTHERN.contains(direction)) {
			return BlockFace.NORTH;
		} else if(EASTERN.contains(direction)) {
			return BlockFace.EAST;
		} else if(SOUTHERN.contains(direction)) {
			return BlockFace.SOUTH;
		} else if(WESTERN.contains(direction)) {
			return BlockFace.WEST;
		} else if(UPWARD.contains(direction)) {
			return BlockFace.UP;
		} else if(DOWNWARD.contains(direction)) {
			return BlockFace.DOWN;
		}
		throw new IllegalArgumentException();
	}

	public static BlockVector3 positionFromConnectionDirection(BlockVector3 origin, ConnectionDirection direction) {
		BlockVector3 offset = BlockVector3.ZERO;
	
		if(UPWARD.contains(direction)) {
			offset = offset.add(BlockVector3.UNIT_Y);
		} else if(DOWNWARD.contains(direction)) {
			offset = offset.add(BlockVector3.UNIT_MINUS_Y);
		}
	
		if(NORTHERN.contains(direction)) {
			offset = offset.add(BlockVector3.UNIT_MINUS_Z);
		} else if(EASTERN.contains(direction)) {
			offset = offset.add(BlockVector3.UNIT_X);
		} else if(SOUTHERN.contains(direction)) {
			offset = offset.add(BlockVector3.UNIT_Z);
		} else if(WESTERN.contains(direction)) {
			offset = offset.add(BlockVector3.UNIT_MINUS_X);
		}
	
		return offset.add(origin);
	}
	
	public static ConnectionDirection toCardinalDirection(ConnectionDirection direction) {
		if(NORTHERN.contains(direction)) {
			return NORTH;
		} else if(EASTERN.contains(direction)) {
			return EAST;
		} else if(SOUTHERN.contains(direction)) {
			return SOUTH;
		} else if(WESTERN.contains(direction)) {
			return WEST;
		} else {
			return null;
		}
	}
}