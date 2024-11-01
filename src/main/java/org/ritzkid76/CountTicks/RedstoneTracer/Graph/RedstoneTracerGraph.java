package org.ritzkid76.CountTicks.RedstoneTracer.Graph;

import com.sk89q.worldedit.math.BlockVector3;
import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Traceable;

import java.util.*;

public class RedstoneTracerGraph {
    private Map<BlockVector3, RedstoneTracerGraphNode> positionToNode = new HashMap<>();
    private Set<BlockVector3> contents = new HashSet<>();
    private BlockVector3 origin;

    public RedstoneTracerGraph(BlockVector3 origin) { this.origin = origin; }

    private void linkChild(RedstoneTracerGraphNode node, BlockVector3 pos) {
        contents.add(pos);
        positionToNode.putIfAbsent(pos, node);
    }
    private void link(RedstoneTracerGraphNode node, BlockVector3 pos) {
        contents.add(pos);
        positionToNode.put(pos, node);
    }
    private void linkChildren(RedstoneTracerGraphNode node, Set<Traceable> children) {
        for(Traceable child : children) {
            BlockVector3 childPos = child.getPosition();
            linkChild(child.toRedstoneTracerGraphNode(), childPos);

            if(node.containsMember(childPos)) continue;
            node.addOutput(childPos);
            RedstoneTracerGraphNode childNode = positionToNode.get(childPos);
            childNode.addInput(node.position);
        }
    }

    private RedstoneTracerGraphNode chainBuilder = null;
    public void add(Traceable current, Set<Traceable> children, boolean chainLast) {
        RedstoneTracerGraphNode currentNode = positionToNode.getOrDefault(current.getPosition(), current.toRedstoneTracerGraphNode());

        if(chainBuilder == null) chainBuilder = currentNode;
        else if(chainLast) chainBuilder.combine(currentNode);
        else chainBuilder = currentNode;

        linkChildren(chainBuilder, children);
        link(chainBuilder, currentNode.position);

        if(!chainLast) chainBuilder = null;
    }

    public boolean contains(BlockVector3 pos) {
        return contents.contains(pos);
    }

    public RedstoneTracerGraphPath fastestPath(BlockVector3 destination) {
        if(!contains(destination)) return new RedstoneTracerGraphPath(new LinkedList<>(), null);

        return djikstra(positionToNode.get(destination));
    }

    private RedstoneTracerGraphPath djikstra(RedstoneTracerGraphNode destination) {
        PriorityQueue<RedstoneTracerGraphPath> queue = new PriorityQueue<>(
            Comparator.comparingInt(RedstoneTracerGraphPath::totalGameTicks)
        );
        Set<RedstoneTracerGraphNode> visited = new HashSet<>();

        RedstoneTracerGraphPath startPath = new RedstoneTracerGraphPath(
            new LinkedList<>(List.of(destination)),
            new GameTickDelay()
        );
        queue.add(startPath);

        while(!queue.isEmpty()) {
            RedstoneTracerGraphPath currentPath = queue.poll();
            RedstoneTracerGraphNode currentNode = currentPath.path().getLast();

            if(!visited.add(currentNode)) continue;
            if(currentNode.position == origin) return currentPath;

            for(BlockVector3 inputPos : currentNode.inputs.connections) {
                RedstoneTracerGraphNode inputNode = positionToNode.get(inputPos);
                GameTickDelay inputNodeDelay = inputNode.gameTickDelay;

                RedstoneTracerGraphPath newPath = new RedstoneTracerGraphPath(
                    new LinkedList<>(currentPath.path()),
                    new GameTickDelay(currentPath.totalGameTicks())
                );
                newPath.path().add(inputNode);
                newPath.gameTickDelay().add(inputNodeDelay);

                queue.add(newPath);
            }
        }

        return new RedstoneTracerGraphPath(new LinkedList<>(), null);
    }

    public String toString() {
        StringBuilder output = new StringBuilder().append("\n");

        for(BlockVector3 pos : positionToNode.keySet()) {
            output.append(pos);
            BlockVector3 stored = positionToNode.get(pos).position;

            if(stored == pos) output.append(" -> ");
            else output.append(" => ");

            output.append(positionToNode.get(pos)).append("\n");
        }

        return output.toString();
    }
}
