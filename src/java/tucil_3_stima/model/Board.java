package tucil_3_stima.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tucil_3_stima.strategy.Heuristic;

public class Board {
    private final int rows, cols;
    private final int exitRow, exitCol;
    private final boolean exitHorizontal;
    private final Vehicle[] vehicles;
    private final List<Map<Integer, BitSet>> vehicleMasks;
    private static final int[] DIRS = { -1, +1 };

    public Board(int rows, int cols, int exitRow, int exitCol, boolean exitHorizontal, Vehicle[] vehicles) {
        this.rows = rows;
        this.cols = cols;
        this.exitRow = exitRow;
        this.exitCol = exitCol;
        this.exitHorizontal = exitHorizontal;
        this.vehicles = vehicles.clone();
        this.vehicleMasks = precomputeMasks();
    }

    private List<Map<Integer, BitSet>> precomputeMasks() {
        List<Map<Integer, BitSet>> list = new ArrayList<>();
        int total = rows * cols;
        for (Vehicle v : vehicles) {
            Map<Integer, BitSet> map = new HashMap<>();
            for (int base = 0; base < total; base++) {
                int r = base / cols;
                int c = base % cols;
                // Check full fit within bounds
                boolean fits;
                if (v.isHorizontal()) {
                    fits = (c + v.length() - 1) < cols;
                } else {
                    fits = (r + v.length() - 1) < rows;
                }
                if (!fits) continue;
                // Build mask
                BitSet mask = new BitSet(rows * cols);
                for (int d = 0; d < v.length(); d++) {
                    int rr = v.isHorizontal() ? r : r + d;
                    int cc = v.isHorizontal() ? c + d : c;
                    mask.set(rr * cols + cc);
                }
                map.put(base, mask);
            }
            list.add(map);
        }
        return list;
    }

    public boolean atExit(State state) {
        int[] pos     = state.getPositions();
        int base      = pos[0];
        Vehicle primary   = vehicles[0];
        int row       = base / cols;
        int col       = base % cols;

        boolean horiz = primary.isHorizontal();
        int frontRow  = horiz ? row : (row + primary.length() - 1);
        int frontCol  = horiz ? (col + primary.length() - 1) : col;

        if (horiz) {
            return row == exitRow && (frontCol == exitCol || col == exitCol);
        }
        else {
            return col == exitCol && (frontRow == exitRow || row == exitRow);
        }
    }

    public BitSet occupancy(State state) {
        BitSet occ = new BitSet(rows * cols);
        int[] pos = state.getPositions();
        for (int i = 0; i < vehicles.length; i++) {
            BitSet m = vehicleMasks.get(i).get(pos[i]);
            if (m != null) occ.or(m);
        }
        return occ;
    }

    public int heuristic(State state, Heuristic h) {
        return h.evaluate(this, state);
    }

    public List<State> neighbors(State state, Heuristic h) {
        // worst case each car can slide at most max(rows, cols) squares in one direction
        int maxSlides = Math.max(rows, cols);
        List<State> list = new ArrayList<>(vehicles.length * maxSlides);

        BitSet occ = occupancy(state);
        int[] pos = state.getPositions();

        for (int i = 0; i < vehicles.length; i++) {
            Vehicle v = vehicles[i];
            int oldBase = pos[i];
            Map<Integer, BitSet> masks = vehicleMasks.get(i);
            BitSet orig = masks.get(oldBase);

            // remove this car from the occupancy
            occ.xor(orig);

            // sliding in both directions
            for (int dir : DIRS) {
                int base = oldBase;
                while (true) {
                    int next = v.isHorizontal()
                            ? base + dir
                            : base + dir * cols;

                    BitSet m = masks.get(next);
                    if (m == null || m.intersects(occ)) {
                        break;
                    }

                    // valid move then allocate state n cost
                    State ns = state.copy();
                    int dist = v.isHorizontal()
                            ? next - oldBase
                            : (next - oldBase) / cols;
                    ns.setLastMovement(i, dist);
                    ns.setPosition(i, next);
                    ns.incrementG();
                    ns.setH(heuristic(ns, h));
                    ns.setParent(state);
                    list.add(ns);

                    base = next;
                }
            }

            // restore this car’s bits
            occ.or(orig);
        }

        return list;
    }

    public BitSet getVehicleMask(int vehicleIdx, int base) {
        return vehicleMasks.get(vehicleIdx).get(base);
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getExitRow() { return exitRow; }
    public int getExitCol() { return exitCol; }
    public boolean getExitHorizontal() { return exitHorizontal; }
    public Vehicle getVehicle(int i) { return vehicles[i]; }
    public Vehicle[] getVehicles() { return vehicles; }
}