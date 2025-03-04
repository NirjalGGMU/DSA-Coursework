/*
Problem: Print a sequence in the format 0, odd, 0, even, 0, odd, ... up to a given number n using three synchronized threads.

Thread Responsibilities:
- ZeroThread: Prints 0.
- OddThread: Prints odd numbers.
- EvenThread: Prints even numbers.

Synchronization Approach:
- Locks & Conditions: Used to coordinate thread execution.
- ZeroThread Execution: Prints 0 and signals either OddThread or EvenThread to proceed.
- OddThread Execution: Prints an odd number, then signals ZeroThread to continue.
- EvenThread Execution: Prints an even number, then signals ZeroThread to continue.

Key Synchronization Elements:
- Lock Mechanism: Ensures only one thread runs at a time.

Conditional Variables:
- zeroCondition: Controls ZeroThread.
- oddEvenCondition: Controls OddThread and EvenThread.
- Turn Flag (isZeroTurn): Determines whether ZeroThread should print 0 or pass control.

Execution Flow:
- ZeroThread prints 0 → Signals OddThread or EvenThread.
- OddThread prints an odd number → Signals ZeroThread.
- EvenThread prints an even number → Signals ZeroThread.
This cycle continues until the sequence reaches n.

Complexity:
- Time: O(n) (each thread runs n times).
- Space: O(1) (constant space for locks and conditions).
*/

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Class to handle printing of numbers (0, even, odd)
class NumberPrinter {
    // Method to print the number 0
    public void printZero() {
        System.out.print("0"); // Print "0" to the console
    }

    // Method to print even numbers
    public void printEven(int num) {
        System.out.print(num); // Print the even number to the console
    }

    // Method to print odd numbers
    public void printOdd(int num) {
        System.out.print(num); // Print the odd number to the console
    }
}

// Class to control the threads and synchronize their execution
class ThreadController {
    private final NumberPrinter printer = new NumberPrinter(); // Instance of NumberPrinter
    private final Lock lock = new ReentrantLock(); // Lock for synchronization
    private final Condition zeroCondition = lock.newCondition(); // Condition for ZeroThread
    private final Condition oddEvenCondition = lock.newCondition(); // Condition for OddThread and EvenThread
    private int currentNumber = 1; // Tracks the current number to be printed
    private boolean isZeroTurn = true; // Flag to indicate if it's ZeroThread's turn to print

    // Method to start and coordinate the threads
    public void printSequence(int n) {
        // Create and start ZeroThread
        Thread zeroThread = new Thread(new ZeroThread(n));
        zeroThread.start();

        // Create and start OddThread
        Thread oddThread = new Thread(new OddThread(n));
        oddThread.start();

        // Create and start EvenThread
        Thread evenThread = new Thread(new EvenThread(n));
        evenThread.start();

        // Wait for all threads to finish execution
        try {
            zeroThread.join(); // Wait for ZeroThread to finish
            oddThread.join(); // Wait for OddThread to finish
            evenThread.join(); // Wait for EvenThread to finish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle thread interruption
        }
    }

    // Inner class for ZeroThread
    private class ZeroThread implements Runnable {
        private final int n; // Maximum number to print

        // Constructor to initialize n
        public ZeroThread(int n) {
            this.n = n;
        }

        // Run method for ZeroThread
        @Override
        public void run() {
            lock.lock(); // Acquire the lock
            try {
                while (currentNumber <= n) { // Loop until currentNumber exceeds n
                    while (!isZeroTurn) { // Wait if it's not ZeroThread's turn
                        zeroCondition.await(); // Wait for signal
                    }
                    if (currentNumber > n) { // Stop if currentNumber exceeds n
                        break;
                    }
                    printer.printZero(); // Print "0"
                    isZeroTurn = false; // Set flag to false (next turn is OddThread or EvenThread)
                    oddEvenCondition.signalAll(); // Signal OddThread or EvenThread to proceed
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Handle thread interruption
            } finally {
                lock.unlock(); // Release the lock
            }
        }
    }

    // Inner class for OddThread
    private class OddThread implements Runnable {
        private final int n; // Maximum number to print

        // Constructor to initialize n
        public OddThread(int n) {
            this.n = n;
        }

        // Run method for OddThread
        @Override
        public void run() {
            lock.lock(); // Acquire the lock
            try {
                while (currentNumber <= n) { // Loop until currentNumber exceeds n
                    while (isZeroTurn || currentNumber % 2 == 0) { // Wait if it's ZeroThread's turn or currentNumber is even
                        oddEvenCondition.await(); // Wait for signal
                    }
                    if (currentNumber > n) { // Stop if currentNumber exceeds n
                        break;
                    }
                    printer.printOdd(currentNumber); // Print odd number
                    currentNumber++; // Increment currentNumber
                    isZeroTurn = true; // Set flag to true (next turn is ZeroThread)
                    zeroCondition.signal(); // Signal ZeroThread to proceed
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Handle thread interruption
            } finally {
                lock.unlock(); // Release the lock
            }
        }
    }

    // Inner class for EvenThread
    private class EvenThread implements Runnable {
        private final int n; // Maximum number to print

        // Constructor to initialize n
        public EvenThread(int n) {
            this.n = n;
        }

        // Run method for EvenThread
        @Override
        public void run() {
            lock.lock(); // Acquire the lock
            try {
                while (currentNumber <= n) { // Loop until currentNumber exceeds n
                    while (isZeroTurn || currentNumber % 2 != 0) { // Wait if it's ZeroThread's turn or currentNumber is odd
                        oddEvenCondition.await(); // Wait for signal
                    }
                    if (currentNumber > n) { // Stop if currentNumber exceeds n
                        break;
                    }
                    printer.printEven(currentNumber); // Print even number
                    currentNumber++; // Increment currentNumber
                    isZeroTurn = true; // Set flag to true (next turn is ZeroThread)
                    zeroCondition.signal(); // Signal ZeroThread to proceed
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Handle thread interruption
            } finally {
                lock.unlock(); // Release the lock
            }
        }
    }
}

// Main class to run the program
public class ZeroOddEvenPrinter {
    public static void main(String[] args) {
        ThreadController controller = new ThreadController(); // Create ThreadController instance
        controller.printSequence(5); // Print sequence up to 5
    }
}

// Output
// 0102030405