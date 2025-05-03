package MLRegression;

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
    private boolean[] players;
    /**
     * This is a 3-d byte array representing each board before each of 60 moves
     */
    private byte[][][] boardStates;

    private OthelloGame og;
    private Player player1;
    private Player player2;
    int forfeit;
    public OthelloGameInterperter(byte[] moves) {
        //byte array of moves. Bytes correspond to 2-digit int where x is ones digit and y is tens
        this.moves = moves;
        //bytes 3d array of 60 8x8 boards
        boardStates = new byte[60][8][8];
        //boolean array of what player is choosing the move
        players = new boolean[60];
        //signifies what 0 indexed turn a player forfeited. -1 when game completed
        forfeit = -1;

        //players for the game
        player1 = new HumanPlayer();
        player2 = new HumanPlayer();

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
        //the current board thats being processed
        int curBoardStateNum = 0;
        //wether the current player is player one
        boolean currentlyPlayerOne = true;

        //iterate over moves
        for (byte curMove: moves) {
            //save boardstate before move
            boardStates[curBoardStateNum] = boardStateConverter(og.getBoard());
            //save what player is currently making the move
            players[curBoardStateNum] = currentlyPlayerOne;

            //int representing current move
            int convertedMove = byteToInt(curMove);
            //if current move is 0, game was forfeited
            if (convertedMove == 0) {
                forfeit = curBoardStateNum;
                break;
            }

            //convert 2 digit number to coordinates, subtract 1 since coordinates for othellogame are 0-indexed while
            // bytes[] is 1 -indexed
            int x = convertedMove / 10 - 1;
            int y = convertedMove % 10 - 1;

            //map of available moves for current player and list of board spaces they flip
            Map<BoardSpace, List<BoardSpace>> availableMoves = og.getAvailableMoves(curPlayer);

            //boolean to see whether actual move exists
            boolean moveExists = false;
            //the move that is selected
            BoardSpace selectedMove = null;
            for (BoardSpace move: availableMoves.keySet()) {
                //check if move was selected
                if (move.getX() == x && move.getY() == y) {
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
            ++curBoardStateNum;
        }
    }


    // convert bytes to ints
    private static int byteToInt(byte b) {
        int move = (b & 0xFF);
        int tens = move / 10;
        int ones = move % 10;
        int swappedMove = ones * 10 + tens;
        return swappedMove;
    }

    private byte[][] boardStateConverter(BoardSpace[][] curBoard) {
        byte[][] ret = new byte[8][8];
        for (int x = 0; x < 8; ++x) {
            for (int y = 0; y < 8; ++y) {
                BoardSpace.SpaceType curType = curBoard[x][y].getType();
                if (curType.equals(BoardSpace.SpaceType.BLACK)) {
                    ret[x][y] = 1;
                } else if (curType.equals(BoardSpace.SpaceType.WHITE)) {
                    ret[x][y] = -1;
                } else {
                    ret[x][y] = 0;
                }
            }
        }
        return ret;
    }

}
