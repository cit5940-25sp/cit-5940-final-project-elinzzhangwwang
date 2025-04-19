package othello.gamelogic;

public interface Strategy {
    public BoardSpace makeMove(BoardSpace[][] board , Player player);
}
