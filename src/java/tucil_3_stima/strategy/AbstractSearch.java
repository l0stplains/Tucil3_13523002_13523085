// AbstractSearch.java
package tucil_3_stima.strategy;

import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;

import java.util.*;

public abstract class AbstractSearch implements SearchStrategy {
    @Override
    public SearchResult solve(Board board, State start, Heuristic heuristic) {
        int nodesExpanded  = 0;
        int nodesGenerated = 0;
        int maxOpenSize    = 0;

        long t0 = System.nanoTime();

        PriorityQueue<State> open = new PriorityQueue<>(Comparator.comparingInt(this::priority));
        Map<State,Integer> best = new HashMap<>();

        start.setH(board.heuristic(start, heuristic));
        open.add(start);
        best.put(start, 0);
        maxOpenSize = 1;

        State goal = null;
        while (!open.isEmpty()) {
            State cur = open.poll();
            nodesExpanded++;

            if (board.atExit(cur)) {
                goal = cur;
                break;
            }

            for (State nxt : board.neighbors(cur, heuristic)) {
                nodesGenerated++;
                int prevG = best.getOrDefault(nxt, Integer.MAX_VALUE);
                if (nxt.getG() < prevG) {
                    best.put(nxt, nxt.getG());
                    open.add(nxt);
                    if (open.size() > maxOpenSize)
                        maxOpenSize = open.size();
                }
            }
        }

        long t1 = System.nanoTime();
        return new SearchResult(goal,
                nodesExpanded,
                nodesGenerated,
                maxOpenSize,
                t1 - t0);
    }

    protected abstract int priority(State s);
}
