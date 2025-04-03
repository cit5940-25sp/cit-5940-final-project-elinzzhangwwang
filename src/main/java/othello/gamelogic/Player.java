package othello.gamelogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract Player class for representing a player within the game.
 * All types of Players have a color and a set of owned spaces on the game board.
 */
public abstract class Player {
    private final List<BoardSpace> playerOwnedSpaces = new ArrayList<>();
    public List<BoardSpace> getPlayerOwnedSpacesSpaces() {
        return playerOwnedSpaces;
    }

    private BoardSpace.SpaceType color;
    public void setColor(BoardSpace.SpaceType color) {
        this.color = color;
    }
    public BoardSpace.SpaceType getColor() {
        return color;
    }

    /**
     * PART 1
     * TODO: Implement this method
     * Gets the available moves for this player given a certain board state.
     * This method will find destinations, empty spaces that are valid moves,
     * and map them to a list of origins that can traverse to those destinations.
     * @param board the board that will be evaluated for possible moves for this player
     * @return a map with a destination BoardSpace mapped to a List of origin BoardSpaces.
     */
    public Map<BoardSpace, List<BoardSpace>> getAvailableMoves(BoardSpace[][] board) {
        Map<BoardSpace, List<BoardSpace>> ret = new HashMap<BoardSpace, List<BoardSpace>>();
        //iterate over whole board
        for (int i = 0; i < board.length; ++i) {
            for (int j = 0; j < board[i].length; ++j) {
                BoardSpace rc = board[i][j];
                //if current piece is not empty, it is not a valid move
                if (rc.getType() != BoardSpace.SpaceType.EMPTY) {
                    continue;
                }
                //List of origins
                ArrayList<BoardSpace> origins = new ArrayList<BoardSpace>();
                //boolean for if move is valid
                boolean flank = false;
                //iterate down the rows(south) of current boardspace
                for (int rci = i+1; rci < board.length; ++rci) {
                    //check if BoardSpace is an enemy color
                    if (board[rci][j].getType() != BoardSpace.SpaceType.EMPTY
                            && board[rci][j].getType() != color) {
                        //if it is an enemy color then it can potentially be the start of a flank
                        flank = true;
                    } else if (flank) {
                        //it is an ally color, if the flank is started then this space completes the flank
                        origins.add(board[rci][j]);
                        break;
                    } else {
                        //space is an ally color and flank is not started. Invalid move
                        break;
                    }
                }

                flank = false;
                //iterate up the rows(north) of current boardspace
                for (int rci = i-1; rci >= 0; --rci) {
                    //check if BoardSpace is an enemy color
                    if (board[rci][j].getType() != BoardSpace.SpaceType.EMPTY
                            && board[rci][j].getType() != color) {
                        //if it is an enemy color then it can potentially be the start of a flank
                        flank = true;
                    } else if (flank) {
                        //it is an ally color, if the flank is started then this space completes the flank
                        origins.add(board[rci][j]);
                        break;
                    } else {
                        //space is an ally color and flank is not started. Invalid move
                        break;
                    }
                }
                flank = false;
                //iterate right of columns(east) of current boardspace
                for (int rcj = j+1; rcj <board[i].length; ++rcj) {
                    //check if BoardSpace is an enemy color
                    if (board[i][rcj].getType() != BoardSpace.SpaceType.EMPTY
                            && board[i][rcj].getType() != color) {
                        //if it is an enemy color then it can potentially be the start of a flank
                        flank = true;
                    } else if (flank) {
                        //it is an ally color, if the flank is started then this space completes the flank
                        origins.add(board[i][rcj]);
                        break;
                    } else {
                        //space is an ally color and flank is not started. Invalid move
                        break;
                    }
                }
                flank = false;
                //iterate right of columns(west) of current boardspace
                for (int rcj = j-1; rcj >= 0; --rcj) {
                    //check if BoardSpace is an enemy color
                    if (board[i][rcj].getType() != BoardSpace.SpaceType.EMPTY
                            && board[i][rcj].getType() != color) {
                        //if it is an enemy color then it can potentially be the start of a flank
                        flank = true;
                    } else if (flank) {
                        //it is an ally color, if the flank is started then this space completes the flank
                        origins.add(board[i][rcj]);
                        break;
                    } else {
                        //space is an ally color and flank is not started. Invalid move
                        break;
                    }
                }
                if (origins.isEmpty()) {
                    ret.put(board[i][j], origins);
                }
            }
        }
        return ret;
    }
}
