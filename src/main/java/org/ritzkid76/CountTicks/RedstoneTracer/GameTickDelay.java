package org.ritzkid76.CountTicks.RedstoneTracer;

public class GameTickDelay {
    public int gameTicks = 0;
    public GameTickDelay(int delay) { gameTicks = delay; }
    public GameTickDelay() {}
    public void add(GameTickDelay addDelay) { gameTicks += addDelay.gameTicks;}

    public String toString() {
        return
            "{" +
                gameTicks / 2 + "t | " +
                gameTicks + "gt" +
            "}";
    }
}
