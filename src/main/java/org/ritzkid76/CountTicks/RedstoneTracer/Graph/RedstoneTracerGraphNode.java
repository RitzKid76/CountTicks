package org.ritzkid76.CountTicks.RedstoneTracer.Graph;

import com.sk89q.worldedit.math.BlockVector3;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Traceable;

import java.util.HashSet;
import java.util.Set;

public class RedstoneTracerGraphNode {
	public RedstoneTracerGraphNodeConnections inputs;
	public RedstoneTracerGraphNodeConnections outputs;

	public Set<BlockVector3> nodeMembers;

	public final BlockVector3 position;
	public final GameTickDelay gameTickDelay;
	private final World world;

	private static final DustOptions DUST_OPTIONS = new DustOptions(Color.LIME, 2); 


	public RedstoneTracerGraphNode(BlockVector3 pos, GameTickDelay delay, World w) {
		inputs = new RedstoneTracerGraphNodeConnections();
		outputs = new RedstoneTracerGraphNodeConnections();

		position = pos;
		gameTickDelay = delay;
		world = w;

		nodeMembers = new HashSet<>();

		addNodeMember(pos);
	}

	public void addNodeMember(Traceable member) {
		addNodeMember(member.getPosition());
	}
	public void addNodeMember(BlockVector3 member) {
		nodeMembers.add(member);
	}

	private void removeSharedConnections(RedstoneTracerGraphNode node) {
		for(BlockVector3 member : nodeMembers) {
			node.outputs.removeConnection(member);
			node.inputs.removeConnection(member);
		}
		for(BlockVector3 nodeMember : node.nodeMembers) {
			outputs.removeConnection(nodeMember);
			inputs.removeConnection(nodeMember);
		}
	}
	public void combine(RedstoneTracerGraphNode node) {
		removeSharedConnections(node);

		inputs.combine(node.inputs);
		outputs.combine(node.outputs);
		nodeMembers.addAll(node.nodeMembers);
	}

	public void addInput(BlockVector3 input) {
		inputs.addConnection(input);
	}
	public void addOutput(BlockVector3 output) {
		outputs.addConnection(output);
	}

	private void createParticle(BlockVector3 pos) {
		world.spawnParticle(
			Particle.DUST,
			pos.x() + .5, pos.y() + .5, pos.z() + .5,
			1,
			DUST_OPTIONS
		);
	}
	public void show() {
		for(BlockVector3 pos : nodeMembers) {
			createParticle(pos);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null || getClass() != obj.getClass())
			return false;

		RedstoneTracerGraphNode that = (RedstoneTracerGraphNode) obj;
		return position.equals(that.position);
	}

	public boolean containsMember(BlockVector3 pos) {
		return nodeMembers.contains(pos);
	}

	@Override
	public int hashCode() {
		return position.hashCode();
	}

	public String toString() {
		return "\nNode" + nodeMembers + "\n" +
		"| i " + inputs.toString() + "\n" +
		"| o " + outputs.toString();
	}
}
