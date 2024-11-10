package org.ritzkid76.CountTicks.RedstoneTracer.Traceable;

import com.sk89q.worldedit.math.BlockVector3;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraphNode;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection.*;

import java.util.HashSet;
import java.util.Set;

public abstract class Traceable {
	private final Set<Connection> inputs;
	private final Set<Connection> outputs;
	private final BlockVector3 position;
	private final GameTickDelay gameTickDelay;

	public final World world;

	public Traceable(Set<Connection> in, Set<Connection> out, BlockData blockData, BlockVector3 pos, World wld) {
		TraceableBlockData data = applyBlockData(blockData);
		BlockFace direction = data.direction();
		GameTickDelay delay = data.gameTickDelay();

		position = pos;
		gameTickDelay = delay;
		world = wld;

		inputs = processConnections(new HashSet<>(in), ConnectionType.INPUTS, direction);
		outputs = processConnections(new HashSet<>(out), ConnectionType.OUTPUTS, direction);
	}

	private Set<Connection> processConnections(Set<Connection> connections, ConnectionType type, BlockFace direction) {
		connections.removeIf(c -> filterConnection(c, type));
		return ConnectionRotator.rotateAllConnections(connections, direction);
	}

	public BlockVector3 getPosition() {
		return position;
	}

	public int delay() {
		return gameTickDelay.gameTicks;
	}
	public GameTickDelay getGameTickDelay() {
		return gameTickDelay;
	}

	private Traceable getTraceableFromConnectionDirection(World world, ConnectionDirection connectionType) {
		BlockVector3 target = ConnectionDirection.positionFromConnectionDirection(
			position,
			connectionType
		);

		return TraceableFactory.traceableFromBlockVector3(world, target);
	}

	private boolean isValidConnection(Traceable traceable, Connection input) {
		for(Connection connection : traceable.inputs) {
			if(connection.isCompatibleWith(input))
				return true;
		}
		return false;
	}

	private void processDependentOutputPowers(Traceable traceable, PowerType inputPower) {
		for(Connection connection : traceable.outputs) {
			PowerType outputPower = connection.powerType;
			if(outputPower == PowerType.INPUT_DEPENDENT)
				traceable.getInputDependentPower(connection, inputPower);
		}
	}

	public Set<Traceable> getNeighbors(World world) {
		Set<Traceable> result = new HashSet<>();

		for(Connection outputConnection: outputs) {
			PowerType connectedTraceableInputPower = outputConnection.powerType;
			Traceable connectedTraceable = getTraceableFromConnectionDirection(world, outputConnection.connectionDirection);

			if(connectedTraceable == null)
				continue; // throw out invalid blocks
			if(connectedTraceableInputPower.compareTo(PowerType.NONE) <= 0)
				continue; // no reason to have this connection if the input power is none

			processDependentOutputPowers(connectedTraceable, connectedTraceableInputPower);

			if(isValidConnection(connectedTraceable, outputConnection))
				result.add(connectedTraceable);
		}

		return result;
	}

	public RedstoneTracerGraphNode toRedstoneTracerGraphNode() {
		return new RedstoneTracerGraphNode(position, gameTickDelay);
	}

	public TraceableBlockData applyBlockData(BlockData blockData) {
		return new TraceableBlockData();
	}
	public boolean filterConnection(Connection connection, ConnectionType type) {
		return false;
	}

	public Material getMaterial() {
		return null;
	}

	public void getInputDependentPower(Connection connection, PowerType powerType) {
		switch(powerType) {
			case HARD -> connection.updatePowerType(PowerType.SOFT);
			case SOFT -> connection.updatePowerType(PowerType.WEAK);
			default-> connection.updatePowerType(PowerType.NONE);
		}
	}
}
