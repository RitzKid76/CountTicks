package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection;

import org.bukkit.block.BlockFace;

public class Connection {
    public ConnectionDirection connectionDirection;
    public PowerType powerType;
    public SignalStrength signalStrength;

    public Connection(ConnectionDirection direction, PowerType type, SignalStrength ss) {
        connectionDirection = direction;
        powerType = type;
        signalStrength = ss;
    }

    public boolean isCompatableWith(Connection other) {
        return (
            powerType.compare(other.powerType) <= 0 &&
            connectionDirection == other.connectionDirection
        );
    }

    public BlockFace toBlockFace() {
        return ConnectionDirection.toBlockFace(connectionDirection);
    }

    public Connection updateDirection(ConnectionDirection direction) {
        return new Connection(direction, powerType, signalStrength);
    }
    public void updatePowerType(PowerType newType) { powerType = newType; }

    public String toString() {
        return "{" + connectionDirection + "|" + powerType + "}";
    }
}