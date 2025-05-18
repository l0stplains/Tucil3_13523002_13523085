package tucil_3_stima.model;

import java.util.BitSet;

public class Vehicle {
    private final boolean horizontal;
    private final int length;
    private final char symbol;

    public Vehicle(boolean horizontal, int length, char symbol) {
        this.horizontal = horizontal;
        this.length = length;
        this.symbol = symbol;
    }

    public boolean isHorizontal() { return horizontal; }
    public int length() { return length; }
    public char getSymbol() { return symbol; }

}
