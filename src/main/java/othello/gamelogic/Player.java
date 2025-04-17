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
        Map<BoardSpace, List<BoardSpace>> ret = new HashMap<>();
        //go over rows
        for (BoardSpace[] i:  board) {
            //System.out.println("row: "+i);
            Map<BoardSpace, List<BoardSpace>> rowsKV = checkValid(i);
            mergeMaps(ret, rowsKV);
        }
        //go over columns
        for (int j = 0; j < board.length; ++j) {
            BoardSpace[] col = new BoardSpace[board.length];
            for (int i = 0; i < board.length; ++i) {
                col[i] = board[i][j];
            }
            Map<BoardSpace, List<BoardSpace>> colKV = checkValid(col);
            mergeMaps(ret, colKV);
        }
        //go over top left to bottom right diagonals
        for (int i = board.length-1; i >= 0; --i) {
            BoardSpace[] tldiag = new BoardSpace[board.length-i];
            for (int j = 0; j < board.length-i; ++j) {
                tldiag[j] = board[i+j][j];
            }
            Map<BoardSpace, List<BoardSpace>> colKV = checkValid(tldiag);
            mergeMaps(ret, colKV);
        }
        for (int j = 1; j < board.length; ++j) {
            BoardSpace[] tldiag = new BoardSpace[board.length-j];
            for (int i = 0; i < board.length-j; ++i) {
                tldiag[i] = board[i][j+i];
            }
            Map<BoardSpace, List<BoardSpace>> colKV = checkValid(tldiag);
            mergeMaps(ret, colKV);
        }

        //go over top right to bottom left diagonals
        for (int j = 0; j < board.length; ++j) {
            BoardSpace[] trdiag = new BoardSpace[j+1];
            for (int i = 0; i < trdiag.length; ++i) {
                trdiag[i] = board[i][j-i];
            }
            Map<BoardSpace, List<BoardSpace>> colKV = checkValid(trdiag);
            mergeMaps(ret, colKV);
        }
        for (int i = 1; i < board.length; ++i) {
            BoardSpace[] trdiag = new BoardSpace[board.length-i];
            for (int j = board.length-1; j >= i; --j) {
                trdiag[board.length-1-j] = board[i+board.length-j-1][j];
            }
            Map<BoardSpace, List<BoardSpace>> colKV = checkValid(trdiag);
            mergeMaps(ret, colKV);
        }

        return ret;
    }
    private void mergeMaps(Map<BoardSpace, List<BoardSpace>> mainMap, Map<BoardSpace, List<BoardSpace>> tempMap) {
        for (BoardSpace key: tempMap.keySet()) {
            mainMap.putIfAbsent(key, new ArrayList<>());
            for (BoardSpace origins: tempMap.get(key)) {
                mainMap.get(key).add(origins);
            }
        }
    }
    /**
     *
     * Helper method for getAvailableMoves. Takes in an array of BoardSpace that corresponds to the current row, column
     * or diagonal being examined. Returns a boolean array that correspond to wether that space is valid or not
     * @param spaces the BoardSpace objects that will be evaluated in order
     * @return a hashmap of key value pairs that correspond to destination and a list of origin
     */
    private Map<BoardSpace, List<BoardSpace>> checkValid(BoardSpace[] spaces) {
        //Hashmap that stores kv pairs of destination and list of origin
        Map<BoardSpace, List<BoardSpace>> valids = new HashMap<>();
        //corresponds to BoardSpace that is start of enemy position
        int enemyStart = -1;
        for (int i = 0; i < spaces.length; ++i) {
            //check if current board space is empty
            if (spaces[i].getType()==BoardSpace.SpaceType.EMPTY) {
                //current board space is empty so can be a potential move
                //check if there is a current enemy
                if (enemyStart != -1) {
                    //check starting  flanks of enemy

                    if (enemyStart != 0 && spaces[enemyStart-1].getType() == color) {
                        //if beginning is an ally, current is empty so current is a valid flank
                        //add current to valid moves map and ally to origins
                        valids.putIfAbsent(spaces[i], new ArrayList<>());
                        valids.get(spaces[i]).add(spaces[enemyStart-1]);
                    }
                    //if beginning is an empty square, current is empty so no flank is possible
                    //reset enemy
                    enemyStart = -1;
                }
                //there is no enemy so do nothing
            }
            //check if current board space is an enemy
            else if(spaces[i].getType() != color) {
                //check if there is a start of enemy
                if (enemyStart == -1) {
                    //if there is no start, then set enemyStart
                    enemyStart = i;
                }
                //if there is already an enemy do nothing

            }
            else {
                //current board space is an ally
                //check if there is a current enemy
                if (enemyStart != -1) {
                    //there is an enemy
                    //check if there is a piece before the enemy start
                    //check if piece before the enemy start is empty
                    if (enemyStart -1 >= 0 && spaces[enemyStart-1].getType() == BoardSpace.SpaceType.EMPTY) {
                        //if beginning is empty, current is ally so beginning is a valid flank
                        //add beginning to valid moves and current to origin
                        valids.putIfAbsent(spaces[enemyStart-1], new ArrayList<>());
                        valids.get(spaces[enemyStart-1]).add(spaces[i]);
                    }
                    //if beginning is an ally nothing can be added
                    //reset enemy
                    enemyStart = -1;
                }
                //there is no enemy so do nothing
            }



        }
        //System.out.println("empty: " +empty + " black: "+ black+ " white: " +white);
        return valids;
    }

}
