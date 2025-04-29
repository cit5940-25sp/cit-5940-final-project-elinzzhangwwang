package othello.gamelogic;

import othello.Constants;
import java.util.*;

public class MCTS implements Strategy {
    private static final int NUM_ITERATIONS = 1000;
    private Player computerPlayer;
    private Player opponent;

    @Override
    public BoardSpace makeMove(BoardSpace[][] board, Player p1) {
        computerPlayer = p1;
        opponent = createOpponent(p1);
        MCTSNode root = new MCTSNode(board, computerPlayer.getColor(), null, null);

        //run 1000 MCTS iterations
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            MCTSNode node = select(root);
            MCTSNode expandedNode = expand(node);
            if (expandedNode == null) continue;
            double result = simulate(expandedNode);
            backPropagate(expandedNode, result);
        }

        //find the best move/child with max visits
        MCTSNode bestNode = null;
        int maxVisits = -1;

        for (MCTSNode child : root.getChildren()) {
            if (child.getVisits() > maxVisits) {
                maxVisits = child.getVisits();
                bestNode = child;
            }
        }

        //check if no move can be made
        if (bestNode == null || bestNode.getMove() == null) {
            return null;
        }

        //get the coordinates of this best move
        BoardSpace bestMove = bestNode.getMove();
        int x = bestMove.getX();
        int y = bestMove.getY();

