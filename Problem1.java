import java.util.Random;
import java.util.Scanner;

public class Problem1 {

    // English Letter Frequencies (A-Z) in percent
    public static final double[] ENGLISH_FREQUENCIES = {
        8.17, 1.49, 2.78, 4.25, 12.70, 2.23, 2.02, 6.09, 6.97, 0.15, // A-J
        0.77, 4.03, 2.41, 6.75, 7.51, 1.93, 0.10, 5.99, 6.33, 9.06,  // K-T
        2.76, 0.98, 2.36, 0.15, 1.97, 0.07                           // U-Z
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Problem 1: Weighted Approximate Common Substring");
        System.out.println("------------------------------------------------");
        System.out.print("Enter random seed (Use 42 to match Report): ");
        
        long seed;
        try {
            seed = scanner.nextLong();
        } catch (Exception e) {
            System.out.println("Invalid input. Defaulting to seed 42.");
            seed = 42;
        }

        runExperiments(seed);
    }

    public static void runExperiments(long seed) {
        Random rand = new Random(seed); 

        // ---------------------------------------------------------
        // SCENARIO 1: W = 1, Penalty = 10
        // ---------------------------------------------------------
        System.out.println("========================================");
        System.out.println("SCENARIO 1: Weight=1, Penalty=10");
        System.out.println("========================================");

        double[] constantWeights = new double[26];
        for (int i = 0; i < 26; i++) constantWeights[i] = 1.0;
        
        String s1 = generateUniformString(100, rand);
        String s2 = generateUniformString(100, rand);
        
        System.out.println("String 1: " + s1);
        System.out.println("String 2: " + s2);
        System.out.println("----------------------------------------");
        
        solveWeightedCommonSubstring(s1.toCharArray(), s2.toCharArray(), constantWeights, 10.0);

        // ---------------------------------------------------------
        // SCENARIO 2: W ~ English Freq, Penalty varies
        // ---------------------------------------------------------
        System.out.println("\n========================================");
        System.out.println("SCENARIO 2: W ~ Frequency, Variable Penalty");
        System.out.println("========================================");

        s1 = generateEnglishLikeString(200, rand);
        s2 = generateEnglishLikeString(200, rand);
        
        System.out.println("String 1: " + s1);
        System.out.println("String 2: " + s2);

        double[] freqWeights = ENGLISH_FREQUENCIES; 
        
        double minW = Double.MAX_VALUE;
        double maxW = Double.MIN_VALUE;
        for (double w : freqWeights) {
            if (w < minW) minW = w;
            if (w > maxW) maxW = w;
        }

        System.out.printf("Weights Range: [%.2f, %.2f]%n", minW, maxW);

        double stepSize = (maxW - minW) / 9.0; 
        
        for (int i = 0; i < 10; i++) {
            double currentPenalty = minW + (i * stepSize);
            
            System.out.println("\n--- Experiment " + (i + 1) + "/10 ---");
            System.out.printf("Penalty: %.4f%n", currentPenalty);
            
            solveWeightedCommonSubstring(s1.toCharArray(), s2.toCharArray(), freqWeights, currentPenalty);
        }
    }

    public static void solveWeightedCommonSubstring(char[] s1, char[] s2, double[] weights, double penalty) {
        int m = s1.length;
        int n = s2.length;
        double[][] dp = new double[m + 1][n + 1];

        double maxScore = 0.0;
        int endI = 0; 
        int endJ = 0; 

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char c1 = s1[i - 1];
                char c2 = s2[j - 1];

                double scoreStep;
                if (c1 == c2) {
                    int idx = c1 - 'A';
                    if (idx >= 0 && idx < 26) scoreStep = weights[idx];
                    else scoreStep = 0; 
                } else {
                    scoreStep = -penalty; 
                }

                double potential = dp[i - 1][j - 1] + scoreStep;

                if (potential > 0) dp[i][j] = potential;
                else dp[i][j] = 0.0;

                if (dp[i][j] > maxScore) {
                    maxScore = dp[i][j];
                    endI = i;
                    endJ = j;
                }
            }
        }

        char[] buffer = new char[Math.max(m, n)];
        int len = 0;
        int currI = endI;
        int currJ = endJ;

        while (currI > 0 && currJ > 0 && dp[currI][currJ] > 0) {
            buffer[len++] = s1[currI - 1];
            currI--;
            currJ--;
        }

        int startI = currI + 1;
        int startJ = currJ + 1;

        System.out.printf("Max Score: %.4f%n", maxScore);
        System.out.printf("Position in S1: %d to %d%n", startI, endI);
        System.out.printf("Position in S2: %d to %d%n", startJ, endJ);
        System.out.print("Substring: ");
        
        if (len == 0) {
            System.out.println("(None)");
        } else {
            for (int k = len - 1; k >= 0; k--) {
                System.out.print(buffer[k]);
            }
            System.out.println();
        }
    }

    public static String generateUniformString(int length, Random rand) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = (char) ('A' + rand.nextInt(26));
        }
        return new String(text);
    }

    public static String generateEnglishLikeString(int length, Random rand) {
        char[] text = new char[length];
        double[] cumulative = new double[26];
        double sum = 0;
        for (int i = 0; i < 26; i++) {
            sum += ENGLISH_FREQUENCIES[i];
            cumulative[i] = sum;
        }

        for (int i = 0; i < length; i++) {
            double r = rand.nextDouble() * sum;
            for (int k = 0; k < 26; k++) {
                if (r <= cumulative[k]) {
                    text[i] = (char) ('A' + k);
                    break;
                }
            }
        }
        return new String(text);
    }
}