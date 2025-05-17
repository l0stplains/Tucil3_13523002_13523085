package tucil_3_stima.strategy;

import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;

public interface SearchStrategy {
    SearchResult solve(Board board, State start, Heuristic heuristic);
}