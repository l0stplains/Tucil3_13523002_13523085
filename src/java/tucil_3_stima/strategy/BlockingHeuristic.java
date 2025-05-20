package tucil_3_stima.strategy;

import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;
import tucil_3_stima.model.Vehicle;

import java.util.BitSet;

public class BlockingHeuristic implements Heuristic {
    @Override
    public int evaluate(Board board, State state) {
        BitSet occ = board.occupancy(state);
        int[] pos = state.getPositions();
        int base = pos[0];
        Vehicle primary = board.getVehicle(0);
        int count = 0;

        int cols = board.getCols();
        int row = base / cols;
        int col = base % cols;

        int exitRow = board.getExitRow();
        int exitCol = board.getExitCol();

        if (primary.isHorizontal()) {
            if (exitCol > col) {
                for (int x = col + primary.length(); x <= exitCol; x++) {
                    if (occ.get(row * cols + x)) count++;
                }
            } else {
                for (int x = col - 1; x >= exitCol; x--) {
                    if (occ.get(row * cols + x)) count++;
                }
            }
        } else {
            if (exitRow > row) {
                for (int y = row + primary.length(); y <= exitRow; y++) {
                    if (occ.get(y * cols + col)) count++;
                }
            } else {
                for (int y = row - 1; y >= exitRow; y--) {
                    if (occ.get(y * cols + col)) count++;
                }
            }
        }

        return count;

    }
}