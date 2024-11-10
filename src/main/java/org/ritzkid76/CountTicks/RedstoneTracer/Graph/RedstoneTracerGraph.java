package org.ritzkid76.CountTicks.RedstoneTracer.Graph;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;
import org.ritzkid76.CountTicks.Exceptions.BoundsUndefinedException;
import org.ritzkid76.CountTicks.Exceptions.NonTraceableStartPositionException;
import org.ritzkid76.CountTicks.Exceptions.PositionOutOfRegionBoundsException;
import org.ritzkid76.CountTicks.Exceptions.ThreadCanceledException;
import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Traceable;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.TraceableFactory;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

public class RedstoneTracerGraph {
	private Map<BlockVector3, RedstoneTracerGraphNode> positionToNode = new HashMap<>();
	private Set<BlockVector3> contents = new HashSet<>();
	private BlockVector3 origin;
	private Region bounds;
	private World world;

	private final Set<BlockVector3> visited = new HashSet<>();
	private final BooleanQueue<Traceable> queue = new BooleanQueue<>(this::isChainable);

	private boolean isChainable(Traceable t) {
		return t.getMaterial() == Material.REDSTONE_WIRE;
	}

	@SuppressWarnings("null")
	public RedstoneTracerGraph(BlockVector3 origin, Region bounds) {
		this.origin = origin;
		this.bounds = bounds;

		if(bounds == null)
			throw new BoundsUndefinedException();
		if(!isInBounds(origin))
			throw new PositionOutOfRegionBoundsException();

		world = Bukkit.getWorld(bounds.getWorld().getName());
	}

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

			if(node.containsMember(childPos))
				continue;
			node.addOutput(childPos);
			RedstoneTracerGraphNode childNode = positionToNode.get(childPos);
			childNode.addInput(node.position);
		}
	}

	private RedstoneTracerGraphNode chainBuilder = null;
	private void add(Traceable current, Set<Traceable> children, boolean chainLast) {
		RedstoneTracerGraphNode currentNode = positionToNode.getOrDefault(current.getPosition(), current.toRedstoneTracerGraphNode());

		if(chainBuilder == null)
			chainBuilder = currentNode;
		else if(chainLast)
			chainBuilder.combine(currentNode);
		else
			chainBuilder = currentNode;

		linkChildren(chainBuilder, children);
		link(chainBuilder, currentNode.position);

		if(!chainLast)
			chainBuilder = null;
	}

	private boolean contains(BlockVector3 pos) {
		return contents.contains(pos);
	}

	public RedstoneTracerGraphPath findFastestPath(BlockVector3 destination) {
		if(!isInBounds(destination))
			return new RedstoneTracerGraphPath(RedstoneTracerGraphPathResult.OUT_OF_BOUNDS);
		if(!contains(destination))
			return new RedstoneTracerGraphPath(RedstoneTracerGraphPathResult.UNSCANNED_LOCATION);

		return djikstraFastestPath(positionToNode.get(destination));
	}

	private RedstoneTracerGraphPath djikstraFastestPath(RedstoneTracerGraphNode destination) {
		PriorityQueue<RedstoneTracerGraphPath> queue = new PriorityQueue<>(
			Comparator.comparingInt(RedstoneTracerGraphPath::delay)
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

			if(!visited.add(currentNode))
				continue;
			if(currentNode.position == origin)
				return currentPath.pathFound();

			for(BlockVector3 inputPos : currentNode.inputs.connections) {
				RedstoneTracerGraphNode inputNode = positionToNode.get(inputPos);
				GameTickDelay inputNodeDelay = inputNode.gameTickDelay;

				RedstoneTracerGraphPath newPath = new RedstoneTracerGraphPath(
					new LinkedList<>(currentPath.path()),
					new GameTickDelay(currentPath.delay())
				);
				newPath.path().add(inputNode);
				newPath.gameTickDelay().add(inputNodeDelay);

				queue.add(newPath);
			}
		}

		return new RedstoneTracerGraphPath(new LinkedList<>(), null);
	}

	private boolean isInBounds(BlockVector3 pos) {
		return bounds.contains(pos);
	}

	public String toString() {
		StringBuilder output = new StringBuilder().append("\n");

		for(BlockVector3 pos : positionToNode.keySet()) {
			output.append(pos);
			BlockVector3 stored = positionToNode.get(pos).position;

			if(stored == pos)
				output.append(" -> ");
			else
				output.append(" => ");

			output.append(positionToNode.get(pos)).append("\n");
		}

		return output.toString();
	}

	public boolean trace(BukkitTask task) {
		Traceable startTraceable;

		try {
			startTraceable = TraceableFactory.traceableFromBlockVector3(world, origin);
			if(startTraceable == null)
				throw new NonTraceableStartPositionException();

			queue.add(startTraceable);
		} catch (NonTraceableStartPositionException e) {
			return false;
		}

		while (!queue.isEmpty()) {
			if(task.isCancelled())
				throw new ThreadCanceledException();

			Traceable current = queue.remove();
			BlockVector3 currentPos = current.getPosition();

			visited.add(currentPos);

			Set<Traceable> candidates = current.getNeighbors(world);
			add(current, candidates, isChainable(current));

			candidates.removeIf(this::candidateRemoval);
			queue.addAll(candidates);
		}

		return true;
	}
	
	private boolean candidateRemoval(Traceable candidate) {
		BlockVector3 pos = candidate.getPosition();
		return
			visited.contains(pos) ||
			!isInBounds(pos);
	}

	public int totalScanned() {
		return visited.size();
	}
	public Set<BlockVector3> getVisited() {
		return visited;
	}
	public BlockVector3 getOrigin() {
		return origin;
	}
	public RedstoneTracerGraphNode get(BlockVector3 pos) {
		return positionToNode.get(pos);
	}
}
