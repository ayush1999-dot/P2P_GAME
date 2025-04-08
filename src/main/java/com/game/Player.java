package com.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;



/**
 * Represents a player in a simple message-passing game.
 * <p>
 * Each player can act as either a server or a client, exchanging messages with another player.
 * Each player runs in its own thread and can communicate with another player
 * using socket-based communication. Players exchange messages, with each response
 * containing the original message plus a counter.
 *  </p>
 *
 * The game continues until a predefined number of messages have been exchanged.
 */
public class Player implements Runnable {

    Random random = new Random();


    private final String name;
    private final String otherName;
    private final int stopCondition; // Number of messages to exchange before stopping.
    private final int port;
    private final String address; // Address of the other player (used when acting as a client).
    private final boolean initiator;// Indicates if this player initiates the communication (client).
    private final String message;// Initial message sent by the initiator.

    /**
     * Constructs a new Player object.
     *
     * @param name          The player's name.
     * @param otherName     The other player's name.
     * @param port          The port number for communication.
     * @param address       The other player's address.
     * @param message       The initial message (only for the initiator).
     * @param initiator     Whether this player initiates the communication.
     * @param stopCondition The number of messages to exchange.
     */
    public Player(String name, String otherName, int port, String address, String message, boolean initiator, int stopCondition) {
        this.name = name;
        this.otherName = otherName;
        this.stopCondition = stopCondition;
        this.initiator = initiator;
        this.message = message;
        this.port = port;
        this.address = address;
    }

    /**
     * Main execution method that determines player role.
     * When thread starts, this method will run the player either as a server
     * or client based on whether it's the initiator.
     */
    @Override
    public void run() {
        if (!initiator) {
            runAsServer();
        } else {
            runAsClient(message);

        }
    }

    /**
     * Runs the player as a server, listening for and responding to messages.
     */
    private void runAsServer() {
        String funcName ="runAsServer";
        try{
            // Create a server socket to listen for incoming connections.
            ServerSocket serverSocket  = new ServerSocket(port);
            // Accept an incoming client connection.
             Socket socket = serverSocket.accept();

            // Setup input and output streams for communication.
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            int count = 0;
            String response;
            while (true) {

                if (count >= stopCondition) {
                    break;
                }
                //logic behind why count are in a different position (README file)
                count++;

                processAndRespond(in, out, count);

            }
        } catch (IOException | InterruptedException e) {
            System.out.println("IOException in "+ funcName + e);
        }
    }


    /**
     * Runs the player as a client, sending and receiving messages.
     *
     * @param message The initial message to send.
     */
    private void runAsClient(String message) {

        String funcName ="runAsClient";
        try {
            // Introduce a short delay for server setup
            Thread.sleep(300);

            // Create a socket to connect to the server.
            Socket socket = new Socket(address, port);

            // Setup input and output streams for communication.
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Send the initial message.
            sendAndDisplay(out,name,message,otherName);

            int count = 0;
            String response;
            while (true) {
                //logic behind why count are in different position (README file)
                count++;
                if (count >= stopCondition) {
                    break;
                }

                processAndRespond(in, out, count);

            }
        } catch (IOException |InterruptedException e) {
            System.out.println("IOException in "+ funcName + e);
        }
    }


    /**
     * Reads a message from the input stream, generates a response which is ( message recieved  + count),
     * simulates network latency, and sends the response through the output stream.
     *
     * @param in    The BufferedReader to read incoming messages.
     * @param out   The PrintWriter to send outgoing messages.
     * @param count The current message count, used for generating responses.
     * @throws IOException if an I/O error occurs during reading or writing.
     * @throws InterruptedException if the thread is interrupted during the delay.
     */
    private void processAndRespond(BufferedReader in, PrintWriter out, int count) throws IOException, InterruptedException {
        // Read the incoming message and create a response.
        String response = readAndCreateResponse(in, count);
        if (response == null) {
            throw new InterruptedException();
        }

        // Simulate network latency
        Thread.sleep(random.nextInt(2000) + 500);

        // Send the response and display it
        sendAndDisplay(out, name, response, otherName);


    }

    /**
     * Reads a message from the input stream and creates a response.
     * <p>
     * The response consists of the original message plus the current message count.
     * </p>
     * @param in    The input stream to read from.
     * @param count The current message count.
     * @return The read line with the count appended, or null if the end of stream is reached.
     * @throws IOException If an I/O error occurs.
     */
    private String  readAndCreateResponse( BufferedReader in, int count) throws IOException {
        String inputLine = in.readLine();
        if (inputLine == null) {
            return null;
        }
        return inputLine + " " + count;
    }

    /**
     * Sends a message to the other player and displays it in the console.
     *
     * @param out       The output stream to send the message to.
     * @param name      The sender's name.
     * @param message   The message to send.
     * @param otherName The recipient's name.
     * @throws InterruptedException If the thread is interrupted.
     */
    private void sendAndDisplay(PrintWriter out,String name,String message,String otherName) throws InterruptedException {
        out.println(message);
        System.out.printf("%s: \"%s\" -> %s%n", name, message, otherName);
    }
}

