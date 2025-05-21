package tucil_3_stima.strategy;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;
import tucil_3_stima.model.Vehicle;

public class RecursiveBlockingHeuristic implements Heuristic {

    @Override
    public int evaluate(Board board, State state) {
        Set<Integer> visited      = new HashSet<>();
        Set<Integer> blockingCars = new HashSet<>();
        BitSet occ                = board.occupancy(state);

        int[] pos     = state.getPositions();
        Vehicle red   = board.getVehicle(0);
        int base      = pos[0];
        int cols      = board.getCols();
        int row       = base / cols;
        int col       = base % cols;

        boolean horiz = red.isHorizontal();
        int exitCoord = horiz ? board.getExitCol() : board.getExitRow();
        int currCoord = horiz ? col : row;
        int dir       = exitCoord >= currCoord ? +1 : -1;

        // start just beyond the "front" of the red car in the correct direction
        int front = (dir > 0 ? currCoord + red.length()
                : currCoord - 1);

        // build path of cells from that front to the exit
        List<Integer> path = new ArrayList<>();
        if (horiz) {
            for (int c = front; dir > 0 ? c <= exitCoord : c >= exitCoord; c += dir) {
                path.add(row * cols + c);
            }
        } else {
            for (int r = front; dir > 0 ? r <= exitCoord : r >= exitCoord; r += dir) {
                path.add(r * cols + col);
            }
        }

        // identify direct blockers
        for (int cell : path) {
            if (occ.get(cell)) {
                int blocker = findVehicleAtCell(board, state, cell);
                if (blocker >= 0 && visited.add(blocker)) {
                    blockingCars.add(blocker);
                    evaluateBlocker(board, state, blocker, visited, blockingCars, occ);
                }
            }
        }
        return blockingCars.size();
    }

    private void evaluateBlocker(Board board,
                                 State state,
                                 int blockerIdx,
                                 Set<Integer> visited,
                                 Set<Integer> blockingCars,
                                 BitSet occ) {
        Vehicle blocker = board.getVehicle(blockerIdx);
        int base        = state.getPositions()[blockerIdx];
        int cols        = board.getCols();

        for (int dir : new int[]{ -1, +1 }) {
            int step     = blocker.isHorizontal() ? dir : dir * cols;
            int nextBase = base + step;

            while (isWithinBounds(nextBase, blocker, board)) {
                BitSet mask = board.getVehicleMask(blockerIdx, nextBase);
                if (mask == null) {
                    break;
                }
                if (mask.intersects(occ)) {
                    // whoever sits on the first overlapping cell
                    int frontMovement = nextBase;
                    if(blocker.isHorizontal()){
                        frontMovement = dir > 0 ? nextBase + blocker.length() - 1 : nextBase;
                    } else {
                        frontMovement = dir > 0 ? nextBase + (blocker.length() - 1) * cols : nextBase;
                    }
                    int other = findVehicleAtCell(board, state, frontMovement);
                    // System.out.println("current blocker: " + blocker.getSymbol() + ", cell: " + cell);
                    if(other != -1) {
                        Vehicle otherCar = board.getVehicle(other);
                        // System.out.println("blocked by: " + otherCar.getSymbol());
                    }
                    if (other >= 0 && !visited.contains(other) && visited.add(other)) {
                        blockingCars.add(other);
                        evaluateBlocker(board, state, other, visited, blockingCars, occ);
                    }
                }
                nextBase += step;
            }
        }
    }

    private boolean isWithinBounds(int base, Vehicle v, Board board) {
        int cols = board.getCols();
        int rows = board.getRows();
        int r = base / cols;
        int c = base % cols;
        return v.isHorizontal()
                ? (c >= 0 && c + v.length() <= cols)
                : (r >= 0 && r + v.length() <= rows);
    }

    private int findVehicleAtCell(Board board, State state, int cell) {
        int[] pos = state.getPositions();
        for (int i = 0; i < pos.length; i++) {
            BitSet m = board.getVehicleMask(i, pos[i]);
            if (m != null && m.get(cell)) return i;
        }
        return -1;
    }
}