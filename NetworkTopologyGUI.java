//QN 5 - NetworkTopologyGUI


import java.awt.*; // Import AWT classes for GUI components
import java.util.*; // Import utility classes for collections
import java.util.List; // Import List interface
import javax.swing.*; // Import Swing classes for GUI components

/*
 * This Java application provides a graphical user interface (GUI) for designing network topologies. 
 * Users can create and visualize network nodes and connecting edges. The interface allows users to 
 * construct network elements, apply minimum spanning tree optimization, and determine the shortest 
 * path between nodes. The program uses a graph data model where devices are represented as nodes 
 * and connections as edges. Swing is used to build the interactive GUI, while Prim's algorithm 
 * computes the minimum spanning tree and Dijkstra's algorithm calculates the shortest path. 
 * The GUI dynamically updates based on user interactions, providing an intuitive experience.
 */

/*
 * Main Class (NetworkTopologyGUI):
 * - Sets up the drawing interface and graph visualization.
 * - Configures the main window with a title, size, and layout.
 * - Provides buttons for adding nodes, connecting edges, optimizing the network, and calculating paths.
 * - Implements action listeners to handle user interactions.
 * 
 * Adding Nodes:
 * - Prompts the user for a node name when the "Add Node" button is clicked.
 * - Validates the input and adds the node to the graph.
 * 
 * Adding Edges:
 * - Prompts the user for source node, destination node, cost, and bandwidth when the "Add Edge" button is clicked.
 * - Validates the input and adds the edge to the graph if both nodes exist.
 * 
 * Optimizing the Network:
 * - Uses Prim's algorithm to compute the minimum spanning tree (MST) when the "Optimize Network" button is clicked.
 * - Displays the MST edges in a dialog.
 * 
 * Calculating Shortest Path:
 * - Prompts the user for start and end nodes when the "Calculate Path" button is clicked.
 * - Uses Dijkstra's algorithm to compute the shortest path and displays the result.
 * 
 * Graph Class (Graph):
 * - Uses a map to store nodes by their names.
 * - Provides methods to add nodes, add edges, compute the MST, and find the shortest path.
 * 
 * Node Class (Node):
 * - Represents a node in the graph with a name and a list of connected edges.
 * - Provides methods to add edges and retrieve node information.
 * 
 * Edge Class (Edge):
 * - Represents an edge in the graph with a destination node, cost, and bandwidth.
 * - Provides methods to retrieve edge information.
 * 
 * GraphPanel Class (GraphPanel):
 * - Extends JPanel to provide a custom drawing space for the graph.
 * - Overrides paintComponent to draw nodes as circles and edges as lines, displaying edge details.
 */

// Main class for the Network Topology GUI
public class NetworkTopologyGUI extends JFrame {
    private Graph graph; // Represents the network graph
    private GraphPanel graphPanel; // Panel to draw the graph

