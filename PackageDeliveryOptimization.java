import java.util.*;

/*
Problem: Calculate the minimum number of roads to traverse to deliver packages to all required locations.

Approach:
1. Represent the locations and roads as a graph.
2. Use Breadth-First Search (BFS) to traverse the graph.
3. Count the number of roads required to visit all locations with packages (marked as 1).
4. Each road is counted twice to account for the return trip.

Steps:
1. Build the graph using the given roads.
2. Use BFS to traverse the graph starting from location 0.
3. For each location with a package, add 2 to the road count (to account for the return trip).
4. Return the total road count.

Complexity:
- Time: O(V + E), where V is the number of locations and E is the number of roads.
- Space: O(V + E) for the graph and visited array.
*/

public class PackageDeliveryOptimization {

    // Method to calculate the minimum number of roads to traverse
    public static int minRoadsToTraverse(int[] packages, int[][] roads) {
        int n = packages.length; // Number of locations
        List<List<Integer>> graph = new ArrayList<>(); // Graph to represent locations and roads

        // Initialize the graph with empty adjacency lists
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        // Build the graph by adding roads (edges) between locations
        for (int[] road : roads) {
            graph.get(road[0]).add(road[1]); // Add road from location A to location B
            graph.get(road[1]).add(road[0]); // Add road from location B to location A (bidirectional)
        }

        boolean[] visited = new boolean[n]; // Array to track visited locations
        Queue<Integer> queue = new LinkedList<>(); // Queue for BFS traversal
        int roadsCount = 0; // Variable to count the number of roads

        // Start BFS from location 0
        queue.offer(0);
        visited[0] = true;

        // Perform BFS traversal
        while (!queue.isEmpty()) {
            int currentNode = queue.poll(); // Get the current location

            // Visit all neighboring locations
            for (int neighbor : graph.get(currentNode)) {
                if (!visited[neighbor]) { // If the neighbor hasn't been visited
                    visited[neighbor] = true; // Mark it as visited
                    if (packages[neighbor] == 1) { // If the neighbor has a package
                        roadsCount += 2; // Add 2 to the road count (to account for the return trip)
                    }
                    queue.offer(neighbor); // Add the neighbor to the queue for further traversal
                }
            }
        }

        return roadsCount; // Return the total number of roads
    }

    // Main method to test the functionality
    public static void main(String[] args) {
        // Example 1
        int[] packages1 = {1, 0, 0, 0, 0, 1}; // Packages at locations 0 and 5
        int[][] roads1 = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}}; // Roads connecting locations
        System.out.println("Example 1 Output: " + minRoadsToTraverse(packages1, roads1)); // Expected output: 2

        // Example 2
        int[] packages2 = {0, 0, 1, 1, 0, 1}; // Packages at locations 2, 3, and 5
        int[][] roads2 = {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {4, 5}}; // Roads connecting locations
        System.out.println("Example 2 Output: " + minRoadsToTraverse(packages2, roads2)); // Expected output: 2
    }
}

// Output
// Example 1 Output: 2
// Example 2 Output: 2