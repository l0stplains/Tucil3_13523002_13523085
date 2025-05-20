package tucil_3_stima.strategy;

import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;

public class DistanceHeuristic implements Heuristic {
    @Override
    public int evaluate(Board board, State state) {
        BlockingHeuristic bh = new BlockingHeuristic();
        int blocking = bh.evaluate(board, state);

        // yes, only this
        // because we count any k displace as one movement
        if(blocking == 0)
            return 1;
        else
            return 0;
    }
}