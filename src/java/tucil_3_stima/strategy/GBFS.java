package tucil_3_stima.strategy;

import tucil_3_stima.model.State;

import java.util.Map;

public class GBFS extends AbstractSearch {
    @Override protected int priority(State s) { return s.getH(); }

    @Override
    protected boolean shouldAdd(State cur, State nxt, Map<State, Integer> best) {
        return !best.containsKey(nxt);
    }
}
