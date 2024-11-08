package org.ritzkid76.CountTicks.RedstoneTracer.Traceable;

import org.bukkit.block.BlockFace;
import org.ritzkid76.CountTicks.RedstoneTracer.GameTickDelay;

public record TraceableBlockData(BlockFace direction, GameTickDelay gameTickDelay) {
	public TraceableBlockData() {
		this(BlockFace.NORTH, new GameTickDelay());
	}
	public TraceableBlockData(GameTickDelay delay) {
		 this(BlockFace.NORTH, delay);
	}
	public TraceableBlockData(BlockFace face) {
		 this(face, new GameTickDelay());
	}
}
