#!/bin/bash

# Define variables
SRC_DIR="src/main/java"
OUT_DIR="out"
JAR_FILE="uniquehands.jar"
MAIN_CLASS="edu.canisius.csc213.project1.UniqueHands"

# Clean previous build
echo "🧹 Cleaning previous build..."
rm -rf $OUT_DIR
mkdir -p $OUT_DIR

# Compile Java files
echo "🚀 Compiling Java files..."
javac -d $OUT_DIR $(find $SRC_DIR -name "*.java")

# Package into a JAR
echo "📦 Creating JAR file..."
jar cvfe $JAR_FILE $MAIN_CLASS -C $OUT_DIR .

# Done!
echo "✅ Build complete! Run it with: java -jar $JAR_FILE"

