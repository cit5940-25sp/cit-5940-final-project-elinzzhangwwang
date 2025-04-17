import othello.gamelogic.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class PlayerTest {
    @Test
    public void testStartingState() {
        BoardSpace[][] board = new BoardSpace[8][8];
        for (int x = 0; x < board.length; ++x) {
            for (int y = 0; y < board[x].length; ++y) {
                board[x][y] = new BoardSpace(x, y, BoardSpace.SpaceType.EMPTY);
                if (x == 3 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }
                if (x == 4 && y == 4) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }
                if (x == 3 && y == 4) {
                    board[x][y].setType(BoardSpace.SpaceType.BLACK);
                    continue;
                }
                if (x == 4 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.BLACK);
                }
            }
        }
        Player p = new HumanPlayer();
        p.setColor(BoardSpace.SpaceType.BLACK);
        Map<BoardSpace, List<BoardSpace>> moves = p.getAvailableMoves(board);
        List<BoardSpace> expectedmoves = new ArrayList<>();
        expectedmoves.add(board[5][4]);
        expectedmoves.add(board[4][5]);
        expectedmoves.add(board[3][2]);
        expectedmoves.add(board[2][3]);
        for (BoardSpace b:moves.keySet()) {
            assertTrue(expectedmoves.contains(b));
            expectedmoves.remove(b);
        }
        assertTrue(expectedmoves.isEmpty());
    }

    @Test
    public void testSimple() {
        BoardSpace[][] board = new BoardSpace[8][8];
        for (int x = 0; x < board.length; ++x) {
            for (int y = 0; y < board[x].length; ++y) {
                board[x][y] = new BoardSpace(x, y, BoardSpace.SpaceType.EMPTY);
                if (x == 3 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }
                if (x == 4 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }
                if (x == 5 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }
                if (x == 2 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.BLACK);
                }
            }
        }
        Player p = new HumanPlayer();
        p.setColor(BoardSpace.SpaceType.BLACK);
        Map<BoardSpace, List<BoardSpace>> moves = p.getAvailableMoves(board);
        List<BoardSpace> expectedmoves = new ArrayList<>();
        expectedmoves.add(board[6][3]);
        for (BoardSpace b:moves.keySet()) {
            assertTrue(expectedmoves.contains(b));
            expectedmoves.remove(b);
        }
        assertTrue(expectedmoves.isEmpty());
    }

    @Test
    public void testNoFlank() {
        BoardSpace[][] board = new BoardSpace[8][8];
        for (int x = 0; x < board.length; ++x) {
            for (int y = 0; y < board[x].length; ++y) {
                board[x][y] = new BoardSpace(x, y, BoardSpace.SpaceType.EMPTY);
                if (x == 3 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }
                if (x == 4 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }
                if (x == 5 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }

            }
        }
        Player p = new HumanPlayer();
        p.setColor(BoardSpace.SpaceType.BLACK);
        Map<BoardSpace, List<BoardSpace>> moves = p.getAvailableMoves(board);

        assertTrue(moves.isEmpty());
    }

    @Test
    public void testMultiFlank() {
        BoardSpace[][] board = new BoardSpace[8][8];
        for (int x = 0; x < board.length; ++x) {
            for (int y = 0; y < board[x].length; ++y) {
                board[x][y] = new BoardSpace(x, y, BoardSpace.SpaceType.EMPTY);
                if (x == 3 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }

                if (x == 5 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }
                if (x == 2 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.BLACK);
                }
                if (x == 6 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.BLACK);
                }
            }
        }
        Player p = new HumanPlayer();
        p.setColor(BoardSpace.SpaceType.BLACK);
        Map<BoardSpace, List<BoardSpace>> moves = p.getAvailableMoves(board);
        List<BoardSpace> expectedmoves = new ArrayList<>();
        expectedmoves.add(board[4][3]);
        for (BoardSpace b:moves.keySet()) {
            assertTrue(expectedmoves.contains(b));
            expectedmoves.remove(b);
        }
        assertTrue(expectedmoves.isEmpty());
    }

    @Test
    public void testLeftWall() {
        BoardSpace[][] board = new BoardSpace[8][8];
        for (int x = 0; x < board.length; ++x) {
            for (int y = 0; y < board[x].length; ++y) {
                board[x][y] = new BoardSpace(x, y, BoardSpace.SpaceType.EMPTY);
                if (x == 0 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }
                if (x == 1 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }
                if (x == 2 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }
            }
        }
        Player p = new HumanPlayer();
        p.setColor(BoardSpace.SpaceType.BLACK);
        Map<BoardSpace, List<BoardSpace>> moves = p.getAvailableMoves(board);
        assertTrue(moves.isEmpty());
    }

    @Test
    public void testRightWall() {
        BoardSpace[][] board = new BoardSpace[8][8];
        for (int x = 0; x < board.length; ++x) {
            for (int y = 0; y < board[x].length; ++y) {
                board[x][y] = new BoardSpace(x, y, BoardSpace.SpaceType.EMPTY);
                if (x == 5 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }
                if (x == 6 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }
                if (x == 7 && y == 3) {
                    board[x][y].setType(BoardSpace.SpaceType.WHITE);
                    continue;
                }
            }
        }
        Player p = new HumanPlayer();
        p.setColor(BoardSpace.SpaceType.BLACK);
        Map<BoardSpace, List<BoardSpace>> moves = p.getAvailableMoves(board);
        assertTrue(moves.isEmpty());
    }


}
