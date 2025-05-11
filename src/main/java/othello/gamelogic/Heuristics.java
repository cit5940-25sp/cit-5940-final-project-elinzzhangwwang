package othello.gamelogic;

import dataProcessing.HeuristicGameProcessor;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Heuristics implements Strategy
{
    int[][] heatMap;
    public Heuristics() {
        File in = new File("HeatMapOut/heatmap.bin");
        heatMap = HeuristicGameProcessor.decodeHeatMap(in);
    }
    @Override
    public BoardSpace makeMove(BoardSpace[][] board, Player p1) {
        int moveVal = 0;
        BoardSpace bestMove = null;
        Map<BoardSpace, List<BoardSpace>> availableMoves = p1.getAvailableMoves(board);
        for (BoardSpace move : availableMoves.keySet()) {
            if (heatMap[move.getX()][move.getY()] >= moveVal) {
                bestMove = move;
            }
        }
        if (bestMove == null) {
            System.err.println("Error in makeMove function. No move could be selected");
            return null;
        }
        return bestMove;
    }
}
