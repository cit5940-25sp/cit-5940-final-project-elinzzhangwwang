package othello.gamelogic;

import MLRegression.OthelloDecisionTree;

import java.util.*;

public class Regression implements Strategy {

    @Override
    public BoardSpace makeMove(BoardSpace[][] board, Player player) {
        OthelloDecisionTree tree = new OthelloDecisionTree(0);

        Map<BoardSpace, List<BoardSpace>> availableMoves = player.getAvailableMoves(board);
        boolean isPlayerOne = (player.getColor() == BoardSpace.SpaceType.BLACK);
        Map<BoardSpace, Double> predictions = tree.getPredictions(board, availableMoves.keySet(), isPlayerOne);

        for (Map.Entry<BoardSpace, Double> entry : predictions.entrySet()) {
            System.out.println("Boardspace (x,y): " + entry.getKey().getX() + " " + entry.getKey().getY() + " Prediction: " + entry.getValue());
        }

        // Step 1: Get entries and sort them
        List<Map.Entry<BoardSpace, Double>> entryList = new ArrayList<>(predictions.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<BoardSpace, Double>>() {
            @Override
            public int compare(Map.Entry<BoardSpace, Double> e1, Map.Entry<BoardSpace, Double> e2) {
                return Double.compare(e2.getValue(), e1.getValue()); // Descending
            }
        });

        // Step 2: Extract sorted keys
        List<BoardSpace> sortedKeys = new ArrayList<>();
        for (Map.Entry<BoardSpace, Double> entry : entryList) {
            sortedKeys.add(entry.getKey());
        }

        if (isPlayerOne) {
            return sortedKeys.get(0);
        }
        return sortedKeys.get(sortedKeys.size()-1);
    }
}
