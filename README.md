# Java Socket Game

## Table of Contents
- [Overview](#overview)
- [Key Concepts](#key-concepts)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [How to Run](#how-to-run)
- [Game Execution Modes](#game-execution-modes)
- [Sample Output](#sample-output)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)

## Overview
This project implements a two-player game where players communicate by exchanging messages over network sockets. The game demonstrates fundamental concepts in concurrent programming and network communication in Java.

The game allows two execution modes:
- **Same JVM Mode**: Both players run in the same Java Virtual Machine but operate independently in separate threads
- **Separate JVMs Mode**: Each player runs in a different Java process, simulating real-world distributed applications

## Key Concepts

### Threads
Threads are lightweight units of execution within a process. In this game, when running in Same JVM mode, each player operates in its own thread, allowing them to run concurrently while sharing the same memory space. This enables:
- Parallel execution of player code
- Independent timing for each player
- Shared access to common resources

### Processes
A process is an independent program execution with its own memory space. In Separate JVMs mode, each player runs in a completely separate Java process, demonstrating:
- Complete isolation between players
- Independent memory spaces
- Communication solely through network protocols

### Sockets
Sockets provide an endpoint for sending and receiving data across a network connection. In this game:
- Players communicate by establishing a socket connection
- One player acts as a server (listening for connections)
- The other player acts as a client (initiating the connection)
- Messages are sent as data streams through the socket

### Message Exchange Pattern
The game implements a request-response pattern where:
1. The initiator player sends a message
2. The recipient processes it and sends back a response
3. This cycle repeats until a stop condition is met

## Project Structure

```
/your-project-root
├── /src
│   └── /main
│       └── /java
│           └── /com
│               └── /game
│                   ├── GameLauncher.java  # Main application entry point
│                   └── Player.java        # Player implementation with socket communication
├── /target
│   └── [Generated JARs]
├── /docs
│   └── [JavaDocs documentation]
├── pom.xml           # Maven project configuration
├── run-game.sh       # Unix/macOS launch script
├── run-game.bat      # Windows launch script
└── README.md         # This documentation file
```

## Requirements

To build and run this project, you'll need:

### Java
- Java Development Kit (JDK) 8 or higher
- To verify installation: `java -version`

### Maven
- Maven 3.5 or higher for building the project
- To verify installation: `mvn -version`
- If not installed, download from [Maven's official site](https://maven.apache.org/download.cgi)

## How to Run

### Building the Project
Before running, build the project using Maven:

```bash
mvn clean package
```

This creates a runnable JAR file in the `/target` directory.

### Starting the Game

#### On Linux/macOS
```bash
./run-game.sh
```

#### On Windows
```bash
run-game.bat
```

> **Note:** The Windows batch file (.bat) is not included in email attachments due to Gmail security restrictions.

## Game Execution Modes

### 1. Same JVM Mode (Different Threads)
In this mode:
- Both players run within the same Java process
- Each player operates in its own thread
- Players communicate via sockets on the local machine
- Demonstrates thread-based concurrency within a single application

### 2. Separate JVMs Mode (Different Processes)
In this mode:
- Each player runs in a separate Java process
- Players communicate over network sockets
- One process acts as a server, the other as a client
- Demonstrates inter-process communication
- Simulates distributed application communication

## Sample Output

```
Write the message you want to send from Initiator:
hello
Enter stop condition (number of exchanges): 2

Select execution mode:
1. Same JVM (different threads)
2. Different JVMs (separate processes)
Enter your choice (1 or 2): 1

Starting players in the same JVM with different threads...
Player 1: "hello" -> Player 2
Player 2: "hello 1" -> Player 1
Player 1: "hello 1 1" -> Player 2
Player 2: "hello 1 1 2" -> Player 1
Game completed successfully.
```

## Troubleshooting

### Maven Issues
If you encounter Maven-related errors:
- Ensure Maven is properly installed and accessible in your PATH
- Try running `mvn clean` to reset the build environment
- Check if your `pom.xml` has all necessary dependencies

### Port Conflicts
The application uses port **3000** by default (defined in `GameLauncher.java`):

```java
static final String PORT = "3000";
```

If you see a `java.net.BindException: Address already in use` error:
1. Another application is using port 3000
2. To resolve this:
  - Modify the `PORT` constant in `GameLauncher.java` to an unused port number
  - Or terminate the process using port 3000

> **Note:** Port 5000 was initially avoided as it's commonly used by system services on macOS.

## FAQ

### Q: Why use Sockets instead of a simpler communication method?
**A:** While a `BlockingQueue` would be simpler for same-JVM communication, sockets provide a consistent mechanism that works in both execution modes. Sockets enable communication whether players run in the same process or in completely separate processes, even on different machines.

### Q: What causes the delay between messages?
**A:** To create a more realistic interaction pattern, the game introduces random time delays between message exchanges. This simulates network latency and processing time in real-world applications.

If you want to remove these delays, comment out these lines in `Player.java`:
- Line 109: `Thread.sleep(random.nextInt(2000) + 500);`
- Line 153: `Thread.sleep(random.nextInt(2000) + 500);`

### Q: How does the stop condition work?
**A:** The stop condition defines how many message exchanges will occur before the game ends. For example, if you set the stop condition to 2:

- Initiator sends message #1 → Recipient responds
- Initiator sends message #2 → Recipient responds
- Game ends after 4 total messages (2 from each player)

### Q: Why do message counts appear in different positions for the client and server?
**A:** This reflects the different roles and message processing flows of the client and server:

- The server increments its count after receiving and before responding
- The client structures its messaging loop to ensure the stop condition accurately tracks complete exchanges

The current implementation ensures that the stop condition correctly represents the number of complete request-response cycles initiated by the client.

### Q: Where are the JavaDocs?
**A:** JavaDoc comments are included in the source code, but the generated HTML documentation files are not included in the distribution due to email attachment limitations. You can generate them yourself with:

```bash
mvn javadoc:javadoc
```