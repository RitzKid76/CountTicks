package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ConnectionSetFactory {
    private final Set<Connection> connections = new HashSet<>();

    public ConnectionSetFactory() {}

    public ConnectionSetFactory add(EnumSet<ConnectionDirection> directions, PowerType type) { return add(directions, type, SignalStrength.INPUT_DEPENDENT); }
    public ConnectionSetFactory add(EnumSet<ConnectionDirection> directions, PowerType type, SignalStrength ss) {
        for(ConnectionDirection direction : directions) {
            connections.add(new Connection(direction, type, ss));
        }
        return this;
    }


    public ConnectionSetFactory add(Set<Connection> direction) {
        connections.addAll(direction);
        return this;
    }

    public ConnectionSetFactory add(ConnectionDirection direction, PowerType type) { return add(direction, type, SignalStrength.INPUT_DEPENDENT); }
    public ConnectionSetFactory add(ConnectionDirection direction, PowerType type, SignalStrength ss) {
        this.connections.add(new Connection(direction, type, ss));
        return this;
    }

    @SafeVarargs
    public static Set<Connection> createConnectionSet(PowerType powerType, EnumSet<ConnectionDirection>... connectionSets) { return createConnectionSet(powerType, SignalStrength.INPUT_DEPENDENT, connectionSets); }
    @SafeVarargs
    public static Set<Connection> createConnectionSet(PowerType powerType, SignalStrength ss, EnumSet<ConnectionDirection>... connectionSets) {
        Set<Connection> output = new HashSet<>();

        for(EnumSet<ConnectionDirection> set : connectionSets) {
            for(ConnectionDirection connection : set) {
                output.add(new Connection(connection, powerType, ss));
            }
        }

        return output;
    }

    public Set<Connection> get() { return connections; }
}
