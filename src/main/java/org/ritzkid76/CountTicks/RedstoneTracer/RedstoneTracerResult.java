package org.ritzkid76.CountTicks.RedstoneTracer;

import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraph;

public record RedstoneTracerResult(RedstoneTracerResultType type, RedstoneTracerGraph graph) {
    public RedstoneTracerResult(RedstoneTracerResultType type) { this(type, null); }
}
