import othello.gamelogic.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class OthelloGameTest {

    @Test
    public void testOGBasic() {
        Player p1 = new HumanPlayer();
        Player p2 = new HumanPlayer();
        p1.setColor(BoardSpace.SpaceType.BLACK);
        p2.setColor(BoardSpace.SpaceType.WHITE);

        OthelloGame og = new OthelloGame(p1, p2);
        BoardSpace[][] board = og.getBoard();

        Map<BoardSpace, List<BoardSpace>> moves = og.getAvailableMoves(og.getPlayerOne());

        List<BoardSpace> expectedMoves = new ArrayList<>();
        expectedMoves.add(board[5][4]);
        expectedMoves.add(board[4][5]);
        expectedMoves.add(board[3][2]);
        expectedMoves.add(board[2][3]);

        for (BoardSpace b:moves.keySet()) {
            assertTrue(expectedMoves.contains(b));
            expectedMoves.remove(b);
        }
        assertTrue(expectedMoves.isEmpty());
    }

    @Test
    public void testOGTake() {
        Player p1 = new HumanPlayer();
        Player p2 = new HumanPlayer();
        p1.setColor(BoardSpace.SpaceType.BLACK);
        p2.setColor(BoardSpace.SpaceType.WHITE);

        OthelloGame og = new OthelloGame(p1, p2);
        BoardSpace[][] board = og.getBoard();

        Map<BoardSpace, List<BoardSpace>> moves = og.getAvailableMoves(p1);
        og.takeSpaces(p1, p2, moves, board[5][4]);

        assertSame(BoardSpace.SpaceType.BLACK, board[5][4].getType());
        assertSame(BoardSpace.SpaceType.BLACK, board[4][4].getType());
        assertSame(BoardSpace.SpaceType.BLACK, board[3][4].getType());

        moves = og.getAvailableMoves(p2);
        List<BoardSpace> expectedMoves = new ArrayList<>();
        expectedMoves.add(board[5][3]);
        expectedMoves.add(board[5][5]);
        expectedMoves.add(board[3][5]);

        for (BoardSpace b:moves.keySet()) {
            assertTrue(expectedMoves.contains(b));
            expectedMoves.remove(b);
        }
        assertTrue(expectedMoves.isEmpty());

    }

    @Test
    public void testOGTakeFrontTake() {
        Player p1 = new HumanPlayer();
        Player p2 = new HumanPlayer();
        p1.setColor(BoardSpace.SpaceType.BLACK);
        p2.setColor(BoardSpace.SpaceType.WHITE);

        OthelloGame og = new OthelloGame(p1, p2);
        BoardSpace[][] board = og.getBoard();
        board[3][4].setType(BoardSpace.SpaceType.EMPTY);
        board[4][4].setType(BoardSpace.SpaceType.EMPTY);
        board[3][3].setType(BoardSpace.SpaceType.EMPTY);
        board[4][3].setType(BoardSpace.SpaceType.EMPTY);

        board[3][3].setType(BoardSpace.SpaceType.WHITE);
        board[4][3].setType(BoardSpace.SpaceType.WHITE);
        board[5][3].setType(BoardSpace.SpaceType.WHITE);
        board[6][3].setType(BoardSpace.SpaceType.BLACK);
        Map<BoardSpace, List<BoardSpace>> moves = og.getAvailableMoves(og.getPlayerOne());

        List<BoardSpace> expectedMoves = new ArrayList<>();
        expectedMoves.add(board[2][3]);

        for (BoardSpace b:moves.keySet()) {
            assertTrue(expectedMoves.contains(b));
            expectedMoves.remove(b);
        }
        assertTrue(expectedMoves.isEmpty());

        og.takeSpaces(p1, p2, moves, board[2][3]);

        for (int x = 0; x < board.length; ++x) {
            for (int y = 0; y < board[x].length; ++y) {

                if (x == 2 && y == 3) {
                    assertSame(BoardSpace.SpaceType.BLACK, board[x][y].getType());
                    continue;
                }
                if (x == 3 && y == 3) {
                    assertSame(BoardSpace.SpaceType.BLACK, board[x][y].getType());
                    continue;
                }
                if (x == 4 && y == 3) {
                    assertSame(BoardSpace.SpaceType.BLACK, board[x][y].getType());
                    continue;
                }
                if (x == 5 && y == 3) {
                    assertSame(BoardSpace.SpaceType.BLACK, board[x][y].getType());
                    continue;
                }
                if (x == 6 && y == 3) {
                    assertSame(BoardSpace.SpaceType.BLACK, board[x][y].getType());
                    continue;
                }
                assertSame(BoardSpace.SpaceType.EMPTY, board[x][y].getType());
            }
        }
    }

    @Test
    public void testOGTakeBackTake() {
        Player p1 = new HumanPlayer();
        Player p2 = new HumanPlayer();
        p1.setColor(BoardSpace.SpaceType.BLACK);
        p2.setColor(BoardSpace.SpaceType.WHITE);

        OthelloGame og = new OthelloGame(p1, p2);
        BoardSpace[][] board = og.getBoard();
        board[3][4].setType(BoardSpace.SpaceType.EMPTY);
        board[4][4].setType(BoardSpace.SpaceType.EMPTY);
        board[3][3].setType(BoardSpace.SpaceType.EMPTY);
        board[4][3].setType(BoardSpace.SpaceType.EMPTY);

        board[2][3].setType(BoardSpace.SpaceType.BLACK);
        board[3][3].setType(BoardSpace.SpaceType.WHITE);
        board[4][3].setType(BoardSpace.SpaceType.WHITE);
        board[5][3].setType(BoardSpace.SpaceType.WHITE);
        Map<BoardSpace, List<BoardSpace>> moves = og.getAvailableMoves(og.getPlayerOne());

        List<BoardSpace> expectedMoves = new ArrayList<>();
        expectedMoves.add(board[6][3]);

        for (BoardSpace b:moves.keySet()) {
            assertTrue(expectedMoves.contains(b));
            expectedMoves.remove(b);
        }
        assertTrue(expectedMoves.isEmpty());

        og.takeSpaces(p1, p2, moves, board[6][3]);

        for (int x = 0; x < board.length; ++x) {
            for (int y = 0; y < board[x].length; ++y) {

                if (x == 2 && y == 3) {
                    assertSame(BoardSpace.SpaceType.BLACK, board[x][y].getType());
                    continue;
                }
                if (x == 3 && y == 3) {
                    assertSame(BoardSpace.SpaceType.BLACK, board[x][y].getType());
                    continue;
                }
                if (x == 4 && y == 3) {
                    assertSame(BoardSpace.SpaceType.BLACK, board[x][y].getType());
                    continue;
                }
                if (x == 5 && y == 3) {
                    assertSame(BoardSpace.SpaceType.BLACK, board[x][y].getType());
                    continue;
                }
                if (x == 6 && y == 3) {
                    assertSame(BoardSpace.SpaceType.BLACK, board[x][y].getType());
                    continue;
                }
                assertSame(BoardSpace.SpaceType.EMPTY, board[x][y].getType());
            }
        }
    }

    @Test
    public void testOGTakeSplit() {
        Player p1 = new HumanPlayer();
        Player p2 = new HumanPlayer();
        p1.setColor(BoardSpace.SpaceType.BLACK);
        p2.setColor(BoardSpace.SpaceType.WHITE);

        OthelloGame og = new OthelloGame(p1, p2);
        BoardSpace[][] board = og.getBoard();
        board[3][4].setType(BoardSpace.SpaceType.EMPTY);
        board[4][4].setType(BoardSpace.SpaceType.EMPTY);
        board[3][3].setType(BoardSpace.SpaceType.EMPTY);
        board[4][3].setType(BoardSpace.SpaceType.EMPTY);

        board[2][3].setType(BoardSpace.SpaceType.BLACK);
        board[3][3].setType(BoardSpace.SpaceType.WHITE);
        board[5][3].setType(BoardSpace.SpaceType.WHITE);
        board[6][3].setType(BoardSpace.SpaceType.BLACK);
        Map<BoardSpace, List<BoardSpace>> moves = og.getAvailableMoves(og.getPlayerOne());

        List<BoardSpace> expectedMoves = new ArrayList<>();
        expectedMoves.add(board[4][3]);

        for (BoardSpace b:moves.keySet()) {
            assertTrue(expectedMoves.contains(b));
            expectedMoves.remove(b);
        }
        assertTrue(expectedMoves.isEmpty());

        og.takeSpaces(p1, p2, moves, board[4][3]);

        for (int x = 0; x < board.length; ++x) {
            for (int y = 0; y < board[x].length; ++y) {

                if (x == 2 && y == 3) {
                    assertSame(BoardSpace.SpaceType.BLACK, board[x][y].getType());
                    continue;
                }
                if (x == 3 && y == 3) {
                    assertSame(BoardSpace.SpaceType.BLACK, board[x][y].getType());
                    continue;
                }
                if (x == 4 && y == 3) {
                    assertSame(BoardSpace.SpaceType.BLACK, board[x][y].getType());
                    continue;
                }
                if (x == 5 && y == 3) {
                    assertSame(BoardSpace.SpaceType.BLACK, board[x][y].getType());
                    continue;
                }
                if (x == 6 && y == 3) {
                    assertSame(BoardSpace.SpaceType.BLACK, board[x][y].getType());
                    continue;
                }
                assertSame(BoardSpace.SpaceType.EMPTY, board[x][y].getType());
            }
        }
    }
    @Test
    public void testOGPlayerOwnedSpaces() {
        Player p1 = new HumanPlayer();
        Player p2 = new HumanPlayer();
        p1.setColor(BoardSpace.SpaceType.BLACK);
        p2.setColor(BoardSpace.SpaceType.WHITE);

        OthelloGame og = new OthelloGame(p1, p2);
        BoardSpace[][] board = og.getBoard();

        Map<BoardSpace, List<BoardSpace>> moves = og.getAvailableMoves(og.getPlayerOne());

        List<BoardSpace> expectedP1Spaces = new ArrayList<>();
        expectedP1Spaces.add(board[3][4]);
        expectedP1Spaces.add(board[4][3]);

        for (BoardSpace b1:p1.getPlayerOwnedSpacesSpaces()) {
            assertTrue(expectedP1Spaces.contains(b1));
            expectedP1Spaces.remove(b1);
        }
        assertTrue(expectedP1Spaces.isEmpty());

        List<BoardSpace> expectedP2Spaces = new ArrayList<>();
        expectedP2Spaces.add(board[3][3]);
        expectedP2Spaces.add(board[4][4]);

        for (BoardSpace b2:p2.getPlayerOwnedSpacesSpaces()) {
            assertTrue(expectedP2Spaces.contains(b2));
            expectedP2Spaces.remove(b2);
        }
        assertTrue(expectedP2Spaces.isEmpty());
    }
}
