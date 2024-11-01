package org.ritzkid76.CountTicks.RedstoneTracer.Traceable;

import com.sk89q.worldedit.math.BlockVector3;
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


    // some retard shit since java doesnt let me call super() unless its the first method in the function. makes sense, but sadge.
    public Traceable(Set<Connection> in, Set<Connection> out, BlockData blockData, BlockVector3 pos) {
        TraceableBlockData data = applyBlockData(blockData);
        BlockFace direction = data.direction();
        GameTickDelay delay = data.gameTickDelay();

        inputs = processConnections(new HashSet<>(in), ConnectionType.INPUTS, direction);
        outputs = processConnections(new HashSet<>(out), ConnectionType.OUTPUTS, direction);

        position = pos;
        gameTickDelay = delay;
    }

    private Set<Connection> processConnections(Set<Connection> connections, ConnectionType type, BlockFace direction) {
        connections.removeIf(c -> filterConnection(c, type));
        return ConnectionRotator.rotateAllConnections(connections, direction);
    }

    public BlockVector3 getPosition() {
        return position;
    }

    public int delay() { return gameTickDelay.gameTicks; }
    public GameTickDelay getGameTickDelay() {
        return gameTickDelay;
    }

    public Traceable getTraceableFromConnectionDirection(ConnectionDirection connectionType) {
        BlockVector3 target = getPosition();

        switch (connectionType) {
            case ConnectionDirection c when ConnectionDirection.UPWARD.contains(c) -> target = target.add(BlockVector3.UNIT_Y);
            case ConnectionDirection c when ConnectionDirection.DOWNWARD.contains(c) -> target = target.add(BlockVector3.UNIT_MINUS_Y);
            default -> {}
        }

        switch (connectionType) {
            case ConnectionDirection c when ConnectionDirection.NORTHERN.contains(c) -> target = target.add(BlockVector3.UNIT_MINUS_Z);
            case ConnectionDirection c when ConnectionDirection.EASTERN.contains(c) -> target = target.add(BlockVector3.UNIT_X);
            case ConnectionDirection c when ConnectionDirection.SOUTHERN.contains(c) -> target = target.add(BlockVector3.UNIT_Z);
            case ConnectionDirection c when ConnectionDirection.WESTERN.contains(c) -> target = target.add(BlockVector3.UNIT_MINUS_X);
            default -> {}
        }

        return TraceableFactory.traceableFromBlockVector3(target);
    }

    private boolean isValidConnection(Traceable traceable, Connection input) {
        for(Connection connection : traceable.inputs) {
            if(connection.isCompatableWith(input)) return true;
        }
        return false;
    }

    private void processDependentOutputPowers(Traceable traceable, PowerType inputPower) {
        for(Connection connection : traceable.outputs) {
            PowerType outputPower = connection.powerType;
            if(outputPower == PowerType.INPUT_DEPENDENT) traceable.getInputDependentPower(connection, inputPower);
        }
    }

    public Set<Traceable> getNeighbors() {
        Set<Traceable> result = new HashSet<>();

        for(Connection outputConnection: outputs) {
            PowerType connectedTraceableInputPower = outputConnection.powerType;
            try {
                Traceable connectedTraceable = getTraceableFromConnectionDirection(outputConnection.connectionDirection);

                if(connectedTraceableInputPower.compareTo(PowerType.NONE) <= 0) continue; // no reason to have this connection if the input power is none
                processDependentOutputPowers(connectedTraceable, connectedTraceableInputPower);

                if(isValidConnection(connectedTraceable, outputConnection)) result.add(connectedTraceable);
            } catch (Exception ignored) {} // throw out non-traceable blocks
        }

        return result;
    }

    public RedstoneTracerGraphNode toRedstoneTracerGraphNode() { return new RedstoneTracerGraphNode(position, gameTickDelay); }

    public TraceableBlockData applyBlockData(BlockData blockData) { return new TraceableBlockData(); }
    public boolean filterConnection(Connection connection, ConnectionType type) {return false; }

    public void getInputDependentPower(Connection connection, PowerType powerType) {
        switch(powerType) {
            case HARD -> connection.updatePowerType(PowerType.SOFT);
            case SOFT -> connection.updatePowerType(PowerType.WEAK);
            default-> connection.updatePowerType(PowerType.NONE);
        }
    }
}
