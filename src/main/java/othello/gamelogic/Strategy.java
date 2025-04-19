package othello.gamelogic;

public interface Strategy {
    BoardSpace makeMove(BoardSpace[][] board, Player player);
}
