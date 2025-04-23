package othello.gamelogic;

import othello.Constants;

import java.util.*;

public class Minimax implements Strategy {
    private static final int MAX_DEPTH = 5; // Depth of minimax search
    private Player maximizingPlayer; // The computer player
    private Player minimizingPlayer; // The opponent player

    @Override
    public BoardSpace makeMove(BoardSpace[][] board, Player p1) {
        // p1 is the maximizing player (computer player)
        maximizingPlayer = p1;
        // Create opponent for simulation purposes
        minimizingPlayer = createOpponent(p1);

        // Get available moves for maximizing player
        Map<BoardSpace, List<BoardSpace>> availableMoves = maximizingPlayer.getAvailableMoves(board);

        // If no moves available, return null immediately
        if (availableMoves.isEmpty()) {
            return null;
        }

        // Create root node with the current board state
        MiniNode root = new MiniNode(deepCopyBoard(board));

        // Initialize best move and best score
        BoardSpace bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        // For each possible move, create a simulated board and add as a child to root
        for (BoardSpace destination : availableMoves.keySet()) {
            // Create a simulated board
            BoardSpace[][] simulatedBoard = deepCopyBoard(board);

            // Apply the move on the simulated board
            simulateTakeSpaces(simulatedBoard, availableMoves, destination, maximizingPlayer.getColor());

            // Create a child node for this move
            MiniNode childNode = new MiniNode(simulatedBoard);
            root.addNode(childNode);
        }

        // Now evaluate each child of the root using minimax with alpha-beta pruning
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (int i = 0; i < root.getChildCount(); i++) {
            MiniNode child = root.children.get(i);
            int score = minimaxAlphaBeta(child, MAX_DEPTH - 1, alpha, beta, false);

            // Update best move if this score is better
            if (score > bestScore) {
                bestScore = score;
                bestMove = getDestinationFromChildren(availableMoves, i);
            }

            // Update alpha (best already explored option for maximizer)
            alpha = Math.max(alpha, bestScore);
        }

        return bestMove;
    }

    /**
     * Helper method to get the corresponding destination BoardSpace from the list of available moves
     * @param availableMoves The map of available moves
     * @param index The index of the child in the root's children list
     * @return The corresponding destination BoardSpace
     */
    private BoardSpace getDestinationFromChildren(Map<BoardSpace, List<BoardSpace>> availableMoves, int index) {
        // Convert the keyset to a list to get by index
        List<BoardSpace> destinations = new ArrayList<>(availableMoves.keySet());
        if (index < destinations.size()) {
            return destinations.get(index);
        }
        return null;
    }

