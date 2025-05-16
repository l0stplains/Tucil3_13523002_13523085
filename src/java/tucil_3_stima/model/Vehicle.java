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

    public BitSet maskAt(int baseIndex, int rows, int cols) {
        BitSet mask = new BitSet(rows * cols);
        int r = baseIndex / cols;
        int c = baseIndex % cols;
        for (int d = 0; d < length; d++) {
            int rr = horizontal ? r : r + d;
            int cc = horizontal ? c + d : c;
            if (rr >= rows || cc >= cols) break;
            mask.set(rr * cols + cc);
        }
        return mask;
    }
}
