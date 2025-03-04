/*
Task: Find and show the top 3 hashtags from tweets, sorted by how often they appear.

Steps:

1. Store tweets in a list of maps (user_id, tweet_id, tweet, tweet_date).
2. Pick out hashtags from tweets and count how many times each appears using a HashMap.
3. Sort hashtags by count (highest first). If two hashtags have the same count, sort them alphabetically.
4. Show the top 3 hashtags in a table format.

How it works:

- Finding Hashtags: Look for words starting with #.
- Counting: Keep track of how many times each hashtag appears.
- Sorting: First by count (highest to lowest), then by name (A to Z).
- Showing: Print the top 3 hashtags in a table.

Efficiency:
- Time: O(n * m), where n = number of tweets, m = words per tweet.
- Space: O(k), where k = unique hashtags.
*/

import java.util.*; // Bring in Java utilities like List and Map for handling tweets.

public class KthSmallestProduct { // Class to find the kth smallest product of two arrays
    private int[] nums1; // Array to store the first set of numbers
    private int[] nums2; // Array to store the second set of numbers

    // Method to find the kth smallest product of pairs from nums1 and nums2
    public long kthSmallestProduct(int[] nums1, int[] nums2, long k) {
        this.nums1 = nums1; // Assign nums1 to the class-level variable
        this.nums2 = nums2; // Assign nums2 to the class-level variable

        int m = nums1.length; // Length of nums1
        int n = nums2.length; // Length of nums2

        // Find the maximum absolute values in nums1 and nums2
        int a = Math.max(Math.abs(nums1[0]), Math.abs(nums1[m - 1]));
        int b = Math.max(Math.abs(nums2[0]), Math.abs(nums2[n - 1]));

        // Set the search range for binary search
        long r = (long) a * b;  // Upper bound for the product
        long l = (long) -a * b; // Lower bound for the product

        // Perform binary search to find the kth smallest product
        while (l < r) {
            long mid = (l + r) >> 1;  // Calculate the midpoint of the current range
            if (count(mid) >= k) { // If the number of products <= mid is >= k
                r = mid; // Narrow the search to the lower half
            } else {
                l = mid + 1; // Narrow the search to the upper half
            }
        }

        return l; // Return the kth smallest product
    }

    // Helper method to count the number of products <= p
    private long count(long p) {
        long cnt = 0; // Counter for valid products
        int n = nums2.length; // Length of nums2

        // Iterate through each element in nums1
        for (int x : nums1) {
            if (x > 0) { // If x is positive
                int l = 0, r = n;
                while (l < r) {
                    int mid = (l + r) >> 1; // Find the middle index of nums2
                    if ((long) x * nums2[mid] > p) { // If the product is greater than p
                        r = mid; // Narrow the search to the left side
                    } else {
                        l = mid + 1; // Narrow the search to the right side
                    }
                }
                cnt += l; // Add the count of valid products for positive x
            } else if (x < 0) { // If x is negative
                int l = 0, r = n;
                while (l < r) {
                    int mid = (l + r) >> 1; // Find the middle index of nums2
                    if ((long) x * nums2[mid] <= p) { // If the product is <= p
                        r = mid; // Narrow the search to the left side
                    } else {
                        l = mid + 1; // Narrow the search to the right side
                    }
                }
                cnt += n - l; // Add the count of valid products for negative x
            } else if (p >= 0) { // If x is 0 and p >= 0
                cnt += n; // All elements in nums2 contribute to valid products
            }
        }

        return cnt; // Return the total count of valid products
    }

    // Main method to test the kthSmallestProduct function
    public static void main(String[] args) {
        KthSmallestProduct kthSmallestProduct = new KthSmallestProduct(); // Create an instance of the class

        // Test Case 1
        System.out.println(kthSmallestProduct.kthSmallestProduct(new int[]{2, 5}, new int[]{3, 4}, 2)); // Expected output: 8

        // Test Case 2
        System.out.println(kthSmallestProduct.kthSmallestProduct(new int[]{-4, -2, 0, 3}, new int[]{2, 4}, 6)); // Expected output: 0
    }
}

// Output
// 8
// 0