    /**
     * The minimax algorithm implementation with alpha-beta pruning.
     * Alpha is the best value that the maximizer currently can guarantee.
     * Beta is the best value that the minimizer currently can guarantee.
     * If alpha >= beta, we can prune the branch (stop exploring it).
     */
    private int minimaxAlphaBeta(MiniNode position, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        // Base case: leaf node or maximum depth reached
        if (depth == 0 || isGameOver(position.boardValue)) {
            return evaluateBoard(position.boardValue);
        }

        // Get the current player based on whether we're maximizing or minimizing
        Player currentPlayer = isMaximizingPlayer ? maximizingPlayer : minimizingPlayer;

        // Get available moves for current player
        Map<BoardSpace, List<BoardSpace>> availableMoves = currentPlayer.getAvailableMoves(position.boardValue);

        // If no moves available, skip turn by switching player
        if (availableMoves.isEmpty()) {
            return minimaxAlphaBeta(position, depth - 1, alpha, beta, !isMaximizingPlayer);
        }

        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;

            // For each possible move, create a child node
            for (BoardSpace destination : availableMoves.keySet()) {
                // Create a simulated board
                BoardSpace[][] simulatedBoard = deepCopyBoard(position.boardValue);

                // Apply the move on the simulated board
                simulateTakeSpaces(simulatedBoard, availableMoves, destination, currentPlayer.getColor());

                // Create a child node for this move
                MiniNode childNode = new MiniNode(simulatedBoard);
                position.addNode(childNode);

                // Recursively evaluate this child
                int eval = minimaxAlphaBeta(childNode, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);

                // Update alpha
                alpha = Math.max(alpha, eval);

                // Alpha-beta pruning
                if (beta <= alpha) {
                    break; // Beta cutoff
                }
            }

            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;

            // For each possible move, create a child node
            for (BoardSpace destination : availableMoves.keySet()) {
                // Create a simulated board
                BoardSpace[][] simulatedBoard = deepCopyBoard(position.boardValue);

                // Apply the move on the simulated board
                simulateTakeSpaces(simulatedBoard, availableMoves, destination, currentPlayer.getColor());

                // Create a child node for this move
                MiniNode childNode = new MiniNode(simulatedBoard);
                position.addNode(childNode);

                // Recursively evaluate this child
                int eval = minimaxAlphaBeta(childNode, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);

                // Update beta
                beta = Math.min(beta, eval);

                // Alpha-beta pruning
                if (beta <= alpha) {
                    break; // Alpha cutoff
                }
            }

            return minEval;
        }
    }

    /**
     * Original minimax method without alpha-beta pruning (kept for reference or testing)
     */
    private int minimax(MiniNode position, int depth, boolean isMaximizingPlayer) {
        // Base case: leaf node or maximum depth reached
        if (depth == 0 || isGameOver(position.boardValue)) {
            return evaluateBoard(position.boardValue);
        }

        // Get the current player based on whether we're maximizing or minimizing
        Player currentPlayer = isMaximizingPlayer ? maximizingPlayer : minimizingPlayer;

        // Get available moves for current player
        Map<BoardSpace, List<BoardSpace>> availableMoves = currentPlayer.getAvailableMoves(position.boardValue);

        // If no moves available, skip turn by switching player
        if (availableMoves.isEmpty()) {
            return minimax(position, depth - 1, !isMaximizingPlayer);
        }

        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;

            // For each possible move, create a child node
            for (BoardSpace destination : availableMoves.keySet()) {
                // Create a simulated board
                BoardSpace[][] simulatedBoard = deepCopyBoard(position.boardValue);

                // Apply the move on the simulated board
                simulateTakeSpaces(simulatedBoard, availableMoves, destination, currentPlayer.getColor());

                // Create a child node for this move
                MiniNode childNode = new MiniNode(simulatedBoard);
                position.addNode(childNode);

                // Recursively evaluate this child
                int eval = minimax(childNode, depth - 1, false);
                maxEval = Math.max(maxEval, eval);
            }

            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;

            // For each possible move, create a child node
            for (BoardSpace destination : availableMoves.keySet()) {
                // Create a simulated board
                BoardSpace[][] simulatedBoard = deepCopyBoard(position.boardValue);

                // Apply the move on the simulated board
                simulateTakeSpaces(simulatedBoard, availableMoves, destination, currentPlayer.getColor());

                // Create a child node for this move
                MiniNode childNode = new MiniNode(simulatedBoard);
                position.addNode(childNode);

                // Recursively evaluate this child
                int eval = minimax(childNode, depth - 1, true);
                minEval = Math.min(minEval, eval);
            }

            return minEval;
        }
    }

    public int testEvaluateBoard(BoardSpace[][] board) {
        return evaluateBoard(board);
    }

    /**
     * Evaluates a board state by summing the weighted positions for each player
     * and returning the difference (maximizer - minimizer)
     */
    public int evaluateBoard(BoardSpace[][] board) {
        int maximizerScore = 0;
        int minimizerScore = 0;

        // Sum up the weights of all spaces for each player
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].getType() == maximizingPlayer.getColor()) {
                    maximizerScore += Constants.BOARD_WEIGHTS[i][j];
                } else if (board[i][j].getType() == minimizingPlayer.getColor()) {
                    minimizerScore += Constants.BOARD_WEIGHTS[i][j];
                }
            }
        }

        // Return the difference (maximizer - minimizer)
        return maximizerScore - minimizerScore;
    }

    /**
     * Checks if the game is over by seeing if either player can make a move
     */
    private boolean isGameOver(BoardSpace[][] board) {
        Map<BoardSpace, List<BoardSpace>> maxMoves = maximizingPlayer.getAvailableMoves(board);
        Map<BoardSpace, List<BoardSpace>> minMoves = minimizingPlayer.getAvailableMoves(board);

        return maxMoves.isEmpty() && minMoves.isEmpty();
    }

    /**
     * Creates a deep copy of the board for simulation
     */
    private BoardSpace[][] deepCopyBoard(BoardSpace[][] original) {
        BoardSpace[][] copy = new BoardSpace[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[i].length; j++) {
                copy[i][j] = new BoardSpace(original[i][j]); // Using the copy constructor
            }
        }
        return copy;
    }

    /**
     * Creates an opponent player with the opposite color
     */
    private Player createOpponent(Player player) {
        Player opponent = new HumanPlayer(); // Using HumanPlayer as a container

        // Set the opposite color
        if (player.getColor() == BoardSpace.SpaceType.BLACK) {
            opponent.setColor(BoardSpace.SpaceType.WHITE);
        } else {
            opponent.setColor(BoardSpace.SpaceType.BLACK);
        }

        return opponent;
    }

    /**
     * Simulates taking spaces on a copied board (doesn't affect the original game board)
     */
    private void simulateTakeSpaces(BoardSpace[][] simulatedBoard,
                                    Map<BoardSpace, List<BoardSpace>> availableMoves,
                                    BoardSpace selectedDestination,
                                    BoardSpace.SpaceType playerColor) {
        // First, set the destination to the player's color
        simulatedBoard[selectedDestination.getX()][selectedDestination.getY()].setType(playerColor);

        List<BoardSpace> origins = availableMoves.get(selectedDestination);
        for (BoardSpace origin : origins) {
            if (origin.getX() == selectedDestination.getX()) {
                // These two are in same column
                int direction = origin.getY() - selectedDestination.getY();
                if (direction > 0) {
                    // Origin must travel negative y direction
                    for (int y = origin.getY(); y > selectedDestination.getY(); --y) {
                        simulatedBoard[origin.getX()][y].setType(playerColor);
                    }
                } else {
                    // Origin must travel positive y direction
                    for (int y = origin.getY(); y < selectedDestination.getY(); ++y) {
                        simulatedBoard[origin.getX()][y].setType(playerColor);
                    }
                }
                continue;
            }

            if (origin.getY() == selectedDestination.getY()) {
                // These two are in same row
                int direction = origin.getX() - selectedDestination.getX();
                if (direction > 0) {
                    // Origin must travel negative x direction
                    for (int x = origin.getX(); x > selectedDestination.getX(); --x) {
                        simulatedBoard[x][origin.getY()].setType(playerColor);
                    }
                } else {
                    // Origin must travel positive x direction
                    for (int x = origin.getX(); x < selectedDestination.getX(); ++x) {
                        simulatedBoard[x][origin.getY()].setType(playerColor);
                    }
                }
                continue;
            }

            if (origin.getX() > selectedDestination.getX()) {
                // These two are diagonally oriented from each other
                int direction = origin.getY() - selectedDestination.getY();
                if (direction > 0) {
                    // Origin must travel negative x and y direction
                    for (int xy = 0; xy < direction; ++xy) {
                        simulatedBoard[origin.getX() - xy][origin.getY() - xy].setType(playerColor);
                    }
                } else {
                    // Origin must travel negative x and positive y direction
                    for (int xy = 0; xy < -direction; ++xy) {
                        simulatedBoard[origin.getX() - xy][origin.getY() + xy].setType(playerColor);
                    }
                }
                continue;
            }
            if (origin.getX() < selectedDestination.getX()) {
                // These two are diagonally oriented from each other
                int direction = origin.getY() - selectedDestination.getY();
                if (direction > 0) {
                    // Origin must travel positive x and negative y direction
                    for (int xy = 0; xy < direction; ++xy) {
                        simulatedBoard[origin.getX() + xy][origin.getY() - xy].setType(playerColor);
                    }
                } else {
                    // Origin must travel positive x and positive y direction
                    for (int xy = 0; xy < -direction; ++xy) {
                        simulatedBoard[origin.getX() + xy][origin.getY() + xy].setType(playerColor);
                    }
                }
            }
        }
    }
}