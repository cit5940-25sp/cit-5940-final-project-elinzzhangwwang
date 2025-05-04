package othello.gamelogic;

import MLRegression.OthelloDecisionTree;

public class Regression implements Strategy {

    @Override
    public BoardSpace makeMove(BoardSpace[][] board, Player p1) {
        OthelloDecisionTree tree = new OthelloDecisionTree();
        return null;
    }

    public static void main (String[] args) {
        OthelloDecisionTree tree = new OthelloDecisionTree();
    }
}
