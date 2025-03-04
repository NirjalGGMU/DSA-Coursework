/*
Short Explanation:
Problem: Distribute candies to employees based on their ratings while following these rules:
- Every employee must receive at least one candy.
- Employees with higher ratings than their neighbors should receive more candies.

Approach:
# Perform two scans through the ratings:
1. Left-to-Right: Increase candies if an employee has a higher rating than the one before.
2. Right-to-Left: Ensure employees with higher ratings than the next one still receive more candies.
# The total number of candies required is the sum from both passes.

Complexity:
- Time: O(n), where n is the number of employees.
- Space: O(n), for storing the candies for each employee.
*/

public class EmployeeRewards { // Class to calculate the minimum candies for employees based on ratings

    // Method to calculate the minimum number of candies needed
    public int employee(int[] ratings) {
        int n = ratings.length; // Number of employees
        if (n == 0) return 0; // If there are no employees, return 0 candies

        // Step 1: Initialize an array to store candies for each employee
        int[] candies = new int[n];
        for (int i = 0; i < n; i++) {
            candies[i] = 1; // Every employee starts with at least 1 candy
        }

        // Step 2: Left-to-right pass
        // Give more candies to employees with higher ratings than their left neighbor
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) {
                candies[i] = candies[i - 1] + 1; // Assign one more candy than the left neighbor
            }
        }

        // Step 3: Right-to-left pass
        // Give more candies to employees with higher ratings than their right neighbor
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) {
                // Ensure the employee gets the maximum of their current candies and one more than the right neighbor
                candies[i] = Math.max(candies[i], candies[i + 1] + 1);
            }
        }

        // Step 4: Calculate the total number of candies required
        int totalCandies = 0;
        for (int candy : candies) {
            totalCandies += candy; // Sum up all the candies
        }

        return totalCandies; // Return the total number of candies needed
    }

    // Main method to test the functionality
    public static void main(String[] args) {
        EmployeeRewards solution = new EmployeeRewards(); // Create an instance of EmployeeRewards

        // Example 1: Employees with ratings [1, 0, 2]
        int[] ratings1 = {1, 0, 2};
        System.out.println(solution.employee(ratings1)); // Expected output: 5

        // Example 2: Employees with ratings [1, 2, 2]
        int[] ratings2 = {1, 2, 2};
        System.out.println(solution.employee(ratings2)); // Expected output: 4
    }
}

// Output
// 5
// 4