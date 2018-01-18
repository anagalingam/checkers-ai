Checkers AI
===========

A detailed explanation of the project can be found in the `project_report.pdf`

Quick Start Guide
-----------------

- To compile, run `javac checkers.java`

- To play, run `java checkers`

- The program first prompts the user for game type: 1) Player vs. AI, 2) AI vs. AI, 3) Player vs. Player

- The program then asks to load a starting board state. If yes, then a .txt file from the testCases directory can be loaded. If no, then the default starting checkers board will be loaded.

- If playing with an AI, the user is prompted for a move time limit in seconds for the AI. Starting player can also be set.

- On each user turn, a list of all valid moves is output. The user must choose from this list using the format "A1-B2".

Code Explanation
----------------

The project code is based in two files. The object structure is kept simple to maximize the speed performance of the AI.

- checkers.java   ->  main function and game setup parsing based on user input

- board.java      ->  core functionality including board interaction and AI move search.

The program implements a fully functional game of checkers with the ability to generate all valid moves in any board state.

The AI agent uses an iterative deepening minimax search with alpha-beta pruning to find the best move within the user-defined time limit. The board and stack of valid moves were designed to minimize memory usage and dynamic memory allocation. The checkerboard is represented by a single four int array and moves are applied to it using bitwise arithmetic operations. The valid move stack is represented by a single 12x500 array with a pointer to the top of the stack. Each valid move is represented by start, end, and jumped squares as well as a heuristic value. The minimization of dynamic memory allocation through this implementation allows for deeper searches of the game tree.

A heuristic function for the minimax search was designed to favor good board positions based on online checkers research and effective piece trading when in the piece advantage. In the end game, the heuristic function deals with king double corner situations (as seen in testCases/sampleCheckers2.txt) using a manhattan distance calculator. If in the piece advantage, the AI moves pieces to minimize the distance to opponent pieces in an attempt to force the opponent's king out of the double corner position. If in the piece disadvantage, the AI "runs away" from the opponent, attempting to force at least a draw in the situation. 

