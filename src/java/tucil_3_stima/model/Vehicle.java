package tucil_3_stima.model;

import java.util.BitSet;

public class Vehicle {
    private final boolean horizontal;
    private final int length;

    public Vehicle(boolean horizontal, int length) {
        this.horizontal = horizontal;
        this.length = length;
    }

    public boolean isHorizontal() { return horizontal; }
    public int length() { return length; }

}
