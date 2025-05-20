package tucil_3_stima.strategy;

import tucil_3_stima.model.State;

import java.util.Map;

public class AStar extends AbstractSearch {
    @Override protected int priority(State s) { return s.getF(); }

    @Override
    protected boolean shouldAdd(State cur, State nxt, Map<State, Integer> best) {
        return nxt.getG() < best.getOrDefault(nxt, Integer.MAX_VALUE);
    }
}
