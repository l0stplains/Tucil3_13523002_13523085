package tucil_3_stima.test;

import tucil_3_stima.model.*;
import tucil_3_stima.strategy.*;
import java.util.*;

public class RushHourTest {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    public void Run() {
        // 6x6
        int rows=6, cols=6;

        // another test (simple)
        /*
        Vehicle[] vehicles = new Vehicle[] {
                new Vehicle(true, 2), // target car
                new Vehicle(false, 3),
                new Vehicle(true, 2),
                new Vehicle(false, 3),
                new Vehicle(false, 2),
        };
        int[] pos = {2*cols+0, 0*cols+2, 4*cols+2, 3*cols+4, 4*cols+1};
        */

        // normal
        /*
        Vehicle[] vehicles = new Vehicle[] {
                new Vehicle(true, 2), // target car
                new Vehicle(true, 3),
                new Vehicle(false, 2),
                new Vehicle(false, 3),
                new Vehicle(false, 2),
                new Vehicle(false, 3),
                new Vehicle(true, 2),
                new Vehicle(true, 2),
        };
        int[] pos = {2*cols+3, 0*cols+0, 1*cols+2, 3*cols+2, 3*cols+3, 0*cols+5, 5*cols+3, 3*cols+4};
         */
        // harder (argh)
        Vehicle[] vehicles = new Vehicle[] {
                new Vehicle(true, 2), // target car
                new Vehicle(false, 3),
                new Vehicle(true, 3),
                new Vehicle(false, 2),
                new Vehicle(false, 2),
                new Vehicle(true, 2),
                new Vehicle(true, 2),
                new Vehicle(true, 2),
                new Vehicle(true, 2),
                new Vehicle(false, 2),
                new Vehicle(false, 2),
                new Vehicle(false, 2),
                new Vehicle(false, 3),

        };
        int[] pos = {2*cols+3, 0*cols+0, 3*cols+0, 1*cols+1, 1*cols+2, 0*cols+1, 5*cols+0, 5*cols+3, 4*cols+4,
        4*cols+2, 3*cols+3, 0*cols+4, 1*cols+5};

        /*
        // TC bimo
        Vehicle[] vehicles = new Vehicle[] {
                new Vehicle(true, 2), // target car
                new Vehicle(true, 2),
                new Vehicle(false, 3),
                new Vehicle(false, 2),
                new Vehicle(true, 2),
                new Vehicle(false, 2),
                new Vehicle(false, 2),
                new Vehicle(true, 2),
                new Vehicle(true, 2),
                new Vehicle(false, 2),
                new Vehicle(false, 2),

        };
        int[] pos = {4*cols+4, 0*cols+2, 0*cols+5, 1*cols+4, 2*cols+2, 2*cols+0, 3*cols+1, 5*cols+0, 5*cols+4, 4*cols+2, 4*cols+3};
         */
        int exitR = 2, exitC = 5;
        Board board = new Board(rows,cols,exitR,exitC,vehicles);
        State start = new State(pos);

        render(board,start);

        SearchStrategy[] strategies = {new GBFS(),  new UCS(), new AStar() };
        Heuristic h = new BlockingHeuristic();
        System.out.println("Heuristic: Blocking");
        for (SearchStrategy s : strategies) {
            System.out.println("=== Testing " + s.getClass().getSimpleName() + " ===");

            SearchResult res = s.solve(board, start, h);

             printBoardSequence(board, res.getSolution());

            System.out.printf("Solution found : %s%n",
                    (res.getSolution() != null ? "YES" : "NO"));
            System.out.printf("Solution depth : %d moves%n",
                    res.getSolutionDepth());

            // Performance stats
            System.out.printf("Nodes expanded : %,d%n", res.getNodesExpanded());
            System.out.printf("Nodes generated: %,d%n", res.getNodesGenerated());
            System.out.printf("Max open size  : %,d%n", res.getMaxOpenSize());
            System.out.printf("Time elapsed   : %.2f ms%n", res.getDurationMillis());
        }
        System.out.println("\n\nHeuristic: distance");
        Heuristic dist = new DistanceHeuristic();
        for (SearchStrategy s : strategies) {
            System.out.println("=== Testing " + s.getClass().getSimpleName() + " ===");

            SearchResult res = s.solve(board, start, dist);

            // printBoardSequence(board, res.getSolution());

            System.out.printf("Solution found : %s%n",
                    (res.getSolution() != null ? "YES" : "NO"));
            System.out.printf("Solution depth : %d moves%n",
                    res.getSolutionDepth());

            // Performance stats
            System.out.printf("Nodes expanded : %,d%n", res.getNodesExpanded());
            System.out.printf("Nodes generated: %,d%n", res.getNodesGenerated());
            System.out.printf("Max open size  : %,d%n", res.getMaxOpenSize());
            System.out.printf("Time elapsed   : %.2f ms%n", res.getDurationMillis());
        }

    }

    private static void printBoardSequence(Board board, State end) {
        if (end == null) {
            return;
        }
        Deque<State> path = new ArrayDeque<>();
        for (State s = end; s != null; s = s.getParent()) path.addFirst(s);
        int step = 0;
        for (State s : path) {
            System.out.println("Step " + (step++) + ":");
            if(s.getLastMovement() != null) {
                int i = s.getLastMovement().getKey();
                System.out.println("Movement: " + (i == 0 ? 'X' : (char) ('A' + (i - 1))) + " " + s.getLastMovement().getValue());
            }
            render(board, s);
        }
    }

    private static void render(Board board, State state) {
        int rows = board.getRows(), cols = board.getCols();
        int[] pos = state.getPositions();
        char[][] grid = new char[rows][cols];
        for (char[] row : grid) Arrays.fill(row, '.');
        // Mark exit if within bounds
        if (board.getExitRow() >= 0 && board.getExitRow() < rows &&
                board.getExitCol() >= 0 && board.getExitCol() < cols) {
            grid[board.getExitRow()][board.getExitCol()] = '$';
        }
        // Place vehicles with bounds check
        for (int i = 0; i < pos.length; i++) {
            Vehicle v = board.getVehicle(i);
            int base = pos[i];
            int r = base / cols, c = base % cols;
            char ch = (i == 0 ? 'X' : (char) ('A' + (i - 1)));
            for (int d = 0; d < v.length(); d++) {
                int rr = v.isHorizontal() ? r : r + d;
                int cc = v.isHorizontal() ? c + d : c;
                if (rr >= 0 && rr < rows && cc >= 0 && cc < cols) {
                    grid[rr][cc] = ch;
                }
            }
        }
        // color always beautiful, still minimal
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char ch = grid[r][c];
                if (ch == 'X') System.out.print(RED + ch + RESET);
                else if (ch == '$') System.out.print(GREEN + ch + RESET);
                else System.out.print(ch);
                System.out.print(' ');
            }
            System.out.println();
        }
        System.out.println();
    }

}

