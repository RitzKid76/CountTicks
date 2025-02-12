package org.ritzkid76.CountTicks.RedstoneTracer.Graph;

import com.sk89q.worldedit.math.BlockVector3;

import java.util.LinkedHashSet;
import java.util.Set;

public class RedstoneTracerGraphNodeConnections {
	public LinkedHashSet<BlockVector3> connections;

	public RedstoneTracerGraphNodeConnections() {
		connections = new LinkedHashSet<>();
	}

	public void addConnection(BlockVector3 connection) {
		connections.add(connection);
	}

	public void removeConnections(Set<BlockVector3> cons) {
		connections.removeAll(cons);
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
