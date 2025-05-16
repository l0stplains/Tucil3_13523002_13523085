package tucil_3_stima.strategy;

import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;

public interface Heuristic {
    int evaluate(Board board, State state);
}

