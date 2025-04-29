import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import othello.gamelogic.BoardSpace;
import othello.gamelogic.HumanPlayer;
import othello.gamelogic.MCTS;
import othello.gamelogic.Player;

public class MCTSTest {

    private MCTS mcts;
    private BoardSpace[][] board;
    private Player player;

    @BeforeEach
    public void setUp() {
        mcts = new MCTS();
        player = new HumanPlayer();
        player.setColor(BoardSpace.SpaceType.BLACK);

        board = new BoardSpace[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new BoardSpace(i, j, BoardSpace.SpaceType.EMPTY);
            }
        }

        board[3][3].setType(BoardSpace.SpaceType.WHITE);
        board[4][4].setType(BoardSpace.SpaceType.WHITE);
        board[3][4].setType(BoardSpace.SpaceType.BLACK);
        board[4][3].setType(BoardSpace.SpaceType.BLACK);
    }


    @Test
    public void testMakeMoves() {
        for (int i = 0; i < 5; i++) {
            BoardSpace move = mcts.makeMove(board, player);
            assertNotNull(move);

            Map<BoardSpace, List<BoardSpace>> availableMoves = player.getAvailableMoves(board);
            boolean validMove = false;

            for (BoardSpace availableMove : availableMoves.keySet()) {
                if (move.getX() == availableMove.getX() && move.getY() == availableMove.getY()) {
                    validMove = true;
                    break;
                }
            }
            assertTrue(validMove);
        }
    }

    @Test
    public void testMakeMoveWithFullBoard() {
        BoardSpace[][] fullBoard = new BoardSpace[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                fullBoard[i][j] = new BoardSpace(i, j, BoardSpace.SpaceType.WHITE);
            }
        }

        BoardSpace move = mcts.makeMove(fullBoard, player);
        assertNull(move);
    }
}