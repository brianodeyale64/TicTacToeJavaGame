# Tic Tac Toe — Java Portfolio Project

A polished, fully-featured Tic Tac Toe game built with Java Swing.
Demonstrates MVC architecture, AI game theory (Minimax + Alpha-Beta pruning), and custom GUI rendering.

## Features

- Two modes: Player vs Player and Player vs AI
- Unbeatable AI via Minimax with Alpha-Beta pruning
- Custom Swing UI — all components hand-painted with Graphics2D (dark theme)
- Fade-in animations on marker placement
- Win highlighting with golden accent
- Score tracking across rounds
- AI runs on a background SwingWorker thread

## Architecture (MVC)

```
TicTacToe/
├── src/tictactoe/
│   ├── Main.java
│   ├── model/
│   │   ├── Board.java          ← Board state & win detection
│   │   ├── GameState.java      ← Enum: IN_PROGRESS / X_WINS / O_WINS / DRAW
│   │   ├── GameMode.java       ← Enum: PLAYER_VS_PLAYER / PLAYER_VS_AI
│   │   └── ScoreTracker.java   ← Cross-round counters
│   ├── ai/
│   │   └── MinimaxAI.java      ← Alpha-beta pruned Minimax
│   ├── controller/
│   │   └── GameController.java ← Turn logic, AI scheduling
│   └── view/
│       ├── GameView.java       ← Interface decoupling controller ↔ view
│       └── MainWindow.java     ← Swing window + custom CellButton
├── pom.xml
├── build.sh
└── README.md
```

## How to Build & Run

### Option 1 — Shell script (JDK 17+)
```bash
chmod +x build.sh
./build.sh
```

### Option 2 — Maven
```bash
mvn compile
mvn exec:java -Dexec.mainClass="tictactoe.Main"
```

### Option 3 — Manual javac
```bash
mkdir -p out
find src -name "*.java" | xargs javac -d out
java -cp out tictactoe.Main
```

## Requirements
- Java 17+ (uses switch expressions and records)
- No external dependencies — pure Java SE
