package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection;

import org.bukkit.block.BlockFace;
import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;

public class Connection {
    public ConnectionDirection connectionDirection;
    public PowerType powerType;

    public Connection(ConnectionDirection direction, PowerType type) {
        connectionDirection = direction;
        powerType = type;
    }

    public boolean isCompatableWith(Connection other) {
        return (
            PowerType.compare(powerType, other.powerType) <= 0 &&
                connectionDirection == other.connectionDirection
        );
    }

    public BlockFace toBlockFace() {
        return ConnectionDirection.toBlockFace(connectionDirection);
    }

    public Connection updateDirection(ConnectionDirection direction) {
        return new Connection(direction, powerType);
    }

    public void updatePowerType(PowerType newType) { powerType = newType; }
}