# Bomber Quest

Bomber Quest game: Desktop Game

Welcome to Bomber Quest Game, an exciting game built using the LibGDX framework! This document will guide you through the project structure,
how to run the game, and an overview of the game mechanics.

Project Structure

The project follows a clear structure to organize the game's logic, rendering, and other components. Here's an overview:

src/
└── de/tum/cit/ase/bomberquest/
├── DesktopLauncher.java               // Entry point for the desktop version of the game
├── gamemechanism/
│   ├── BomberQuestGame.java           // Core game logic and setup
│   ├── Player.java                    // Represents the player character
│   ├── Enemy.java                     // Handles enemy behavior
│   ├── Bomb.java                      // Logic for bomb placement and explosions
│   └── GameMap.java                   // Handles the map, including tiles and collisions
├── rendering/
│   ├── GameRenderer.java              // Handles rendering of the game elements
│   └── TextureLoader.java             // Utility for loading textures
├── input/
│   └── InputProcessor.java            // Handles keyboard and mouse input
├── utils/
└── Constants.java                 // Defines constants used throughout the game
libs/
├── libgdx.jar                         // LibGDX core library
├── lwjgl3.jar                         // LWJGL backend for desktop
└── desktop-file-chooser.jar           // External library for file selection
README.md                                  // This file
Class Hierarchy

DesktopLauncher: The entry point for the desktop version. Configures and launches the game.
BomberQuestGame: The central class that manages game initialization and the game loop.
Player: Represents the player, including movement, actions, and collision handling.
Enemy: Represents enemies and their AI behavior.
Bomb: Handles bomb placement, timing, and explosion effects.
GameMap: Manages the game's map, including tiles, obstacles, and the grid system.
GameRenderer: Responsible for drawing all game elements on the screen.
InputProcessor: Handles user input (e.g., keyboard and mouse).
Constants: Stores game-wide constants (e.g., screen dimensions, default speeds).
How to Run the Game

Prerequisites:
Java Development Kit (JDK) 17 or higher installed.
All dependencies (e.g., LibGDX, LWJGL3, DesktopFileChooser) available in the libs/ folder.
Compiling the Code: Run the following command to compile the code:
javac -cp "libs/*" -d out src/de/tum/cit/ase/bomberquest/DesktopLauncher.java
Running the Game: Execute the game with:
java -cp "libs/*:out" de.tum.cit.ase.bomberquest.DesktopLauncher
Creating a Runnable JAR (Optional): You can package the game into a runnable JAR file for easy execution:
jar cfe BomberQuest.jar de.tum.cit.ase.bomberquest.DesktopLauncher -C out .
java -jar BomberQuest.jar
Game Mechanics

Objective
Navigate through the map, place bombs to destroy obstacles and enemies, and reach the goal while avoiding being caught in explosions.

Core Mechanics
Player Movement:
Use arrow keys (↑, ↓, ←, →) to move the player.
Collisions with walls and obstacles are handled in the game logic.
Bomb Placement:
Press Space to place a bomb.
Bombs explode after a short delay, clearing obstacles and defeating enemies within the blast radius.
Enemies:
Enemies patrol predefined paths or actively chase the player.
Colliding with an enemy results in a loss of life.
Game Map:
The map consists of destructible and indestructible tiles.
Some tiles may hide power-ups (e.g., speed boost, extra bombs).
Lives and Game Over:
Players have a limited number of lives. Losing all lives ends the game.
Avoid bombs, explosions, and enemies to survive.
Extended Mechanics (Optional Features)

Power-ups:
Collect power-ups to enhance abilities (e.g., increased speed, larger bomb radius).
Multiple Levels:
Progress through increasingly challenging levels with different layouts and enemy types.
High Scores:
Save and display high scores locally using a file system.
Additional Notes

Game Assets: All textures, sounds, and other assets are loaded dynamically using the TextureLoader utility.
Configuration: Game settings (e.g., screen resolution, FPS) are configured in DesktopLauncher.java.
Acknowledgments

The game is powered by the LibGDX Framework.
The file chooser functionality uses Desktop File Chooser.
