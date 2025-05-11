package dataProcessing;

import othello.gamelogic.BoardSpace;
import othello.gamelogic.HumanPlayer;
import othello.gamelogic.OthelloGame;
import othello.gamelogic.Player;

import java.util.List;
import java.util.Map;

/** A class that's meant to take in a 60 byte array of what moves were made in a game and store a 8x8x60 array of
 * every board state in that game
* */
public class OthelloGameInterperter {
    /**
     * This is a byte array representing each move made in x y format
     */
    private byte[] moves;
    private int[][] heat;
    private OthelloGame og;

    private Player player1;
    private Player player2;
    private int blackScore;
    private int whiteScore;

    public OthelloGameInterperter(byte[] moves, byte score) {
        //byte array of moves. Bytes correspond to 2-digit int where x is ones digit and y is tens
        this.moves = moves;
        //2d array of int values
        heat = new int[8][8];

        //players for the game
        player1 = new HumanPlayer();
        player2 = new HumanPlayer();

        player1.setColor(BoardSpace.SpaceType.BLACK);
        player2.setColor(BoardSpace.SpaceType.WHITE);

        //what the players scored at the end of the game
        blackScore = (score & 0xFF);
        whiteScore = 64 - blackScore;
        if (blackScore > 64 ) {
            System.err.println("Score is wrong");
            return;
        }

        //the othello game
        og = new OthelloGame(player1, player2);
        og.initBoard();

        //process all moves
        readMoves();
    }

    private void readMoves() {
        //current player making move
        Player curPlayer = player1;
        //the other player
        Player otherPlayer = player2;
        //wether the current player is player one
        boolean currentlyPlayerOne = true;

        //iterate over moves
        for (byte curMove : moves) {
            //int[] representing current move

            //if current move is 0, game was forfeited
            if (curMove == 0) {
                forfeit();
                break;
            }

            int[] moveCoordinates = byteToMove(curMove);

            //map of available moves for current player and list of board spaces they flip
            Map<BoardSpace, List<BoardSpace>> availableMoves = og.getAvailableMoves(curPlayer);

            //boolean to see whether actual move exists
            boolean moveExists = false;
            //the move that is selected
            BoardSpace selectedMove = null;
            for (BoardSpace move : availableMoves.keySet()) {
                //check if move was selected
                if (move.getX() == moveCoordinates[0] && move.getY() == moveCoordinates[1]) {
                    moveExists = true;
                    selectedMove = move;
                    break;
                }
            }

            //if actual move isn't valid, print error
            if (!moveExists) {
                System.err.println("Move does not exist");
                return;
            }

            //fill out heatmap
            if (currentlyPlayerOne) {
                heat[moveCoordinates[0]][moveCoordinates[1]] = blackScore;
            } else {
                heat[moveCoordinates[0]][moveCoordinates[1]] = whiteScore;
            }

            //take spaces changing the board
            og.takeSpaces(curPlayer, otherPlayer, availableMoves, selectedMove);

            //check if otherPlayer has moves
            if (!og.getAvailableMoves(otherPlayer).isEmpty()) {
                //other player has moves

                //change player
                currentlyPlayerOne = !currentlyPlayerOne;
                Player temp = curPlayer;
                curPlayer = otherPlayer;
                otherPlayer = temp;

            }
            //other player has no moves, skip their turn
            //do not change player
        }
    }

    /**
     * if the game was forfeited, fill out the rest of the spaces with a neutral score of 32.
     */
    private void forfeit() {
        for (int i = 0; i < heat.length; i++) {
            for (int j = 0; j < heat[i].length; j++) {
                if ((i == 3 || i == 4) && (j == 3 || j == 4)) {
                    continue;
                }
                if (heat[i][j] == 0) {
                    heat[i][j] = 32;
                }
            }
        }

    }

    /**
     * converts move from wthor byte format (1 indexed 10 * row + column) to othellogame coordinates
     * ( 0 indexed [column][row])
     * @param b byte of move (1 indexed 10* row + column)
     * @return array of move coordiantes ( 0 indexed [column][row])
     */
    private int[] byteToMove(byte b) {
        int[] ret = new int[2];
        if (b == 0) {
            System.err.println("0 signifies forfeit. Should be skipped");
            return null;
        }
        int move = (b & 0xFF);

        //columns/x
        ret[0] = (move % 10) - 1;
        //rows/Y
        ret[1] = (move / 10) -1;
        return ret;
    }

    public int[][] getHeatmap() {
        return heat;
    }


}
