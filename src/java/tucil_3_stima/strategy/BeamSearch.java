// BeamSearch.java
package tucil_3_stima.strategy;

import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;

import java.util.*;

public class BeamSearch implements SearchStrategy {
    private final int beamWidth;

    public BeamSearch(int beamWidth) {
        if (beamWidth < 1) throw new IllegalArgumentException();
        this.beamWidth = beamWidth;
    }

    @Override
    public SearchResult solve(Board board, State start, Heuristic heuristic) {
        int nodesExpanded = 0, nodesGenerated = 0, maxOpenSize = 0;
        long t0 = System.nanoTime();

        start.setH(board.heuristic(start, heuristic));
        List<State> beam = List.of(start);
        Set<State> visited = new HashSet<>(beam);
        maxOpenSize = 1;
        State goal = null;

        while (!beam.isEmpty()) {
            // goal check
            for (State s : beam) {
                if (board.atExit(s)) {
                    goal = s;
                    break;
                }
            }
            if (goal != null) break;

            // expand
            List<State> candidates = new ArrayList<>();
            for (State s : beam) {
                nodesExpanded++;
                for (State nxt : board.neighbors(s, heuristic)) {
                    if (visited.add(nxt)) {
                        nodesGenerated++;
                        // incremental or fresh heuristic:
                        nxt.setH(board.heuristic(nxt, heuristic));
                        candidates.add(nxt);
                    }
                }
            }
            if (candidates.isEmpty()) break;

            // top-K selection via a fixed-size max-heap
            PriorityQueue<State> topK = new PriorityQueue<>(
                    beamWidth, Comparator.comparingInt(State::getF).reversed()
            );
            for (State c : candidates) {
                if (topK.size() < beamWidth) {
                    topK.add(c);
                } else if (c.getF() < topK.peek().getF()) {
                    topK.poll();
                    topK.add(c);
                }
            }
            beam = new ArrayList<>(topK);
            maxOpenSize = Math.max(maxOpenSize, beam.size());
        }

        long t1 = System.nanoTime();
        return new SearchResult(goal, nodesExpanded, nodesGenerated, maxOpenSize, t1 - t0);
    }
}
