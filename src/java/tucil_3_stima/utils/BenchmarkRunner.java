package tucil_3_stima.utils;

import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;
import tucil_3_stima.strategy.*;
import javafx.util.Pair;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class BenchmarkRunner {
    public static void run(String folderPath, String outputFile) {
        File folder = new File(folderPath);
        if (!folder.isDirectory()) {
            System.err.println("Error: " + folderPath + " is not a valid directory");
            System.exit(1);
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.err.println("Error: No .txt files found in " + folderPath);
            System.exit(1);
        }

        Arrays.sort(files);
        List<Pair<String, SearchStrategy>> strategies = List.of(
                new Pair<>("UCS", new UCS()),
                new Pair<>("GBFS", new GBFS()),
                new Pair<>("AStar", new AStar()),
                new Pair<>("BeamSearch", new BeamSearch(100))
        );

        List<Pair<String, Heuristic>> heuristics = List.of(
                new Pair<>("ZeroHeuristic", new ZeroHeuristic()),
                new Pair<>("DistanceHeuristic", new DistanceHeuristic()),
                new Pair<>("BlockingHeuristic", new BlockingHeuristic()),
                new Pair<>("RecursiveBlockingHeuristic", new RecursiveBlockingHeuristic())
        );

        StringBuilder csv = new StringBuilder();
        csv.append("Filename,Strategy,Heuristic,Solution Depth,Nodes Expanded,Nodes Generated,Max Open Size,Duration (ms),Has Solution\n");

        for (File file : files) {
            String filename = file.getName();
            System.out.println("Processing: " + filename);
            try {
                Pair<Board, State> input = InputHandler.inputTestCaseFromFile(file.getAbsolutePath());
                Board board = input.getKey();
                State state = input.getValue();

                for (var strat : strategies) {
                    for (var heur : heuristics) {
                        try {
                            var res = strat.getValue().solve(board, state, heur.getValue());
                            boolean solved = res.getSolutionDepth() != -1;
                            csv.append(formatCsv(filename, strat.getKey(), heur.getKey(), res, solved));
                        } catch (Exception e) {
                            csv.append(filename).append(",").append(strat.getKey()).append(",").append(heur.getKey())
                                    .append(",ERROR,,,,No\n");
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to process " + filename + ": " + e.getMessage());
            }
        }

        if (outputFile != null) {
            try {
                // Ensure directory exists
                Path outputPath = Paths.get(outputFile);
                if (outputPath.getParent() != null) {
                    Files.createDirectories(outputPath.getParent());
                }

                // Write the CSV content
                try (FileWriter writer = new FileWriter(outputFile)) {
                    writer.write(csv.toString());
                }
                System.out.println("Results written to: " + outputFile);
            } catch (IOException e) {
                System.err.println("Error writing output: " + e.getMessage());
            }
        } else {
            System.out.println(csv.toString());
        }
    }

    private static String formatCsv(String file, String strat, String heur, SearchResult res, boolean solved) {
        return String.join(",",
                file,
                strat,
                heur,
                Integer.toString(res.getSolutionDepth()),
                Long.toString(res.getNodesExpanded()),
                Long.toString(res.getNodesGenerated()),
                Integer.toString(res.getMaxOpenSize()),
                String.format("%.3f", res.getDurationMillis()),
                solved ? "Yes" : "No"
        ) + "\n";
    }
}