    // Constructor for the NetworkTopologyGUI
    public NetworkTopologyGUI() {
        graph = new Graph(); // Initialize the graph
        graphPanel = new GraphPanel(graph); // Create the graph panel

        // Set up the JFrame properties
        setTitle("Network Topology Designer"); // Set the window title
        setSize(800, 600); // Set the window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the application on exit
        setLayout(new BorderLayout()); // Use BorderLayout for the frame

        add(graphPanel, BorderLayout.CENTER); // Add the graph panel to the center

        // Create a control panel for buttons
        JPanel controlPanel = new JPanel();
        JButton addNodeButton = new JButton("Add Node"); // Button to add a node
        JButton addEdgeButton = new JButton("Add Edge"); // Button to add an edge
        JButton optimizeButton = new JButton("Optimize Network"); // Button to optimize the network
        JButton calculatePathButton = new JButton("Calculate Path"); // Button to calculate the shortest path

        // Add buttons to the control panel
        controlPanel.add(addNodeButton);
        controlPanel.add(addEdgeButton);
        controlPanel.add(optimizeButton);
        controlPanel.add(calculatePathButton);
        add(controlPanel, BorderLayout.SOUTH); // Add control panel to the bottom

        // Action listener for adding a node
        addNodeButton.addActionListener(e -> {
            String nodeName = JOptionPane.showInputDialog("Enter node name:"); // Prompt for node name
            if (nodeName != null && !nodeName.trim().isEmpty()) { // Validate input
                graph.addNode(nodeName); // Add node to the graph
                graphPanel.repaint(); // Refresh the graph panel
            } else {
                JOptionPane.showMessageDialog(null, "Node name cannot be empty."); // Show error for invalid input
            }
        });

        // Action listener for adding an edge
        addEdgeButton.addActionListener(e -> {
            String fromNode = JOptionPane.showInputDialog("Enter from node:"); // Prompt for source node
            String toNode = JOptionPane.showInputDialog("Enter to node:"); // Prompt for destination node
            String costStr = JOptionPane.showInputDialog("Enter cost:"); // Prompt for edge cost
            String bandwidthStr = JOptionPane.showInputDialog("Enter bandwidth:"); // Prompt for edge bandwidth

            // Validate all inputs
            if (fromNode != null && toNode != null && costStr != null && bandwidthStr != null) {
                try {
                    int cost = Integer.parseInt(costStr); // Parse cost
                    int bandwidth = Integer.parseInt(bandwidthStr); // Parse bandwidth
                    if (cost < 0 || bandwidth < 0) { // Ensure non-negative values
                        JOptionPane.showMessageDialog(null, "Cost and bandwidth must be non-negative.");
                    } else {
                        graph.addEdge(fromNode, toNode, cost, bandwidth); // Add edge to the graph
                        graphPanel.repaint(); // Refresh the graph panel
                    }
                } catch (NumberFormatException ex) { // Handle invalid number format
                    JOptionPane.showMessageDialog(null, "Invalid cost or bandwidth.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "All fields must be filled."); // Show error for missing fields
            }
        });

        // Action listener for optimizing the network
        optimizeButton.addActionListener(e -> {
            List<Edge> mstEdges = graph.getMinimumSpanningTree(); // Compute MST
            StringBuilder result = new StringBuilder("Minimum Spanning Tree Edges:\n"); // Prepare result string
            for (Edge edge : mstEdges) { // Iterate through MST edges
                result.append(edge.getDestination().getName()).append(" (Cost: ").append(edge.getCost()).append(")\n");
            }
            JOptionPane.showMessageDialog(null, result.toString()); // Display result
        });

        // Action listener for calculating the shortest path
        calculatePathButton.addActionListener(e -> {
            String fromNode = JOptionPane.showInputDialog("Enter start node:"); // Prompt for start node
            String toNode = JOptionPane.showInputDialog("Enter end node:"); // Prompt for end node
            if (fromNode != null && toNode != null) { // Validate inputs
                List<Node> path = graph.findShortestPath(fromNode, toNode); // Compute shortest path
                if (path != null) { // Check if a path exists
                    StringBuilder result = new StringBuilder("Shortest Path:\n"); // Prepare result string
                    for (Node node : path) { // Iterate through path nodes
                        result.append(node.getName()).append(" -> ");
                    }
                    result.setLength(result.length() - 4); // Remove the last arrow
                    JOptionPane.showMessageDialog(null, result.toString()); // Display result
                } else {
                    JOptionPane.showMessageDialog(null, "No path found."); // Show error if no path exists
                }
            }
        });
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NetworkTopologyGUI gui = new NetworkTopologyGUI(); // Create GUI instance
            gui.setVisible(true); // Display the GUI
        });
    }
}

// Class representing the graph structure
class Graph {
    private final Map<String, Node> nodes = new HashMap<>(); // Map to store nodes by name

    // Method to add a node to the graph
    public void addNode(String name) {
        nodes.put(name, new Node(name)); // Add a new node
    }

    // Method to add an edge between two nodes
    public void addEdge(String from, String to, int cost, int bandwidth) {
        Node source = nodes.get(from); // Get source node
        Node destination = nodes.get(to); // Get destination node
        if (source != null && destination != null) { // Validate nodes
            source.addEdge(new Edge(destination, cost, bandwidth)); // Add edge
        } else {
            JOptionPane.showMessageDialog(null, "One or both nodes do not exist."); // Show error if nodes are invalid
        }
    }

    // Method to get all nodes in the graph
    public Collection<Node> getNodes() {
        return nodes.values(); // Return all nodes
    }

    // Method to compute the minimum spanning tree (MST)
    public List<Edge> getMinimumSpanningTree() {
        List<Edge> mstEdges = new ArrayList<>(); // List to store MST edges
        Set<Node> visited = new HashSet<>(); // Set to track visited nodes
        PriorityQueue<Edge> edgeQueue = new PriorityQueue<>(Comparator.comparingInt(Edge::getCost)); // Priority queue for edges

        if (!nodes.isEmpty()) { // Check if the graph has nodes
            Node startNode = nodes.values().iterator().next(); // Get a starting node
            visited.add(startNode); // Mark start node as visited
            edgeQueue.addAll(startNode.getEdges()); // Add edges of the start node to the queue

            // Build the MST
            while (!edgeQueue.isEmpty()) {
                Edge edge = edgeQueue.poll(); // Get the edge with the lowest cost
                Node destination = edge.getDestination(); // Get the destination node
                if (!visited.contains(destination)) { // Check if the node is unvisited
                    visited.add(destination); // Mark node as visited
                    mstEdges.add(edge); // Add edge to MST
                    edgeQueue.addAll(destination.getEdges()); // Add edges of the new node to the queue
                }
            }
        }
        return mstEdges; // Return the MST edges
    }

