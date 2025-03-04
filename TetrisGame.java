/*
Problem: Develop a Tetris game where falling blocks must be arranged to complete full rows.

Approach:
1. Game Board: A 2D array is used to track block positions.
2. Blocks: Represented by different shapes and colors, with movement and rotation capabilities.
3. Player Controls: Allows moving blocks left, right, down, and rotating them.
4. Game Over Condition: The game ends when a newly spawned block cannot move down.

Key Components:
- Grid System: Manages block placements and occupied spaces.
- Block Mechanics: Defines how pieces move and rotate.
- Gravity Mechanism: A timer controls the rate at which blocks fall.
- Collision Handling: Prevents blocks from overlapping or exceeding boundaries.
- Row Clearance: Identifies and removes fully occupied rows.

Game Flow:
A new block spawns at the top.
The player moves and rotates it to fit within the board.
When a row is fully filled, it gets cleared, and the game continues.
The game stops when a block cannot be placed.

Complexity:
- Time: O(1) for block movement and rotation, O(n) for row completion checks.
- Space: O(n²) for the game board.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class TetrisGame extends JPanel implements ActionListener {
    // Constants for the game board dimensions, block size, and colors
    private static final int BOARD_WIDTH = 10; // Width of the game board in blocks
    private static final int BOARD_HEIGHT = 20; // Height of the game board in blocks
    private static final int BLOCK_SIZE = 30; // Size of each block in pixels
    private static final Color[] COLORS = { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE }; // Colors for the blocks

    private Timer timer; // Timer to control the game speed
    private boolean isGameOver; // Flag to check if the game is over
    private int[][] board; // 2D array representing the game board
    private Block currentBlock; // The current block that is falling
    private Queue<Block> blockQueue; // Queue to hold the next blocks

    // Constructor to initialize the game
    public TetrisGame() {
        setPreferredSize(new Dimension(BOARD_WIDTH * BLOCK_SIZE, BOARD_HEIGHT * BLOCK_SIZE)); // Set the size of the game panel
        setBackground(Color.BLACK); // Set the background color to black
        setFocusable(true); // Allow the panel to receive key events
        addKeyListener(new KeyAdapter() { // Add a key listener to handle key presses
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isGameOver) { // Only handle key presses if the game is not over
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            moveBlockLeft(); // Move the block left
                            break;
                        case KeyEvent.VK_RIGHT:
                            moveBlockRight(); // Move the block right
                            break;
                        case KeyEvent.VK_DOWN:
                            moveBlockDown(); // Move the block down
                            break;
                        case KeyEvent.VK_UP:
                            rotateBlock(); // Rotate the block
                            break;
                    }
                    repaint(); // Redraw the game panel
                }
            }
        });
        board = new int[BOARD_HEIGHT][BOARD_WIDTH]; // Initialize the game board
        blockQueue = new LinkedList<>(); // Initialize the block queue
        timer = new Timer(500, this); // Initialize the timer with a delay of 500ms
        timer.start(); // Start the timer
        generateNewBlock(); // Generate the first block
    }

    // Method to generate a new block
    private void generateNewBlock() {
        Random random = new Random(); // Create a random number generator
        int shapeIndex = random.nextInt(COLORS.length); // Randomly select a color index
        int[][] shape = getRandomShape(); // Get a random shape for the block
        Block block = new Block(shape, COLORS[shapeIndex]); // Create a new block with the selected shape and color
        blockQueue.add(block); // Add the block to the queue
        currentBlock = blockQueue.poll(); // Set the current block to the first block in the queue
    }

    // Method to get a random shape for the block
    private int[][] getRandomShape() {
        int[][][] shapes = {
                {{1, 1, 1, 1}},          // I-shape
                {{1, 1, 0}, {0, 1, 1}},  // Z-shape
                {{0, 1, 1}, {1, 1, 0}},  // S-shape
                {{1, 1, 1}, {0, 1, 0}},  // T-shape
                {{1, 1}, {1, 1}}         // O-shape
        };
        Random random = new Random(); // Create a random number generator
        return shapes[random.nextInt(shapes.length)]; // Return a random shape
    }

    // Method called by the timer to update the game state
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) { // Only update the game if it is not over
            if (canMoveDown(currentBlock)) { // Check if the current block can move down
                currentBlock.row++; // Move the block down
            } else {
                placeBlock(currentBlock); // Place the block on the board
                checkCompletedRows(); // Check if any rows are completed
                generateNewBlock(); // Generate a new block
                if (!canMoveDown(currentBlock)) { // Check if the new block can move down
                    isGameOver = true; // If not, the game is over
                }
            }
            repaint(); // Redraw the game panel
        }
    }

    // Method to check if the current block can move down
    private boolean canMoveDown(Block block) {
        for (int row = 0; row < block.shape.length; row++) {
            for (int col = 0; col < block.shape[row].length; col++) {
                if (block.shape[row][col] != 0) { // Check if the current cell in the block is occupied
                    int newRow = block.row + row + 1; // Calculate the new row position
                    int newCol = block.col + col; // Calculate the new column position
                    if (newRow >= BOARD_HEIGHT || (newRow >= 0 && board[newRow][newCol] != 0)) { // Check if the new position is out of bounds or occupied
                        return false; // The block cannot move down
                    }
                }
            }
        }
        return true; // The block can move down
    }

    // Method to place the current block on the board
    private void placeBlock(Block block) {
        for (int row = 0; row < block.shape.length; row++) {
            for (int col = 0; col < block.shape[row].length; col++) {
                if (block.shape[row][col] != 0) { // Check if the current cell in the block is occupied
                    board[block.row + row][block.col + col] = block.colorIndex + 1; // Place the block on the board
                }
            }
        }
    }

    // Method to check if any rows are completed and clear them
    private void checkCompletedRows() {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            boolean isComplete = true; // Assume the row is complete
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] == 0) { // Check if any cell in the row is empty
                    isComplete = false; // The row is not complete
                    break;
                }
            }
            if (isComplete) { // If the row is complete
                for (int r = row; r > 0; r--) { // Shift all rows above the completed row down
                    board[r] = board[r - 1];
                }
                board[0] = new int[BOARD_WIDTH]; // Clear the top row
            }
        }
    }

    // Method to move the current block left
    private void moveBlockLeft() {
        if (canMoveLeft(currentBlock)) { // Check if the block can move left
            currentBlock.col--; // Move the block left
        }
    }

    // Method to check if the current block can move left
    private boolean canMoveLeft(Block block) {
        for (int row = 0; row < block.shape.length; row++) {
            for (int col = 0; col < block.shape[row].length; col++) {
                if (block.shape[row][col] != 0) { // Check if the current cell in the block is occupied
                    int newRow = block.row + row; // Calculate the new row position
                    int newCol = block.col + col - 1; // Calculate the new column position
                    if (newCol < 0 || (newRow >= 0 && board[newRow][newCol] != 0)) { // Check if the new position is out of bounds or occupied
                        return false; // The block cannot move left
                    }
                }
            }
        }
        return true; // The block can move left
    }

    // Method to move the current block right
    private void moveBlockRight() {
        if (canMoveRight(currentBlock)) { // Check if the block can move right
            currentBlock.col++; // Move the block right
        }
    }

    // Method to check if the current block can move right
    private boolean canMoveRight(Block block) {
        for (int row = 0; row < block.shape.length; row++) {
            for (int col = 0; col < block.shape[row].length; col++) {
                if (block.shape[row][col] != 0) { // Check if the current cell in the block is occupied
                    int newRow = block.row + row; // Calculate the new row position
                    int newCol = block.col + col + 1; // Calculate the new column position
                    if (newCol >= BOARD_WIDTH || (newRow >= 0 && board[newRow][newCol] != 0)) { // Check if the new position is out of bounds or occupied
                        return false; // The block cannot move right
                    }
                }
            }
        }
        return true; // The block can move right
    }

    // Method to move the current block down
    private void moveBlockDown() {
        if (canMoveDown(currentBlock)) { // Check if the block can move down
            currentBlock.row++; // Move the block down
        }
    }

    // Method to rotate the current block
    private void rotateBlock() {
        currentBlock.rotate(); // Rotate the block
        if (!canMoveDown(currentBlock)) { // Check if the block can move down after rotation
            currentBlock.rotate(); // Rotate back if collision detected
            currentBlock.rotate();
            currentBlock.rotate();
        }
    }

    // Method to paint the game panel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call the superclass method to paint the background
        // Draw the game board
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] != 0) { // Check if the current cell is occupied
                    g.setColor(COLORS[board[row][col] - 1]); // Set the color for the block
                    g.fillRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE); // Draw the block
                }
            }
        }
        // Draw the current block
        if (currentBlock != null) {
            g.setColor(currentBlock.color); // Set the color for the current block
            for (int row = 0; row < currentBlock.shape.length; row++) {
                for (int col = 0; col < currentBlock.shape[row].length; col++) {
                    if (currentBlock.shape[row][col] != 0) { // Check if the current cell in the block is occupied
                        g.fillRect((currentBlock.col + col) * BLOCK_SIZE, (currentBlock.row + row) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE); // Draw the block
                    }
                }
            }
        }
        // Display game over message
        if (isGameOver) {
            g.setColor(Color.WHITE); // Set the color for the game over message
            g.setFont(new Font("Arial", Font.BOLD, 36)); // Set the font for the game over message
            g.drawString("Game Over", 50, 300); // Draw the game over message
        }
    }

    // Main method to start the game
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris Game"); // Create a new JFrame for the game
        TetrisGame game = new TetrisGame(); // Create a new instance of the game
        frame.add(game); // Add the game panel to the frame
        frame.pack(); // Pack the frame to fit the game panel
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the default close operation
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true); // Make the frame visible
    }

    // Inner class to represent a block
    static class Block {
        int[][] shape; // 2D array representing the shape of the block
        int row, col; // Position of the block on the board
        Color color; // Color of the block
        int colorIndex; // Index of the color in the COLORS array

        // Constructor to initialize the block
        public Block(int[][] shape, Color color) {
            this.shape = shape; // Set the shape of the block
            this.row = 0; // Set the initial row position
            this.col = BOARD_WIDTH / 2 - shape[0].length / 2; // Set the initial column position
            this.color = color; // Set the color of the block
            this.colorIndex = getColorIndex(color); // Set the color index
        }

        // Method to get the index of the color in the COLORS array
        private int getColorIndex(Color color) {
            for (int i = 0; i < COLORS.length; i++) {
                if (COLORS[i].equals(color)) { // Check if the color matches
                    return i; // Return the index of the color
                }
            }
            return -1; // Return -1 if the color is not found
        }

        // Method to rotate the block 90 degrees clockwise
        public void rotate() {
            int rows = shape.length; // Get the number of rows in the shape
            int cols = shape[0].length; // Get the number of columns in the shape
            int[][] rotated = new int[cols][rows]; // Create a new array for the rotated shape
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    rotated[j][rows - i - 1] = shape[i][j]; // Rotate the shape 90 degrees clockwise
                }
            }
            shape = rotated; // Set the shape to the rotated shape
        }
    }
}