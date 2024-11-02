package org.ritzkid76.CountTicks.RedstoneTracer.Traceable.Connection;

public enum SignalStrength {
    MAX(15), SS(-1), ZERO(0),
    INPUT_DEPENDENT(-1);

    private int signalStrength;

    SignalStrength(int ss) { signalStrength = ss; }

    public static SignalStrength of(int value) {
        SignalStrength instance = SS;
        instance.signalStrength = assertRange(value);
        return instance;
    }

    public void decrement() { signalStrength = assertRange(signalStrength - 1); }
    public void subtract(SignalStrength ss) { signalStrength = assertRange(signalStrength - ss.signalStrength); }
    public int compare(SignalStrength ss) { return Integer.compare(signalStrength, ss.signalStrength); }
    private static int assertRange(int ss) { return Math.clamp(ss, 0, 15); }

    public boolean isZero() { return signalStrength == 0; }
}
