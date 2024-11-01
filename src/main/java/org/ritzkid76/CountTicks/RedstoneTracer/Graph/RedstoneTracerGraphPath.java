package org.ritzkid76.CountTicks.RedstoneTracer.Graph;

import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;

import java.util.LinkedList;

public record RedstoneTracerGraphPath(LinkedList<RedstoneTracerGraphNode> path, GameTickDelay gameTickDelay) {
    public int totalGameTicks() { return gameTickDelay.gameTicks; }
}
