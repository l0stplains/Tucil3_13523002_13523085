package tucil_3_stima.model;

import java.util.Arrays;

public class State {
    private final int[] positions;
    private int g, h;
    private State parent;
    private String key;

    public State(int[] initial) {
        this.positions = initial.clone();
        computeKey();
    }

    public State copy() {
        State s = new State(positions);
        s.g = this.g;
        s.h = this.h;
        s.parent = this.parent;
        return s;
    }

    public int[] getPositions() { return positions; }
    public int getG() { return g; }
    public int getH() { return h; }
    public int getF() { return g + h; }
    public State getParent() { return parent; }
    public String getKey() { return key; }

    public void setPosition(int idx, int val) { positions[idx] = val; computeKey(); }
    public void incrementG() { g++; }
    public void setH(int h) { this.h = h; }
    public void setParent(State p) { this.parent = p; }

    private void computeKey() {
        this.key = Arrays.toString(positions);
    }
}
