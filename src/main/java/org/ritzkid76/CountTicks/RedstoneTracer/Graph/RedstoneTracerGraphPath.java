package org.ritzkid76.CountTicks.RedstoneTracer.Graph;

import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;
import org.ritzkid76.CountTicks.RedstoneTracer.RedstoneTracerPathResult;

import java.util.LinkedList;

public record RedstoneTracerGraphPath(RedstoneTracerPathResult result, LinkedList<RedstoneTracerGraphNode> path, GameTickDelay gameTickDelay) {
	public int delay() { return gameTickDelay.gameTicks; }

	public RedstoneTracerGraphPath(RedstoneTracerPathResult r) { this(r, null, null); }
	public RedstoneTracerGraphPath(LinkedList<RedstoneTracerGraphNode> p, GameTickDelay delay) { this(RedstoneTracerPathResult.NO_PATH, p, delay); }

	public RedstoneTracerGraphPath pathFound() { return new RedstoneTracerGraphPath(RedstoneTracerPathResult.PATH_FOUND, path, gameTickDelay); }
}
