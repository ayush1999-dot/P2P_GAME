#!/bin/bash

# Stop on any error
set -e

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven first."
    exit 1
fi

# Build the project with Maven
echo "Building project with Maven..."
mvn clean package

# Get the generated JAR file
JAR_FILE=$(find target -name "*.jar")

if [ -z "$JAR_FILE" ]; then
    echo "Error: Could not find the built JAR file."
    exit 1
fi

echo "Found JAR file: $JAR_FILE"

# Run the game
echo "Starting the game launcher..."
java -jar "$JAR_FILE"