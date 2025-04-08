package com.game;

import java.util.Scanner;

/**
 * GameLauncher class provides entry points for launching the game in different modes.
 *
 * <p>This class allows users to choose between running players in separate JVM processes
 * or in the same JVM with different threads.</p>
 */
class GameLauncher {

static final String port = "3000";
    /**
     * Main entry point that determines which mode to run based on arguments.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        // If no arguments, run the setup mode that starts the game
        if (args.length == 0) {
            setupGame();
        }
        // If arguments exist, we're being started as a specific player in a separate process
        else if (args.length == 7) {
            String name = args[0];
            String otherName = args[1];
            int port = Integer.parseInt(args[2]);
            String address = args[3];
            String message = args[4];
            boolean initiator = Boolean.parseBoolean(args[5]);
            int stopCondition = Integer.parseInt(args[6]);

            // Create and run player directly
            Player player = new Player(name, otherName, port, address, message, initiator, stopCondition);
            player.run();
        } else {
            System.out.println("Invalid arguments.");
            System.exit(1);
        }
    }

    /**
     * Sets up the game by getting user input and starting players based on the selected mode.
     */
    private static void setupGame() {
        // Get user input
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWrite the message you want to send from Initiator:");
        String message = scanner.nextLine();

        // Check if the message is null or empty
        if (message == null || message.trim().isEmpty()) {
            System.out.println("Message cannot be null or empty. Please provide a valid message.");
            return;
        }


        System.out.print("Enter stop condition (number of exchanges): ");
        String input = scanner.nextLine();

        int stopCondition;
        try {
            stopCondition = Integer.parseInt(input);
            if (stopCondition < 1) {
                System.out.println(" Stop condition must be at least 1.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println(" Invalid input. Please enter a valid number for stop condition.");
            return;
        }

        // Ask user for the execution mode
        System.out.println("\nSelect execution mode:");
        System.out.println("1. Same JVM (different threads)");
        System.out.println("2. Different JVMs (separate processes)");
        System.out.print("Enter your choice (1 or 2): ");
        int mode = scanner.nextInt();
        scanner.close();

        if (mode == 1) {
            runInSameJvm(message, stopCondition);
        } else if (mode == 2) {
            runInSeparateJvms(message, stopCondition);
        } else {
            System.out.println("Invalid option. Exiting.");
            System.exit(1);
        }
    }

    /**
     * Runs the game with both players in the same JVM using different threads.
     *
     * @param message Initial message from the initiator
     * @param stopCondition Number of exchanges before stopping
     */
    private static void runInSameJvm(String message, int stopCondition) {
        System.out.println("Starting players in the same JVM with different threads...\n");
        int convPort = Integer.parseInt(port);
        // Create player instances
        Player player1 = new Player("Player 1", "Player 2",convPort, "localhost", message, true, stopCondition);
        Player player2 = new Player("Player 2", "Player 1", convPort, "localhost", message, false, stopCondition);

        // Create threads for each player
        Thread thread1 = new Thread(player1);
        Thread thread2 = new Thread(player2);

        try {

            thread1.start();
            thread2.start();

            // Wait for both threads to complete
            thread1.join();
            thread2.join();

            System.out.println("\nGame completed successfully.");
        } catch (Exception e) {
            System.out.println("Game was interrupted: " + e.getMessage());
            if (e instanceof java.net.BindException) {
                System.out.println("\nIt seems there was a 'java.net.BindException: Address already in use' error.");
                System.out.println("This means another program is likely using the default port (" + port + ").");
                System.out.println("To fix this, please change the value of 'DEFAULT_PORT' at the beginning of the GameLauncher.java class to a different number and rebuild the project.");
            }
        }
    }

    /**
     * Runs the game with players in separate JVM processes.
     * here we assume that player 1 is the initiator
     *
     * @param message Initial message from the initiator
     * @param stopCondition Number of exchanges before stopping
     */
    private static void runInSeparateJvms(String message, int stopCondition) {
        System.out.println("Starting players in separate JVM processes...\n");

        try {
            // Build the command for Player 2 (server)
            ProcessBuilder pb2 = new ProcessBuilder(
                    "java",
                    "-cp",
                    System.getProperty("java.class.path"),
                    "com.game.GameLauncher",
                    "Player 2", "Player 1", port, "localhost", message, "false", String.valueOf(stopCondition)
            );
            pb2.inheritIO(); // Redirect output to current console
            Process p2 = pb2.start();

            // Short delay to ensure server starts first
            Thread.sleep(500);

            // Build the command for Player 1 (client)
            ProcessBuilder pb1 = new ProcessBuilder(
                    "java",
                    "-cp",
                    System.getProperty("java.class.path"),
                    "com.game.GameLauncher",
                    "Player 1", "Player 2", port, "localhost", message, "true", String.valueOf(stopCondition)
            );
            pb1.inheritIO(); // Redirect output to current console
            Process p1 = pb1.start();

            // Wait for both processes to complete
            p1.waitFor();
            p2.waitFor();

            System.out.println("\nEXIT");

        } catch (Exception e) {
            System.out.println("Error running separate processes: " + e.getMessage());
            if (e instanceof java.net.BindException) {
                System.out.println("\nIt seems there was a 'java.net.BindException: Address already in use' error.");
                System.out.println("This means another program is likely using the default port (" + port + ").");
                System.out.println("To fix this, please change the value of 'DEFAULT_PORT' at the beginning of the GameLauncher.java class to a different number and rebuild the project.");
            }
        }
    }
}