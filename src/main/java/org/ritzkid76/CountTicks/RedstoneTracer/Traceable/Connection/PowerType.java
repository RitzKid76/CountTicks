package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection;

public enum PowerType {
    INPUT_DEPENDENT(-1), // custom type that depends on inputs
    NONE(-1),

    ANY(0),             // all the following
    WEAK(1),            // signal taken through blocks from dust
    SOFT(2),            // dust-like powering
    HARD(3);            // repeater-like powering

    private final int order;

    PowerType(int order) { this.order = order; }

    public int getOrder() { return order; }

    public static int compare(PowerType p1, PowerType p2) { return Integer.compare(p1.getOrder(), p2.getOrder()); }
}
