package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection;

import org.bukkit.block.BlockFace;

import java.util.EnumSet;

public enum ConnectionDirection {
    UP, DOWN, NORTH, EAST, SOUTH, WEST,
    NORTH_UP, EAST_UP, SOUTH_UP, WEST_UP,
    NORTH_DOWN, EAST_DOWN, SOUTH_DOWN, WEST_DOWN;

    public static final EnumSet<ConnectionDirection> ALL = EnumSet.of(UP, DOWN, NORTH, EAST, SOUTH, WEST, NORTH_UP, EAST_UP, SOUTH_UP, WEST_UP, NORTH_DOWN, EAST_DOWN, SOUTH_DOWN, WEST_DOWN);

    public static final EnumSet<ConnectionDirection> AXIAL = EnumSet.of(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    public static final EnumSet<ConnectionDirection> CARDINAL = EnumSet.of(NORTH, EAST, SOUTH, WEST);
    public static final EnumSet<ConnectionDirection> VERTICAL = EnumSet.of(UP, DOWN);

    public static final EnumSet<ConnectionDirection> UPWARD_DIAGONAL = EnumSet.of(NORTH_UP, EAST_UP, SOUTH_UP, WEST_UP);
    public static final EnumSet<ConnectionDirection> DOWNWARD_DIAGONAL = EnumSet.of(NORTH_DOWN, EAST_DOWN, SOUTH_DOWN, WEST_DOWN);

    public static final EnumSet<ConnectionDirection> UPWARD = EnumSet.of(UP, NORTH_UP, EAST_UP, SOUTH_UP, WEST_UP);
    public static final EnumSet<ConnectionDirection> DOWNWARD = EnumSet.of(DOWN, NORTH_DOWN, EAST_DOWN, SOUTH_DOWN, WEST_DOWN);
    public static final EnumSet<ConnectionDirection> NORTHERN = EnumSet.of(NORTH, NORTH_UP, NORTH_DOWN);
    public static final EnumSet<ConnectionDirection> EASTERN = EnumSet.of(EAST, EAST_UP, EAST_DOWN);
    public static final EnumSet<ConnectionDirection> SOUTHERN = EnumSet.of(SOUTH, SOUTH_UP, SOUTH_DOWN);
    public static final EnumSet<ConnectionDirection> WESTERN = EnumSet.of(WEST, WEST_UP, WEST_DOWN);

    public static BlockFace toBlockFace(ConnectionDirection direction) {
        switch(direction) {
            case ConnectionDirection c when NORTHERN.contains(c) -> { return BlockFace.NORTH; }
            case ConnectionDirection c when EASTERN.contains(c) -> { return BlockFace.EAST; }
            case ConnectionDirection c when SOUTHERN.contains(c) -> { return BlockFace.SOUTH; }
            case ConnectionDirection c when WESTERN.contains(c) -> { return BlockFace.WEST; }
            case ConnectionDirection c when UPWARD.contains(c) -> { return BlockFace.UP; }
            case ConnectionDirection c when DOWNWARD.contains(c) -> { return BlockFace.DOWN; }
            default -> { return null; } // literally impossible, but IDEs and Java disagree
        }
    }
}