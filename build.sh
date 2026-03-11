#!/bin/bash
# ─── build.sh ─────────────────────────────────────────────────────────
# Compiles and runs Tic Tac Toe without Maven.
# Requires Java 17+ JDK.

set -e
SRC="src"
OUT="out"

echo "Cleaning output directory..."
rm -rf "$OUT"
mkdir -p "$OUT"

echo "Compiling sources..."
find "$SRC" -name "*.java" -print0 | xargs -0 javac -d "$OUT"

echo "Running Tic Tac Toe..."
java -cp "$OUT" tictactoe.Main
