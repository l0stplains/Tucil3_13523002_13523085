package tucil_3_stima.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.util.Pair;
import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;
import tucil_3_stima.model.Vehicle;

public class InputHandler {

    /**
     * Parse and validate inputs from the test case file.
     *
     * <p>Expected Format:</p>
     * <pre>
     * A B
     * N
     * [Vehicle definitions...]
     * </pre>
     *
     * <p>For example:</p>
     * <pre>
     * 6 6
     * 11
     * AAB..F
     * ..BCDF
     * GPPCDFK
     * GH.III
     * GHJ...
     * LLJMM.
     * </pre>
     *
     * @param filename the test case file name
     * @return a pair of the board and the state detected
     * @throws IOException if a file I/O error occurs or the input is malformed
     */
    public static Pair<Board, State> inputTestCaseFromFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filename);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Parse the first line: board dimensions.
            String[] variables = reader.readLine().split(" ");
            if (variables.length != 2) {
                throw new IllegalArgumentException("Wrong number of variables on the first line of file: " + filename);
            }
            int A, B;
            try {
                A = Integer.parseInt(variables[0]);
                B = Integer.parseInt(variables[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Variable not parseable on the first line of file: " + filename);
            }
            // Validate that the number of blocks read matches P, just in case.
            if (A < 1 || B < 1) {
                throw new IllegalArgumentException("Invalid variable value on the first line of file: " + filename);
            }

            int N;
            String variable = reader.readLine();
            try {       
                N = Integer.parseInt(variable);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Variable not parseable on the Second line of file: " + filename);
            }
            // Validate that the number of blocks read matches P, just in case.
            if (N < 1) {
                throw new IllegalArgumentException("Invalid variable value on the Second line of file: " + filename);
            }

            // Read all remaining non-empty lines.
            int exitR = -1;
            int exitC = -1;
            List<String> allLines = new ArrayList<>();
            List<String> allLinesRaw = new ArrayList<>();
            String line;
            String lineTrim;
            int row = 0;

            // Parse the board itself and find K
            while ((line = reader.readLine()) != null) {
                lineTrim = line.trim();

                if (lineTrim.isBlank() && row < A) { 
                    throw new IllegalArgumentException("Empty line on the board of file: " + filename);
                }

                if (row <= A) {
                    // check if lastrow exist even though exit already found
                    if (row == A && exitC != -1 && exitR != -1 && !line.isBlank()) {
                        throw new IllegalArgumentException("(4) Board size and the given board size doesn't match at file: " + filename);
                    }

                    // normal
                    if (lineTrim.length() == B) {
                        allLines.add(lineTrim);
                        allLinesRaw.add(line);
                    }
                    // K inline
                    else if (lineTrim.length() == B + 1) {
                        // K on the left side
                        if (line.charAt(0) == 'K') {
                            if (exitR != -1 && exitC != -1) {
                                throw new IllegalArgumentException("Duplicate exit on the board of file: " + filename);
                            }
                            exitR = row;
                            exitC = 0;

                            allLines.add(lineTrim.substring(1, B + 1));
                            allLinesRaw.add(line);
                        }
                        // K on the right side
                        else if (line.charAt(line.length() - 1) == 'K') {
                            if (exitR != -1 && exitC != -1) {
                                throw new IllegalArgumentException("Duplicate exit on the board of file: " + filename);
                            }

                            exitR = row;
                            exitC = line.length() - 2;

                            allLines.add(lineTrim.substring(0, B));
                            allLinesRaw.add(line);
                        }
                        else {
                            throw new IllegalArgumentException("(1) Board size and the given board size doesn't match at file: " + filename);
                        }

                    }
                    // K Alone
                    else if (lineTrim.length() == 1) {
                        // Not in board vertical range
                        if (row != 0 && row != A) {
                            throw new IllegalArgumentException("Invalid exit position on the board of file: " + filename);
                        }
                        if (lineTrim.charAt(0) == 'K') {
                            if (exitR != -1 && exitC != -1) {
                                throw new IllegalArgumentException("Duplicate exit on the board of file: " + filename);
                            }

                            int col = line.indexOf('K');

                            // Not in board horizontal range
                            if (col < 0 || col > B - 1) {
                                throw new IllegalArgumentException("Invalid exit position on the board of file: " + filename);
                            }

                            exitR = row;
                            exitC = col;

                            if (row == A){
                                exitR--;
                            }
                            if (row == 0) {
                                row--;
                            }
                        }
                        else {
                            throw new IllegalArgumentException("(5) Board size and the given board size doesn't match at file: " + filename);
                        }
                    }
                    else {
                        System.out.println("at Row: " + row + "\nline: " + line + "\nlineTrim: " + lineTrim);
                        throw new IllegalArgumentException("(2) Board size and the given board size doesn't match at file: " + filename);
                    }
                }
                else if (row > A && line.isBlank()) continue;
                else {
                    throw new IllegalArgumentException("(3) Board size and the given board size doesn't match at file: " + filename);
                }

                row++;
            }

            if (exitR == -1 || exitC == -1) {
                throw new IllegalArgumentException("No exit found on the board of file: " + filename);
            }
            
            // Parse the vehicles
            List<Vehicle> vehicles = new ArrayList<>();
            List<Vehicle> potentialRiskyVehicles = new ArrayList<>();
            List<Integer> pos = new ArrayList<>();
            List<Integer> potentialRiskyPos = new ArrayList<>();
            Set<Character> uniqueSymbols = new HashSet<>();
            boolean[][] visited = new boolean[A][B];
            
            System.out.println("Board: ");
            for (String line1 : allLines) {
                System.out.println(line1);
            }

            // Asumsi udh size AxB (harusnya udh sih, di verif di bagian sebelumnya)
            for (int i = 0; i < A; i++) {
                for (int j = 0; j < B; j++) {
                    char c = allLines.get(i).charAt(j);
                    
                    if (validSymbol(c)) {
                        if (visited[i][j] || c == '.') continue;

                        // duplicate symbol
                        if (!uniqueSymbols.add(c)) {
                            throw new IllegalArgumentException("Duplicate symbol found: " + c);
                        }

                        System.out.println("Current symbol: " + c + "\nfound at (" + i + ", " + j + ")");

                        // traverse vehicle
                        // make sure its a valid 1xN or Nx1
                        int vehicleLength = 0;
                        boolean isHorizontal = false;

                        
                        // check if size is 1x1 and get vehicle orientation
                        if (j + 1 < B && allLines.get(i).charAt(j + 1) == c) isHorizontal = true;
                        else if (i + 1 < A && allLines.get(i + 1).charAt(j) == c) isHorizontal = false;
                        else {
                            throw new IllegalArgumentException("1x1 sized vehicle found at file: " + filename);
                        }
                        
                        // cmiiw but im pretty sure i just need to check to the right and below
                        int cur_i = i;
                        int cur_j = j;
                        while (cur_i < A && cur_j < B && allLines.get(cur_i).charAt(cur_j) == c) {
                            visited[cur_i][cur_j] = true;
                            vehicleLength++;

                            if (isHorizontal) {
                                // check if not 1XN
                                if ((cur_i - 1 >= 0 && allLines.get(cur_i - 1).charAt(cur_j) == c) ||
                                (cur_i + 1 < A && allLines.get(cur_i + 1).charAt(cur_j) == c)
                                ) {
                                    throw new IllegalArgumentException("Invalid vehicle size found at file: " + filename);
                                }
                                
                                cur_j++;
                            }
                            else {
                                // check if not Nx1
                                if ((cur_j - 1 >= 0 && allLines.get(cur_i).charAt(cur_j - 1) == c) ||
                                (cur_j + 1 < A && allLines.get(cur_i).charAt(cur_j + 1) == c)
                                ) {
                                    throw new IllegalArgumentException("Invalid vehicle size found at file: " + filename);
                                }

                                cur_i++;
                            }
                            System.out.println("cur_i: " + cur_i + "\ncur_j: " + cur_j + "\n---------");
                        }

                        if (c == 'P') {
                            // check if exit in parallel to the player car
                            if ((isHorizontal && exitR != i) || (!isHorizontal && exitC != j)) {
                                throw new IllegalArgumentException("Vehicle and exit not inline to each other at file: " + filename);
                            }

                            vehicles.add(0, new Vehicle(isHorizontal, vehicleLength));
                            pos.add(0, i * B + j);
                        }                         
                        else {
                            Vehicle v = new Vehicle(isHorizontal, vehicleLength);
                            
                            // may be an unsolvable obstacle to the exit
                            if ((isHorizontal && exitR == i) || (!isHorizontal && exitC == j)) {
                                potentialRiskyVehicles.add(v);
                                potentialRiskyPos.add(i * B + j);
                            }

                            vehicles.add(v);
                            pos.add(i * B + j);
                        } 
                    }
                    else {
                        throw new IllegalArgumentException("Invalid symbol on the board of file: " + filename);
                    }
                }
            }

            // check if the board is aligned to left (no empty space to the left) except if K on the left
            if (!(exitC == 0 && vehicles.get(0).isHorizontal())) {
                for (String lineTest : allLinesRaw) {
                    if (!validSymbol(lineTest.charAt(0))) {
                        throw new IllegalArgumentException("Empty space to left side of the board of file: " + filename);
                    }
                }
            }
            // if K is on the left 
            else {
                for (String lineTest : allLinesRaw) {
                    if (!validSymbol(lineTest.charAt(1))) {
                        throw new IllegalArgumentException("Empty space to left side of the board of file: " + filename);
                    }
                }
            }

            // check if there is another vehicle between the player's and exit 
            // that has the same orientation to the player's
            int playerPos = pos.get(0); 
            int playerR = playerPos / A;
            int playerC = playerPos % B;
            int lbound;
            int rbound;
            boolean carOrientation = vehicles.get(0).isHorizontal();

            // get left and right bound
            if (carOrientation) {
                if (exitC > playerC) {
                    lbound = playerC;
                    rbound = exitC;
                }
                else {
                    lbound = exitC;
                    rbound = playerC;
                }
            }
            else {
                if (exitR > playerR) {
                    lbound = playerR;
                    rbound = exitR;
                }
                else {
                    lbound = exitR;
                    rbound = playerR;
                }
            }

            for (int carPos : potentialRiskyPos) {
                int carBound;
                if (carOrientation) carBound = carPos % B;
                else carBound = carPos / A;

                if (carBound > lbound && carBound < rbound) {
                    throw new IllegalArgumentException("Unsolvable board of file: " + filename);
                }
            }

            // check if amount of vehicles the same as the given N 
            if (N != vehicles.size() - 1) {
                throw new IllegalArgumentException("Number of vehicles is not the same as the given N at file: " + filename);
            }


            Vehicle[] vehiclesArr = new Vehicle[vehicles.size()];
            vehiclesArr = vehicles.toArray(vehiclesArr);
            int[] posArr = pos.stream().mapToInt(i -> i).toArray();

            System.out.println("Done Parsing");
            return new Pair<>(new Board(A, B, exitR, exitC, vehiclesArr), new State(posArr));
        } catch (IOException e) {
            throw new IOException("Error reading file: " + filename, e);
        }
    }

    private static boolean validSymbol(char c) {
        return c == '.' || (c >= 'A' && c <= 'Z' && c != 'K');
    }
}