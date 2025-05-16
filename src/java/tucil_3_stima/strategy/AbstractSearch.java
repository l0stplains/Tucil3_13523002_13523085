package tucil_3_stima.strategy;

import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;

import java.util.*;

public abstract class AbstractSearch implements SearchStrategy {
    @Override
    public State solve(Board board, State start, Heuristic heuristic) {
        PriorityQueue<State> open = new PriorityQueue<>(Comparator.comparingInt(this::priority));
        Map<String,Integer> best = new HashMap<>();
        start.setH(board.heuristic(start, heuristic));
        open.add(start); best.put(start.getKey(), 0);
        while (!open.isEmpty()) {
            State cur = open.poll();
            if (board.atExit(cur)) return cur;
            for (State nxt : board.neighbors(cur, heuristic)) {
                String k = nxt.getKey();
                int pg = best.getOrDefault(k,Integer.MAX_VALUE);
                if (nxt.getG() < pg) { best.put(k,nxt.getG()); open.add(nxt); }
            }
        }
        return null;
    }
    protected abstract int priority(State s);
}