        //if the coordinates are within bound of the board, return this move
        if (x >= 0 && x < board.length &&
                y >= 0 && y < board[0].length) {
            return board[x][y];
        }
        return null;
    }

    private MCTSNode select(MCTSNode node) {
        //Traverse the tree along nodes with max UCT values until a leaf node is found
        while (!node.isLeaf()) {
            if (!node.isFullyExpanded()) {
                return node;
            }

            //if th current node has been fully expanded, find the child with highest UCT value
            MCTSNode bestChild = null;
            double bestValue = Double.NEGATIVE_INFINITY;
            for (MCTSNode child : node.getChildren()) {
                double uctValue;
                //if the child has not been visited, set its UCT value to positive infinity
                if (child.getVisits() == 0) {
                    uctValue = Double.POSITIVE_INFINITY;
                } else {
                    uctValue = (child.getWins() / child.getVisits()) +
                            Constants.EXPLORATION_PARAM * Math.sqrt(Math.log(node.getVisits()) / child.getVisits());
                }
                if (uctValue > bestValue) {
                    bestValue = uctValue;
                    bestChild = child;
                }
            }

            if (bestChild == null) {
                return node;
            }
            node = bestChild;
        }
        return node;
    }

    private MCTSNode expand(MCTSNode node) {
        Player currentPlayer = new HumanPlayer();
        currentPlayer.setColor(node.getNextPlayer());
        Map<BoardSpace, List<BoardSpace>> availableMoves = currentPlayer.getAvailableMoves(node.getBoardState());

        if (availableMoves.isEmpty()) {
            return null;
        }

        //find all expandable nodes/moves
        for (BoardSpace move : availableMoves.keySet()) {
            boolean alreadyExpanded = false;
            for (MCTSNode child : node.getChildren()) {
                if (child.getMove().equals(move)) {
                    alreadyExpanded = true;
                    break;
                }
            }

            if (!alreadyExpanded) {
                BoardSpace[][] newBoard = deepCopyBoard(node.getBoardState());
                simulateTakeSpaces(newBoard, availableMoves, move, node.getNextPlayer());

                BoardSpace.SpaceType nextPlayer;
                if (node.getNextPlayer() == computerPlayer.getColor()) {
                    nextPlayer = opponent.getColor();
                } else {
                    nextPlayer = computerPlayer.getColor();
                }

                MCTSNode child = new MCTSNode(newBoard, nextPlayer, node, move);
                node.addChild(child);
                return child;
            }
        }

        return null;
    }

    private double simulate(MCTSNode node) {
        BoardSpace[][] simulatedBoard = deepCopyBoard(node.getBoardState());
        BoardSpace.SpaceType currentPlayerColor = node.getNextPlayer();

        Player activePlayer = new HumanPlayer();
        activePlayer.setColor(currentPlayerColor);
        Player waitingPlayer = createOpponent(activePlayer);

        while (true) {
            Map<BoardSpace, List<BoardSpace>> availableMoves = activePlayer.getAvailableMoves(simulatedBoard);

            if (availableMoves.isEmpty()) {
                Map<BoardSpace, List<BoardSpace>> opponentMoves = waitingPlayer.getAvailableMoves(simulatedBoard);
                if (opponentMoves.isEmpty()) {
                    return calculateResult(simulatedBoard);
                }
                //skip current player, and swap players
                Player playerSwap = activePlayer;
                activePlayer = waitingPlayer;
                waitingPlayer = playerSwap;
            } else {
                //current player make a random move
                List<BoardSpace> moves = new ArrayList<>(availableMoves.keySet());
                int randomIndex = (int) (Math.random() * moves.size());
                BoardSpace randomMove = moves.get(randomIndex);
                simulateTakeSpaces(simulatedBoard, availableMoves, randomMove, activePlayer.getColor());

                //swap players after move made
                Player playerSwap = activePlayer;
                activePlayer = waitingPlayer;
                waitingPlayer = playerSwap;
            }
        }
    }

    private double calculateResult(BoardSpace[][] board) {
        int computerScore = countDiscs(board, computerPlayer.getColor());
        int opponentScore = countDiscs(board, opponent.getColor());
        if (computerScore > opponentScore) return 1.0;
        if (computerScore < opponentScore) return 0.0;
        return 0.5;
    }

    private void backPropagate(MCTSNode node, double result) {
        while (node != null) {
            node.addVisits();
            node.addWins(result);
            node = node.getParent();
        }
    }

    private Player createOpponent(Player player) {
        Player opponent = new HumanPlayer();

        if (player.getColor() == BoardSpace.SpaceType.BLACK) {
            opponent.setColor(BoardSpace.SpaceType.WHITE);
        } else {
            opponent.setColor(BoardSpace.SpaceType.BLACK);
        }

        return opponent;
    }

    private int countDiscs(BoardSpace[][] board, BoardSpace.SpaceType color) {
        int count = 0;
        for (BoardSpace[] row : board) {
            for (BoardSpace space : row) {
                if (space.getType() == color) count++;
            }
        }
        return count;
    }

    private BoardSpace[][] deepCopyBoard(BoardSpace[][] original) {
        BoardSpace[][] copy = new BoardSpace[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[i].length; j++) {
                copy[i][j] = new BoardSpace(original[i][j]);
            }
        }
        return copy;
    }

    //same simulateTakeSpaces method borrowed from Minimax
    private void simulateTakeSpaces(BoardSpace[][] simulatedBoard,
                                    Map<BoardSpace, List<BoardSpace>> availableMoves,
                                    BoardSpace selectedDestination,
                                    BoardSpace.SpaceType playerColor) {
        simulatedBoard[selectedDestination.getX()][selectedDestination.getY()].setType(playerColor);

        List<BoardSpace> origins = availableMoves.get(selectedDestination);
        for (BoardSpace origin : origins) {
            if (origin.getX() == selectedDestination.getX()) {
                int direction = origin.getY() - selectedDestination.getY();
                if (direction > 0) {
                    for (int y = origin.getY(); y > selectedDestination.getY(); --y) {
                        simulatedBoard[origin.getX()][y].setType(playerColor);
                    }
                } else {
                    for (int y = origin.getY(); y < selectedDestination.getY(); ++y) {
                        simulatedBoard[origin.getX()][y].setType(playerColor);
                    }
                }
                continue;
            }

            if (origin.getY() == selectedDestination.getY()) {
                int direction = origin.getX() - selectedDestination.getX();
                if (direction > 0) {
                    for (int x = origin.getX(); x > selectedDestination.getX(); --x) {
                        simulatedBoard[x][origin.getY()].setType(playerColor);
                    }
                } else {
                    for (int x = origin.getX(); x < selectedDestination.getX(); ++x) {
                        simulatedBoard[x][origin.getY()].setType(playerColor);
                    }
                }
                continue;
            }

            if (origin.getX() > selectedDestination.getX()) {
                int direction = origin.getY() - selectedDestination.getY();
                if (direction > 0) {
                    for (int xy = 0; xy < direction; ++xy) {
                        simulatedBoard[origin.getX() - xy][origin.getY() - xy].setType(playerColor);
                    }
                } else {
                    for (int xy = 0; xy < -direction; ++xy) {
                        simulatedBoard[origin.getX() - xy][origin.getY() + xy].setType(playerColor);
                    }
                }
                continue;
            }
            if (origin.getX() < selectedDestination.getX()) {
                int direction = origin.getY() - selectedDestination.getY();
                if (direction > 0) {
                    for (int xy = 0; xy < direction; ++xy) {
                        simulatedBoard[origin.getX() + xy][origin.getY() - xy].setType(playerColor);
                    }
                } else {
                    for (int xy = 0; xy < -direction; ++xy) {
                        simulatedBoard[origin.getX() + xy][origin.getY() + xy].setType(playerColor);
                    }
                }
            }
        }
    }
}
