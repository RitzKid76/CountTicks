package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ConnectionSetFactory {
	private final Set<Connection> connections = new HashSet<>();

	public ConnectionSetFactory add(EnumSet<ConnectionDirection> directions, PowerType type) {
		for(ConnectionDirection direction : directions) {
			connections.add(new Connection(direction, type));
		}
		return this;
	}

	public ConnectionSetFactory add(Set<Connection> direction) {
		connections.addAll(direction);
		return this;
	}

	public ConnectionSetFactory add(ConnectionDirection direction, PowerType type) {
		this.connections.add(new Connection(direction, type));
		return this;
	}

	public Set<Connection> get() {
		return connections;
	}
}
