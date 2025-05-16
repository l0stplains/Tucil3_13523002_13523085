package tucil_3_stima.strategy;

import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;

import java.util.BitSet;

public class BlockingHeuristic implements Heuristic {
    @Override
    public int evaluate(Board board, State state) {
        BitSet occ = board.occupancy(state);
        int[] pos = state.getPositions();
        int count = 0;
        int base = pos[0];
        int cols = board.getCols();
        int r = base / cols;
        int c = base % cols + board.getVehicle(0).length();
        for (int x = c; x <= board.getExitCol(); x++) if (occ.get(r*cols + x)) count++;
        return count;
    }
}