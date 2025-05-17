// State.java
package tucil_3_stima.model;

import java.util.Arrays;

public class State {
    private final int[] positions;
    private int g, h;
    private State parent;

    public State(int[] initial) {
        this.positions = initial.clone();
    }

    public State copy() {
        State s = new State(this.positions);
        s.g      = this.g;
        s.h      = this.h;
        s.parent = this.parent;
        return s;
    }

    public int[] getPositions() { return positions; }
    public int getG()           { return g;         }
    public int getH()           { return h;         }
    public int getF()           { return g + h;     }
    public State getParent()    { return parent;    }

    public void setPosition(int idx, int val) { positions[idx] = val; }
    public void incrementG()                  { g++;                }
    public void setH(int h)                   { this.h = h;         }
    public void setParent(State p)            { this.parent = p;    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(positions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;
        return Arrays.equals(positions, ((State)o).positions);
    }
}
