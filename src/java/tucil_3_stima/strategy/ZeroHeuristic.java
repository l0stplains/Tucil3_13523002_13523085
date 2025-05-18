package tucil_3_stima.strategy;

import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;
import tucil_3_stima.model.Vehicle;

import java.util.BitSet;

public class ZeroHeuristic implements Heuristic {
    @Override
    public int evaluate(Board board, State state) {
        return 0;
    }
}