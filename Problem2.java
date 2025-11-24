import java.util.Random;
import java.util.Scanner;

public class Problem2 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Problem 2: Largest Zero Sub-matrix");
        System.out.println("----------------------------------");
        System.out.println("Note: To match 'Part A' of the report, use seed 123.");
        System.out.println("Note: To match 'Part B' of the report, use seed 42.");
        System.out.print("Enter random seed: ");
        
        long seed;
        try {
            seed = scanner.nextLong();
        } catch (Exception e) {
            System.out.println("Invalid input. Defaulting to seed 42.");
            seed = 42;
        }

        // Part A: Verification 
        System.out.println("\n=== PART A: Verification Run (15x15 Matrix) ===");
        runVerificationDemo(seed);

        System.out.println("\n");

        // Part B: Experiments 
        System.out.println("=== PART B: Performance Experiments ===");
        runExperiments(seed);
    }

    public static void runVerificationDemo(long seed) {
        Random rand = new Random(seed); 
        int rows = 15;
        int cols = 15;
        byte[][] matrix = generateRandomMatrix(rows, cols, rand);

        System.out.println("Generated Matrix (0=Target, 1=Obstacle):");
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                System.out.print(matrix[r][c] + " ");
            }
            System.out.println();
        }

        solveLargestZeroSubmatrix(matrix, rows, cols, true); 
    }

    public static void runExperiments(long seed) {
        System.out.println("Dataset | Matrix Size | Max Square | Time (ns)   | Memory (bytes)");
        System.out.println("-----------------------------------------------------------------");

        int[][] datasets = {
            {10, 10},
            {10, 100},
            {10, 1000},
            {100, 1000},
            {1000, 1000}
        };

        Random rand = new Random(seed);

        for (int i = 0; i < datasets.length; i++) {
            int rows = datasets[i][0];
            int cols = datasets[i][1];

            byte[][] matrix = generateRandomMatrix(rows, cols, rand);

            System.gc();
            
            long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long startTime = System.nanoTime();

            int maxSquare = solveLargestZeroSubmatrix(matrix, rows, cols, false);

            long endTime = System.nanoTime();
            long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            long duration = endTime - startTime;
            long memoryUsed = Math.max(0, endMemory - startMemory);

            System.out.printf("%-7d | %4d x %-4d | %-10d | %-11d | %-14d%n", 
                (i + 1), rows, cols, maxSquare, duration, memoryUsed);
        }
    }

    public static int solveLargestZeroSubmatrix(byte[][] matrix, int rows, int cols, boolean printDetails) {
        byte[][] dp = new byte[rows][cols];

        int maxSide = 0;
        int endR = -1;
        int endC = -1;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == 1) {
                    dp[i][j] = 0;
                } else {
                    if (i == 0 || j == 0) {
                        dp[i][j] = 1; 
                    } else {
                        int left = dp[i][j - 1];
                        int up = dp[i - 1][j];
                        int diag = dp[i - 1][j - 1];

                        int minVal = left;
                        if (up < minVal) minVal = up;
                        if (diag < minVal) minVal = diag;
                        
                        if (minVal + 1 > 127) dp[i][j] = 127; 
                        else dp[i][j] = (byte) (minVal + 1);
                    }
                }

                if (dp[i][j] > maxSide) {
                    maxSide = dp[i][j];
                    endR = i;
                    endC = j;
                }
            }
        }

        if (printDetails) {
            System.out.println("--- Solution Extraction ---");
            if (maxSide == 0) {
                System.out.println("No zero sub-matrix found.");
            } else {
                int startR = endR - maxSide + 1;
                int startC = endC - maxSide + 1;
                System.out.println("Max Square Size: " + maxSide);
                System.out.println("Location: Row " + startR + " to " + endR + 
                                   ", Col " + startC + " to " + endC);
            }
        }
        
        return maxSide;
    }

    public static byte[][] generateRandomMatrix(int rows, int cols, Random rand) {
        byte[][] m = new byte[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                m[r][c] = (byte) rand.nextInt(2); 
            }
        }
        return m;
    }
}