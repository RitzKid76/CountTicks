package org.ritzkid76.CountTicks.RedstoneTracer.Graph;

import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;

import java.util.LinkedList;

public record RedstoneTracerGraphPath(RedstoneTracerGraphPathResult result, LinkedList<RedstoneTracerGraphNode> path, GameTickDelay gameTickDelay) {
	public int delay() { return gameTickDelay.gameTicks; }

	public RedstoneTracerGraphPath(RedstoneTracerGraphPathResult r) { this(r, null, null); }
	public RedstoneTracerGraphPath(LinkedList<RedstoneTracerGraphNode> p, GameTickDelay delay) { this(null, p, delay); }

	public RedstoneTracerGraphPath pathFound() { return new RedstoneTracerGraphPath(RedstoneTracerGraphPathResult.PATH_FOUND, path, gameTickDelay); }
}
