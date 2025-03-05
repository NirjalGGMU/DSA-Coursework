// QN.3a - DeviceConnectionCostCalculator

/*
 * This program calculates the minimum cost required to connect all devices in a network.
 * It uses Kruskal’s Minimum Spanning Tree (MST) algorithm to find the lowest cost connections between devices.
 * Additionally, it considers the cheapest module cost required for operation.
 * 
 * Algorithm:
 * 1. Convert the given connections into a list of edges.
 * 2. Sort the edges in ascending order based on their cost.
 * 3. Use the Union-Find data structure to apply Kruskal’s algorithm:
 *    - Iterate through sorted edges and add them to the MST if they connect different sets.
 * 4. Compute the total cost of the MST.
 * 5. Add the cost of the cheapest module.
 * 6. Return the minimum total cost.
 */


import java.util.*;

public class DeviceConnectionCostCalculator { // Class to compute the minimum cost to connect all devices

    // Represents a connection between two devices with an associated cost
    static class Edge {
        int device1;
        int device2;
        int cost;

        // Constructor to initialize an edge
        public Edge(int device1, int device2, int cost) {
            this.device1 = device1;
            this.device2 = device2;
            this.cost = cost;
        }
    }

    // Implements the Union-Find data structure for managing connected components
    static class UnionFind {
        int[] parent; // Stores the representative parent of each device
        int[] rank;   // Stores the rank (depth) of each component

        // Initializes the Union-Find structure with n devices
        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i; // Each device is initially its own representative
                rank[i] = 0;    // All ranks start at zero
            }
        }

        // Finds the root representative of a given device with path compression
        public int find(int u) {
            if (parent[u] != u) {
                parent[u] = find(parent[u]); // Flatten the tree for efficiency
            }
            return parent[u];
        }

        // Merges the sets of two devices if they are not already connected
        public boolean union(int u, int v) {
            int rootU = find(u);
            int rootV = find(v);

            if (rootU != rootV) { // If they belong to different sets, merge them
                if (rank[rootU] > rank[rootV]) {
                    parent[rootV] = rootU;
                } else if (rank[rootU] < rank[rootV]) {
                    parent[rootU] = rootV;
                } else {
                    parent[rootV] = rootU;
                    rank[rootU]++; // Increase rank when merging trees of equal size
                }
                return true; // Merge was successful
            }
            return false; // Already in the same set, no merge needed
        }
    }

    // Calculates the minimum total cost to connect all devices
    public static int minTotalCost(int n, int[] modules, int[][] connections) {
        List<Edge> edges = new ArrayList<>(); // List to store all available connections

        // Convert the input connection data into Edge objects
        for (int[] connection : connections) {
            edges.add(new Edge(connection[0] - 1, connection[1] - 1, connection[2])); // Convert to zero-based index
        }

        // Sort connections in ascending order based on cost (for Kruskal's algorithm)
        edges.sort(Comparator.comparingInt(edge -> edge.cost));

        UnionFind uf = new UnionFind(n); // Initialize Union-Find structure
        int totalCost = 0; // Stores the total cost of the Minimum Spanning Tree (MST)

        // Apply Kruskal’s algorithm to construct the MST
        for (Edge edge : edges) {
            if (uf.union(edge.device1, edge.device2)) { // Add edge only if it connects two different sets
                totalCost += edge.cost;
            }
        }

        // Add the cost of the least expensive module
        int minModuleCost = Arrays.stream(modules).min().getAsInt();
        totalCost += minModuleCost;

        return totalCost; // Return the minimum cost required to connect all devices
    }

    public static void main(String[] args) {
        int n = 3; // Number of devices
        int[] modules = {1, 2, 2}; // Cost of the module for each device
        int[][] connections = {{1, 2, 1}, {2, 3, 1}}; // Available connections (device1, device2, cost)

        // Compute and display the minimum connection cost
        int result = minTotalCost(n, modules, connections);
        System.out.println("Minimum total cost to connect all devices: " + result); // Expected output: 3
    }
}

// Output: Minimum total cost to connect all devices: 3