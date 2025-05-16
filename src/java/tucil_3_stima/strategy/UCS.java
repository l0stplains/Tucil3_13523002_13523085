package tucil_3_stima.strategy;

import tucil_3_stima.model.State;

public class UCS extends AbstractSearch {
    @Override protected int priority(State s) { return s.getG(); }
}

