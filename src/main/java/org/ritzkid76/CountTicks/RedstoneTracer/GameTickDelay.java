package org.ritzkid76.CountTicks.RedstoneTracer;

public class GameTickDelay {
	public int gameTicks;
	public GameTickDelay(int delay) { gameTicks = delay; }
	public GameTickDelay() { gameTicks = 0; }
	public void add(GameTickDelay addDelay) { gameTicks += addDelay.gameTicks;}

	public String toString() {
		return
			"{" +
				gameTicks / 2 + "t | " +
				gameTicks + "gt" +
			"}";
	}
}
