package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection;

import org.bukkit.block.BlockFace;
import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;

public class Connection {
    public ConnectionDirection connectionDirection;
    public PowerType powerType;
    public GameTickDelay gameTickDelay;

    public Connection(ConnectionDirection direction, PowerType type, GameTickDelay delay) {
        connectionDirection = direction;
        powerType = type;
        gameTickDelay = delay;
    }
    public Connection(ConnectionDirection direction, PowerType type) {
        connectionDirection = direction;
        powerType = type;
        gameTickDelay = new GameTickDelay(0);
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
        return new Connection(direction, powerType, gameTickDelay);
    }

    public void updatePowerType(PowerType newType) { powerType = newType; }
}