import org.junit.Test;
import org.junit.Assert;
import othello.gamelogic.BoardSpace;
import othello.gamelogic.HumanPlayer;
import othello.gamelogic.Player;
import othello.gamelogic.Minimax;

/**
 * JUnit test class for the Minimax strategy's board evaluation function
 */
public class MinimaxTest {
    @Test
    public void testEvaluateBoardMethod() {
        // Create a test board with the specified positions
        BoardSpace[][] testBoard = createTestBoard();

        // Create players
        Player whitePlayer = new HumanPlayer();
        whitePlayer.setColor(BoardSpace.SpaceType.WHITE);

        Player blackPlayer = new HumanPlayer();
        blackPlayer.setColor(BoardSpace.SpaceType.BLACK);

        // Create Minimax instance
        Minimax minimax = new Minimax();

        // Set up the minimax instance with a makeMove call
        // This initializes the maximizingPlayer and minimizingPlayer fields
        minimax.makeMove(testBoard, whitePlayer);

        // Now call the public evaluateBoard method
        int actualResult = minimax.evaluateBoard(testBoard);

        // Check the result
        int expectedResult = -22;
        Assert.assertEquals("Board evaluation should match expected value", expectedResult, actualResult);
    }

    @Test
    public void testMoveSelection() {
        // Create a test board with the specified positions
        BoardSpace[][] testBoard = createTestBoard();

        // Create white player (maximizing player)
        Player whitePlayer = new HumanPlayer();
        whitePlayer.setColor(BoardSpace.SpaceType.WHITE);

        // Create Minimax instance
        Minimax minimax = new Minimax();

        // Get the move selected by the minimax algorithm
        BoardSpace selectedMove = minimax.makeMove(testBoard, whitePlayer);

        // Print the selected move for debugging
        System.out.println("Minimax selected move: [" + selectedMove.getX() + "][" + selectedMove.getY() + "]");

        // Check if the selected move is one of the expected moves
        boolean isExpectedMove = (selectedMove.getX() == 2 && selectedMove.getY() == 2) ||
                (selectedMove.getX() == 4 && selectedMove.getY() == 2);

        Assert.assertTrue("Minimax should select either [2][2] or [4][2] as the best move", isExpectedMove);
    }

    /**
     * Creates a test board with the specified piece positions
     */
    private BoardSpace[][] createTestBoard() {
        // Create an 8x8 empty board
        BoardSpace[][] board = new BoardSpace[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new BoardSpace(i, j, BoardSpace.SpaceType.EMPTY);
            }
        }

        // Set the white pieces (3 pieces)
        board[2][4].setType(BoardSpace.SpaceType.WHITE);
        board[3][4].setType(BoardSpace.SpaceType.WHITE);
        board[4][4].setType(BoardSpace.SpaceType.WHITE);

        // Set the black pieces (6 pieces)
        board[0][5].setType(BoardSpace.SpaceType.BLACK);
        board[1][4].setType(BoardSpace.SpaceType.BLACK);
        board[2][3].setType(BoardSpace.SpaceType.BLACK);
        board[2][5].setType(BoardSpace.SpaceType.BLACK);
        board[3][3].setType(BoardSpace.SpaceType.BLACK);
        board[4][3].setType(BoardSpace.SpaceType.BLACK);

        return board;
    }
}