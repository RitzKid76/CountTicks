package org.ritzkid76.CountTicks.RedstoneTracer;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.World;
import org.ritzkid76.CountTicks.Debug;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraph;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Traceable;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableFactory;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class RedstoneTracer {
    private static World tracerWorld;

    public static World getTracerWorld() {
        return tracerWorld;
    }

    private final RedstoneTracerGraph graph;
    private final Set<BlockVector3> visited = new HashSet<>();
    private final PriorityQueue<Traceable> queue = new PriorityQueue<>(
        Comparator.comparing(t -> !isPriority(t))
    );

    private boolean invalidSelection;

    private boolean isPriority(Traceable t) { return t.delay() == 0; }

    public RedstoneTracer(World world, BlockVector3 startPoint) {
        graph = new RedstoneTracerGraph(startPoint);
        trace(world, startPoint);
    }

    private void trace(World world, BlockVector3 startPoint) {
        tracerWorld = world;

        Traceable startTraceable;

        try {
            startTraceable = TraceableFactory.traceableFromBlockVector3(startPoint);
            queue.add(startTraceable);
        } catch (Exception e) {
            invalidSelection = true;
            return;
        }

        int iterations = 2000; //safety measure in case i fuck up
        while (!queue.isEmpty() && iterations-- > 0) {
            Traceable current = queue.remove();
            BlockVector3 currentPos = current.getPosition();

            visited.add(currentPos);

            Set<Traceable> candidates = current.getNeighbors();
            graph.add(current, candidates, isPriority(current));

            candidates.removeIf(c -> visited.contains(c.getPosition()));
            queue.addAll(candidates);
        }

        if(iterations <= 0) Debug.log("ITERATION LIMIT EXCEEDED." , "WARNING");
    }

    public RedstoneTracerResult getPath(BlockVector3 endPoint) {
        if (invalidSelection) return new RedstoneTracerResult(RedstoneTracerResultType.INVALID_SELECTION);

        if (graph.contains(endPoint)) return new RedstoneTracerResult(RedstoneTracerResultType.PATH_FOUND, graph);
        return new RedstoneTracerResult(RedstoneTracerResultType.NO_PATH);
    }
}
