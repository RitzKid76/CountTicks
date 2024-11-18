package org.ritzkid76.CountTicks.RedstoneTracer.Graph;

import com.sk89q.worldedit.math.BlockVector3;

import java.util.LinkedHashSet;

public class RedstoneTracerGraphNodeConnections {
	public LinkedHashSet<BlockVector3> connections;

	public RedstoneTracerGraphNodeConnections() {
		connections = new LinkedHashSet<>();
	}

	public void addConnection(BlockVector3 connection) {
		connections.add(connection);
	}

	public void removeConnection(BlockVector3 connection) {
		connections.remove(connection);
	}

	public void combine(RedstoneTracerGraphNodeConnections connection) {
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
