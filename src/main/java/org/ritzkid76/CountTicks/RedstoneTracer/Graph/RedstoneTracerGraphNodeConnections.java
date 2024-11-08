package org.ritzkid76.CountTicks.RedstoneTracer.Graph;

import com.sk89q.worldedit.math.BlockVector3;

import java.util.LinkedHashSet;

public class RedstoneTracerGraphNodeConnections {
	public Type type;
	public LinkedHashSet<BlockVector3> connections;

	public enum Type {
		NONE(0),
		SINGLE(1),
		MULTIPLE(2);

		private final int value;

		Type(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public Type add(Type other) {
			int sum = this.getValue() + other.getValue();

			switch(sum) {
				case 0 -> { return NONE; }
				case 1 -> { return SINGLE; }
				default -> { return MULTIPLE; }
			}
		}

		public Type typeFromConnectionCount(int connections) {
			switch(connections) {
				case 0 -> { return Type.NONE; }
				case 1 -> { return Type.SINGLE; }
				default -> { return Type.MULTIPLE; }
			}
		}
	}

	public RedstoneTracerGraphNodeConnections() {
		type = Type.NONE;
		connections = new LinkedHashSet<>();
	}

	public void addConnection(BlockVector3 connection) {
		connections.add(connection);
		type = type.add(Type.SINGLE);
	}

	public void removeConnection(BlockVector3 connection) {
		if(connections.remove(connection))
			type = type.typeFromConnectionCount(connections.size());
	}

	public void combine(RedstoneTracerGraphNodeConnections connection) {
		type = type.add(connection.type);
		connections.addAll(connection.connections);
	}

	public String toString() {
		StringBuilder output = new StringBuilder();

		for(BlockVector3 pos : connections) {
			output.append(pos);
		}

		return output.toString();
	}
}
