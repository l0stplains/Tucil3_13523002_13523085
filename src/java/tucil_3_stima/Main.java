package tucil_3_stima;

import tucil_3_stima.gui.MainApp;
import tucil_3_stima.utils.BenchmarkRunner;

public class Main {
    public static void main(String[] args) {
        boolean benchmarkMode = false;
        String inputFolder = null;
        String outputFile = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-b":
                    benchmarkMode = true;
                    if (i + 1 < args.length) {
                        inputFolder = args[++i];
                    } else {
                        System.err.println("Error: Missing input folder after -b");
                        System.exit(1);
                    }
                    break;
                case "-o":
                    if (i + 1 < args.length) {
                        outputFile = args[++i];
                        if (!outputFile.toLowerCase().endsWith(".csv")) {
                            System.err.println("Error: Output file must have .csv extension");
                            System.exit(1);
                        }
                    } else {
                        System.err.println("Error: Missing output file after -o");
                        System.exit(1);
                    }
                    break;
                default:
                    // ignore unrecognized flags
                    break;
            }
        }

        if (benchmarkMode) {
            if (inputFolder == null) {
                System.err.println("Error: No input folder specified for benchmarking");
                System.exit(1);
            }
            BenchmarkRunner.run(inputFolder, outputFile);
        } else {
            MainApp.show();
        }
    }
}