    // Method to find the shortest path between two nodes
    public List<Node> findShortestPath(String startName, String endName) {
        Node startNode = nodes.get(startName); // Get start node
        Node endNode = nodes.get(endName); // Get end node
        if (startNode == null || endNode == null) return null; // Return null if nodes are invalid

        Map<Node, Integer> distances = new HashMap<>(); // Map to store distances
        Map<Node, Node> previousNodes = new HashMap<>(); // Map to store previous nodes
        PriorityQueue<Node> nodeQueue = new PriorityQueue<>(Comparator.comparingInt(distances::get)); // Priority queue for nodes

        // Initialize distances and previous nodes
        for (Node node : nodes.values()) {
            distances.put(node, Integer.MAX_VALUE); // Set initial distance to infinity
            previousNodes.put(node, null); // Set previous node to null
        }
        distances.put(startNode, 0); // Set distance of start node to 0
        nodeQueue.add(startNode); // Add start node to the queue

        // Compute shortest path
        while (!nodeQueue.isEmpty()) {
            Node currentNode = nodeQueue.poll(); // Get the node with the smallest distance
            if (currentNode.equals(endNode)) break; // Stop if end node is reached

            // Explore neighbors
            for (Edge edge : currentNode.getEdges()) {
                Node neighbor = edge.getDestination(); // Get the neighbor node
                int newDist = distances.get(currentNode) + edge.getCost(); // Calculate new distance
                if (newDist < distances.get(neighbor)) { // Check if the new distance is shorter
                    distances.put(neighbor, newDist); // Update distance
                    previousNodes.put(neighbor, currentNode); // Update previous node
                    nodeQueue.add(neighbor); // Add neighbor to the queue
                }
            }
        }

        // Build the path from end to start
        List<Node> path = new ArrayList<>();
        for (Node at = endNode; at != null; at = previousNodes.get(at)) {
            path.add(at); // Add node to the path
        }
        Collections.reverse(path); // Reverse the path to get start to end
        return path.size() > 1 ? path : null; // Return the path if valid, otherwise null
    }
}

// Class representing a node in the graph
class Node {
    private final String name; // Name of the node
    private final List<Edge> edges = new ArrayList<>(); // List of connected edges

    // Constructor for the Node class
    Node(String name) {
        this.name = name; // Set the node name
    }

    // Method to add an edge to the node
    void addEdge(Edge edge) {
        edges.add(edge); // Add edge to the list
    }

    // Method to get the node name
    public String getName() {
        return name; // Return the name
    }

    // Method to get the connected edges
    public List<Edge> getEdges() {
        return edges; // Return the edges
    }
}

// Class representing an edge in the graph
class Edge {
    private final Node destination; // Destination node
    private final int cost; // Edge cost
    private final int bandwidth; // Edge bandwidth

    // Constructor for the Edge class
    Edge(Node destination, int cost, int bandwidth) {
        this.destination = destination; // Set the destination node
        this.cost = cost; // Set the cost
        this.bandwidth = bandwidth; // Set the bandwidth
    }

    // Method to get the destination node
    public Node getDestination() {
        return destination; // Return the destination
    }

    // Method to get the edge cost
    public int getCost() {
        return cost; // Return the cost
    }

    // Method to get the edge bandwidth
    public int getBandwidth() {
        return bandwidth; // Return the bandwidth
    }
}

// Class for the panel that draws the graph
class GraphPanel extends JPanel {
    private final Graph graph; // Graph to be drawn

    // Constructor for the GraphPanel class
    public GraphPanel(Graph graph) {
        this.graph = graph; // Set the graph
        setBackground(Color.WHITE); // Set the background color
    }

    // Method to paint the component
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call superclass method
        drawGraph(g); // Draw the graph
    }

    // Method to draw the graph
    private void drawGraph(Graphics g) {
        int nodeRadius = 20; // Radius of nodes
        int xOffset = 50; // X offset for positioning
        int yOffset = 50; // Y offset for positioning
        int spacing = 100; // Spacing between nodes
        int index = 0; // Index for positioning

        // Draw nodes
        for (Node node : graph.getNodes()) {
            int x = xOffset + (index % 5) * spacing; // Calculate X position
            int y = yOffset + (index / 5) * spacing; // Calculate Y position
            g.fillOval(x, y, nodeRadius, nodeRadius); // Draw node as a circle
            g.drawString(node.getName(), x + 5, y + 15); // Draw node name
            index++; // Increment index
        }

        // Draw edges
        for (Node node : graph.getNodes()) {
            for (Edge edge : node.getEdges()) {
                // Calculate positions for the edge
                int fromX = xOffset + (Arrays.asList(graph.getNodes().toArray()).indexOf(node) % 5) * spacing + nodeRadius / 2;
                int fromY = yOffset + (Arrays.asList(graph.getNodes().toArray()).indexOf(node) / 5) * spacing + nodeRadius / 2;
                int toX = xOffset + (Arrays.asList(graph.getNodes().toArray()).indexOf(edge.getDestination()) % 5) * spacing + nodeRadius / 2;
                int toY = yOffset + (Arrays.asList(graph.getNodes().toArray()).indexOf(edge.getDestination()) / 5) * spacing + nodeRadius / 2;

                g.drawLine(fromX, fromY, toX, toY); // Draw the edge
                g.drawString("Cost: " + edge.getCost() + ", Bandwidth: " + edge.getBandwidth(), (fromX + toX) / 2, (fromY + toY) / 2); // Draw edge details
            }
        }
    }
}