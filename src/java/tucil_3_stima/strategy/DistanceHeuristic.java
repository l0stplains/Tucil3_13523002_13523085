package tucil_3_stima.strategy;

import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;
import tucil_3_stima.model.Vehicle;

import java.util.BitSet;

public class DistanceHeuristic implements Heuristic {
    @Override
    public int evaluate(Board board, State state) {
        int[] pos = state.getPositions();
        int base = pos[0];
        Vehicle red = board.getVehicle(0);

        int cols = board.getCols();
        int row = base / cols;
        int col = base % cols;

        int exitRow = board.getExitRow();
        int exitCol = board.getExitCol();

        if (red.isHorizontal()) {
            if (exitCol > col) {
                return exitCol - (col + red.length()) + 1;
            } else {
                return col - exitCol;
            }
        } else {
            if (exitRow > row) {
                return exitRow - (row + red.length()) + 1;
            } else {
                return row - exitRow;
            }
        }
    }
}