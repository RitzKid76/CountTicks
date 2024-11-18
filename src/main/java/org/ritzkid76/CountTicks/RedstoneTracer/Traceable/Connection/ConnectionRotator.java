package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.block.BlockFace;
import org.ritzkid76.CountTicks.Exceptions.OddNumberOfInputsException;

public class ConnectionRotator {
	// NORTH is not required here since there's no reason to map it to the default.
	private static final Map<ConnectionDirection, ConnectionDirection> EAST = createMapWithInfiniteEntries(
		ConnectionDirection.NORTH, ConnectionDirection.EAST,
		ConnectionDirection.EAST, ConnectionDirection.SOUTH,
		ConnectionDirection.SOUTH, ConnectionDirection.WEST,
		ConnectionDirection.WEST, ConnectionDirection.NORTH,
		ConnectionDirection.NORTH_UP, ConnectionDirection.EAST_UP,
		ConnectionDirection.EAST_UP, ConnectionDirection.SOUTH_UP,
		ConnectionDirection.SOUTH_UP, ConnectionDirection.WEST_UP,
		ConnectionDirection.WEST_UP, ConnectionDirection.NORTH_UP,
		ConnectionDirection.NORTH_DOWN, ConnectionDirection.EAST_DOWN,
		ConnectionDirection.EAST_DOWN, ConnectionDirection.SOUTH_DOWN,
		ConnectionDirection.SOUTH_DOWN, ConnectionDirection.WEST_DOWN,
		ConnectionDirection.WEST_DOWN, ConnectionDirection.NORTH_DOWN
	);
	private static final Map<ConnectionDirection, ConnectionDirection> SOUTH = createMapWithInfiniteEntries(
		ConnectionDirection.NORTH, ConnectionDirection.SOUTH,
		ConnectionDirection.EAST, ConnectionDirection.WEST,
		ConnectionDirection.SOUTH, ConnectionDirection.NORTH,
		ConnectionDirection.WEST, ConnectionDirection.EAST,
		ConnectionDirection.NORTH_UP, ConnectionDirection.SOUTH_UP,
		ConnectionDirection.EAST_UP, ConnectionDirection.WEST_UP,
		ConnectionDirection.SOUTH_UP, ConnectionDirection.NORTH_UP,
		ConnectionDirection.WEST_UP, ConnectionDirection.EAST_UP,
		ConnectionDirection.NORTH_DOWN, ConnectionDirection.SOUTH_DOWN,
		ConnectionDirection.EAST_DOWN, ConnectionDirection.WEST_DOWN,
		ConnectionDirection.SOUTH_DOWN, ConnectionDirection.NORTH_DOWN,
		ConnectionDirection.WEST_DOWN, ConnectionDirection.EAST_DOWN
	);
	private static final Map<ConnectionDirection, ConnectionDirection> WEST = createMapWithInfiniteEntries(
		ConnectionDirection.NORTH, ConnectionDirection.WEST,
		ConnectionDirection.EAST, ConnectionDirection.NORTH,
		ConnectionDirection.SOUTH, ConnectionDirection.EAST,
		ConnectionDirection.WEST, ConnectionDirection.SOUTH,
		ConnectionDirection.NORTH_UP, ConnectionDirection.WEST_UP,
		ConnectionDirection.EAST_UP, ConnectionDirection.NORTH_UP,
		ConnectionDirection.SOUTH_UP, ConnectionDirection.EAST_UP,
		ConnectionDirection.WEST_UP, ConnectionDirection.SOUTH_UP,
		ConnectionDirection.NORTH_DOWN, ConnectionDirection.WEST_DOWN,
		ConnectionDirection.EAST_DOWN, ConnectionDirection.NORTH_DOWN,
		ConnectionDirection.SOUTH_DOWN, ConnectionDirection.EAST_DOWN,
		ConnectionDirection.WEST_DOWN, ConnectionDirection.SOUTH_DOWN
	);


	private static Map<ConnectionDirection, ConnectionDirection> createMapWithInfiniteEntries(ConnectionDirection... entries) {
		if(entries.length % 2 == 1)
			throw new OddNumberOfInputsException();

		Map<ConnectionDirection, ConnectionDirection> map = new HashMap<>();

		for(int i = 0; i < entries.length; i +=2 ) {
			ConnectionDirection k = entries[i];
			ConnectionDirection v = entries[i + 1];
			map.put(k, v);
		}

		return map;
	}

	public static Connection rotateConnection(Connection connection, BlockFace direction) {
		ConnectionDirection rotatedType = connection.connectionDirection;

		switch(direction) {
			case EAST -> rotatedType = EAST.get(rotatedType);
			case SOUTH -> rotatedType = SOUTH.get(rotatedType);
			case WEST -> rotatedType = WEST.get(rotatedType);
			default -> {}
		}

		if(rotatedType == null)
			return connection;
		return connection.updateDirection(rotatedType);
	}

	public static Set<Connection> rotateAllConnections(Set<Connection> connections, BlockFace direction) {
		Set<Connection> rotatedConnections = new HashSet<>();

		for(Connection connection: connections) {
			rotatedConnections.add(rotateConnection(connection, direction));
		}

		return rotatedConnections;
	}
}
