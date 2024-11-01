package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection;

import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ConnectionSetFactory {
    private Set<Connection> connections = new HashSet<>();

    public ConnectionSetFactory() {}

    public ConnectionSetFactory add(EnumSet<ConnectionDirection> directions, PowerType type) {
        for(ConnectionDirection direction : directions) {
            connections.add(new Connection(direction, type));
        }
        return this;
    }

    public ConnectionSetFactory add(EnumSet<ConnectionDirection> directions, PowerType type, GameTickDelay delay) {
        for(ConnectionDirection direction : directions) {
            connections.add(new Connection(direction, type, delay));
        }
        return this;
    }

    public ConnectionSetFactory add(Set<Connection> direction) {
        connections.addAll(direction);
        return this;
    }

    public ConnectionSetFactory add(ConnectionDirection direction, PowerType type, GameTickDelay delay) {
        this.connections.add(new Connection(direction, type, delay));
        return this;
    }
    public ConnectionSetFactory add(ConnectionDirection direction, PowerType type) {
        this.connections.add(new Connection(direction, type));
        return this;
    }

    @SafeVarargs
    public static Set<Connection> createConnectionSet(PowerType powerType, EnumSet<ConnectionDirection>... connectionSets) {
        Set<Connection> output = new HashSet<>();

        for(EnumSet<ConnectionDirection> set : connectionSets) {
            for(ConnectionDirection connection : set) {
                output.add(new Connection(connection, powerType));
            }
        }

        return output;
    }

    public Set<Connection> get() { return connections; }
}
