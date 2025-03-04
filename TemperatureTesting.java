public class TemperatureTesting { // Class to determine the minimum tests needed for temperature levels

    // Method to compute the least number of tests required
    public static int minMeasurements(int k, int n) {
        // If there's only one sample (k = 1), each temperature must be checked one by one.
        if (k == 1) return n;

        // Initialize a DP table where dp[i][j] represents the highest number of temperature
        // levels that can be examined using 'i' samples and 'j' tests.
        int[][] dp = new int[k + 1][n + 1];

        // Counter to track the number of tests performed
        int moves = 0;

        // Continue testing until we can determine at least 'n' temperature levels
        while (dp[k][moves] < n) {
            moves++; // Increase the test count

            // Iterate over each sample count from 1 to k
            for (int i = 1; i <= k; i++) {
                // DP formula:
                // The maximum number of temperature levels we can test is derived from:
                // - The current test being performed
                // - The number of levels testable with one fewer sample (dp[i - 1][moves - 1])
                // - The number of levels testable with the same samples but one less test (dp[i][moves - 1])
                dp[i][moves] = 1 + dp[i - 1][moves - 1] + dp[i][moves - 1];
            }
        }

        // Return the minimum number of tests required
        return moves;
    }

    // Main method to run test cases
    public static void main(String[] args) {
        System.out.println(minMeasurements(1, 2));  // Expected output: 2
        System.out.println(minMeasurements(2, 6));  // Expected output: 3
        System.out.println(minMeasurements(3, 14)); // Expected output: 4
    }
}
