package othello.gamelogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MCTSNode {
    private final BoardSpace[][] boardState;
    private final BoardSpace.SpaceType nextPlayer;
    private final MCTSNode parent;
    private final List<MCTSNode> children;
    private final BoardSpace move;
    private int visits;
    private double wins;

    //MCTSNode Constructor
    public MCTSNode(BoardSpace[][] boardState, BoardSpace.SpaceType currentPlayer, MCTSNode parent, BoardSpace move) {
        this.boardState = deepCopyBoard(boardState);
        this.nextPlayer = currentPlayer;
        this.parent = parent;
        this.move = move;
        this.children = new ArrayList<>();
        this.visits = 0;
        this.wins = 0;
    }

    public BoardSpace[][] getBoardState() {
        return boardState;
    }

    public BoardSpace.SpaceType getNextPlayer() {
        return nextPlayer;
    }

    public MCTSNode getParent() {
        return parent;
    }

    public List<MCTSNode> getChildren() {
        return children;
    }

    public BoardSpace getMove() {
        return move;
    }

    public int getVisits() {
        return visits;
    }

    public double getWins() {
        return wins;
    }

    public void addVisits() {
        visits++;
    }

    public void addWins(double result) {
        wins += result;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean isFullyExpanded() {
        Player tempPlayer = new HumanPlayer();
        tempPlayer.setColor(nextPlayer);
        Map<BoardSpace, List<BoardSpace>> availableMoves = tempPlayer.getAvailableMoves(boardState);
        return availableMoves.size() == children.size();
    }

    public void addChild(MCTSNode child) {
        children.add(child);
    }

    //Create a copy of original board
    private BoardSpace[][] deepCopyBoard(BoardSpace[][] original) {
        BoardSpace[][] copy = new BoardSpace[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[i].length; j++) {
                copy[i][j] = new BoardSpace(original[i][j]);
            }
        }
        return copy;
    }
}
