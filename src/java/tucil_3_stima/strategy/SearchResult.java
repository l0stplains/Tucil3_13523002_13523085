package tucil_3_stima.strategy;

import tucil_3_stima.model.State;

public class SearchResult {
    private final State solution;
    private final int   nodesExpanded;
    private final int   nodesGenerated;
    private final int   maxOpenSize;
    private final long  durationNanos;
    private final int solutionDepth;

    public SearchResult(State solution,
                        int nodesExpanded,
                        int nodesGenerated,
                        int maxOpenSize,
                        long durationNanos) {
        this.solution       = solution;
        this.nodesExpanded  = nodesExpanded;
        this.nodesGenerated = nodesGenerated;
        this.maxOpenSize    = maxOpenSize;
        this.durationNanos  = durationNanos;
        this.solutionDepth  = solution  != null ? solution.getG() : -1;
    }

    public State getSolution()       { return solution; }
    public int   getNodesExpanded()  { return nodesExpanded; }
    public int   getNodesGenerated() { return nodesGenerated; }
    public int   getMaxOpenSize()    { return maxOpenSize; }
    public long  getDurationNanos()  { return durationNanos; }
    public double getDurationMillis(){ return durationNanos / 1_000_000.0; }
    public int getSolutionDepth() { return solutionDepth; }
}
