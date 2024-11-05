package org.ritzkid76.CountTicks.RedstoneTracer.Graph;

import com.sk89q.worldedit.math.BlockVector3;
import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;
import org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Traceable;

import java.util.HashSet;
import java.util.Set;

public class RedstoneTracerGraphNode {
	public RedstoneTracerGraphNodeConnections inputs;
	public RedstoneTracerGraphNodeConnections outputs;

	public Set<BlockVector3> nodeMembers;

	public BlockVector3 position;
	public GameTickDelay gameTickDelay;


	public RedstoneTracerGraphNode(BlockVector3 pos, GameTickDelay delay) {
		inputs = new RedstoneTracerGraphNodeConnections();
		outputs = new RedstoneTracerGraphNodeConnections();
		position = pos;
		gameTickDelay = delay;

		nodeMembers = new HashSet<>();

		addNodeMember(pos);
	}

	public void addNodeMember(Traceable member) { addNodeMember(member.getPosition()); }
	public void addNodeMember(BlockVector3 member) { nodeMembers.add(member); }

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

	public void addInput(BlockVector3 input) { inputs.addConnection(input); }
	public void addOutput(BlockVector3 output) { outputs.addConnection(output); }

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false;

		RedstoneTracerGraphNode that = (RedstoneTracerGraphNode) obj;
		return position.equals(that.position);
	}

	public boolean containsMember(BlockVector3 pos) { return nodeMembers.contains(pos); }

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
