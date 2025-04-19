package othello.gamelogic;

import java.util.*;

/**
 * Models a board of Othello.
 * Includes methods to get available moves and take spaces.
 */
public class OthelloGame {
    public static final int GAME_BOARD_SIZE = 8;

    private BoardSpace[][] board;
    private final Player playerOne;
    private final Player playerTwo;

    public OthelloGame(Player playerOne, Player playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        initBoard();
    }

    public BoardSpace[][] getBoard() {
        return board;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Player getPlayerTwo() {
        return  playerTwo;
    }

    /**
     * Returns the available moves for a player.
     * Used by the GUI to get available moves each turn.
     * @param player player to get moves for
     * @return the map of available moves,that maps destination to list of origins
     */
    public Map<BoardSpace, List<BoardSpace>> getAvailableMoves(Player player) {
        return player.getAvailableMoves(board);
    }

    /**
     * Initializes the board at the start of the game with all EMPTY spaces.
     */
    public void initBoard() {
        board = new BoardSpace[GAME_BOARD_SIZE][GAME_BOARD_SIZE];
        for (int i = 0; i < GAME_BOARD_SIZE; i++) {
            for (int j = 0; j < GAME_BOARD_SIZE; j++) {
                board[i][j] = new BoardSpace(i, j, BoardSpace.SpaceType.EMPTY);
            }
        }
        takeSpace(playerTwo, playerOne, 4, 4);
        takeSpace(playerTwo, playerOne, 3, 3);
        takeSpace(playerOne, playerTwo, 3, 4);
        takeSpace(playerOne, playerTwo, 4, 3);

    }

    /**
     * PART 1
     * Claims the specified space for the acting player.
     * Should also check if the space being taken is already owned by the acting player,
     * should not claim anything if acting player already owns space at (x,y)
     * @param actingPlayer the player that will claim the space at (x,y)
     * @param opponent the opposing player, will lose a space if their space is at (x,y)
     * @param x the x-coordinate of the space to claim
     * @param y the y-coordinate of the space to claim
     */
    public void takeSpace(Player actingPlayer, Player opponent, int x, int y) {
        //check if space is already owned by acting player
        BoardSpace curBoardSpace = board[x][y];
        if (curBoardSpace.getType() == opponent.getColor()) {
            opponent.getPlayerOwnedSpacesSpaces().remove(curBoardSpace);
        }
        curBoardSpace.setType(actingPlayer.getColor());
        if (!actingPlayer.getPlayerOwnedSpacesSpaces().contains(curBoardSpace)) {
            actingPlayer.getPlayerOwnedSpacesSpaces().add(curBoardSpace);
        }
    }

    /**
     * PART 1
     * Claims spaces from all origins that lead to a specified destination.
     * This is called when a player, human or computer, selects a valid destination.
     * @param actingPlayer the player that will claim spaces
     * @param opponent the opposing player, that may lose spaces
     * @param availableMoves map of the available moves, that maps destination to list of origins
     * @param selectedDestination the specific destination that a HUMAN player selected
     */
    public void takeSpaces(Player actingPlayer, Player opponent,
                           Map<BoardSpace, List<BoardSpace>> availableMoves,
                           BoardSpace selectedDestination) {
        List<BoardSpace> origins = availableMoves.get(selectedDestination);
        for (BoardSpace origin : origins) {
            if (origin.getX() == selectedDestination.getX()) {
                //these two are in same column;
                int direction = origin.getY() - selectedDestination.getY();
                if (direction > 0) {
                    //origin must travel negative y direction
                    for (int y = origin.getY(); y >= selectedDestination.getY(); --y) {
                        takeSpace(actingPlayer, opponent, origin.getX(), y);
                    }
                } else {
                    //origin must travel positive y direction
                    for (int y = origin.getY(); y <= selectedDestination.getY(); ++y) {
                        takeSpace(actingPlayer, opponent, origin.getX(), y);
                    }
                }
                continue;
            }

            if (origin.getY() == selectedDestination.getY()) {
                //these two are in same row;
                int direction = origin.getX() - selectedDestination.getX();
                if (direction > 0) {
                    //origin must travel negative x direction
                    for (int x = origin.getX(); x >= selectedDestination.getX(); --x) {
                        takeSpace(actingPlayer, opponent, x, origin.getY());
                    }
                } else {
                    //origin must travel positive x direction
                    for (int x = origin.getX(); x <= selectedDestination.getX(); ++x) {
                        takeSpace(actingPlayer, opponent, x, origin.getY());
                    }
                }
                continue;
            }

            if (origin.getX() > selectedDestination.getX()) {
                //these two are diagonally oriented from each other
                int direction = origin.getY() - selectedDestination.getY();
                if (direction > 0) {
                    //origin must travel negative x and y direction
                    for (int xy = 0; xy <= direction; ++xy) {

                        takeSpace(actingPlayer, opponent, origin.getX() - xy, origin.getY() - xy);
                    }
                } else {
                    //origin must travel negative x and positive y direction
                    for (int xy = 0; xy <= -direction; ++xy) {
                        takeSpace(actingPlayer, opponent, origin.getX() - xy, origin.getY() + xy);
                    }
                }
                continue;
            }
            if (origin.getX() < selectedDestination.getX()) {
                //these two are diagonally oriented from each other
                int direction = origin.getY() - selectedDestination.getY();
                if (direction > 0) {
                    //origin must travel positive x and negative y direction
                    for (int xy = 0; xy <= direction; ++xy) {
                        takeSpace(actingPlayer, opponent, origin.getX() + xy, origin.getY() - xy);
                    }
                } else {
                    //origin must travel positive x and positive y direction
                    for (int xy = 0; xy <= -direction; ++xy) {
                        takeSpace(actingPlayer, opponent, origin.getX() + xy, origin.getY() + xy);
                    }
                }
            }
        }
    }

    /**
     * PART 2
     *
     * Gets the computer decision for its turn.
     * Should call a method within the ComputerPlayer class that returns a BoardSpace
     * using a specific strategy.
     * @param computer computer player that is deciding their move for their turn
     * @return the BoardSpace that was decided upon
     */
    public BoardSpace computerDecision(ComputerPlayer computer) {
        return computer.makeMove(board);
    }

}
