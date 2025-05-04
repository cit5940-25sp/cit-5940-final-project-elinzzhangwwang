package MLRegression;


import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

import othello.gamelogic.BoardSpace;
import smile.regression.RegressionTree;

import static java.nio.file.Files.write;

public class OthelloDecisionTree {
    RegressionTree tree;
    public OthelloDecisionTree() {
        //file for combined binary of game moves
        File movesFile = new File("WHTHORCombined/combined_output.bin");
        //file for combined binary of game scores
        File scoreFile = new File("WHTHORScore/allScores.bin");

        long numGames = scoreFile.length();
        if (numGames != movesFile.length()/60) {
            System.err.println("Score file and Moves file must contain same number of games");
            return;
        }

        //create features array
        double[][] X = new double[(int)numGames * 60][66];
        double[] Y = new double[(int)numGames*60];
        int curIndex = 0;
        int count = 1;
        try (
                FileInputStream movesFileReader = new FileInputStream(movesFile);
                FileInputStream scoreFileReadr = new FileInputStream(scoreFile)
        ){
            byte[] curScore = new byte[1];
            byte[] curMoves = new byte[60];
            int scoreRead;
            int movesRead;

            while ((scoreRead = scoreFileReadr.read(curScore)) != -1 &&
                    (movesRead = movesFileReader.read(curMoves)) != -1) {
                //current game
                System.out.println("Processing Game : " + count);
                ++count;
                double[][] curFeatureVectors = vectorGameBoard(curMoves);
                double score = (double) curScore[0];
                for (double[] curVector: curFeatureVectors) {
                    X[curIndex] = curVector;
                    Y[curIndex] = score;
                    ++curIndex;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        double[][] trimmedFeatures = Arrays.copyOf(X, curIndex);
        double[] trimmedTarget = Arrays.copyOf(Y, curIndex);
        System.out.println("Building tree");
        this.tree = new RegressionTree(trimmedFeatures, trimmedTarget, 10);
        System.out.println("Saving Tree");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("tree.model"))) {
            out.writeObject(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private RegressionTree getPrediction(BoardSpace[][] board, int player, int xMove, int yMove) {
        double[] feature = new double[66];
        for (int x = 0; x < board.length; ++x) {
            for (int y = 0; y < board.length; ++y) {
                double boardType = 0.0;

                BoardSpace.SpaceType curType = board[x][y].getType();
                if (curType.equals(BoardSpace.SpaceType.BLACK)) {
                    boardType = 1.0;
                } else if (curType.equals(BoardSpace.SpaceType.WHITE)) {
                    boardType = -1.0;
                } else {
                    boardType = 0.0;
                }

                feature[vectorIndices[x][y]] = boardType;
            }
        }
        return null;
    }

    private static double[][] vectorGameBoard(byte[] moves) {
        double[][] ret;
        OthelloGameInterperter ogi = new OthelloGameInterperter(moves);

        int forfeitTurn = ogi.getForfeit();
        byte[][][] boardStates = ogi.getBoardStates();
        boolean[] player = ogi.getPlayers();

        ret = new double[forfeitTurn][66];
        for (int turn = 0; turn < forfeitTurn; ++turn) {
            //this is an individual turn in the game
            double[] curFeature = ret[turn];
            for (int x = 0; x < 8; ++x) {
                for (int y = 0; y < 8; ++y) {
                    int vectorIndex = vectorIndices[x][y];
                    double curSpace = boardStates[turn][x][y];
                    curFeature[vectorIndex] = curSpace;
                }
            }
            //puts current player in array
            if (player[turn]) {
                curFeature[64] = 1.0;
            } else {
                curFeature[64] = 0.0;
            }
            //puts current move in array
            curFeature[65] = (double)(moves[turn] & 0xFF) / 64.0;

        }
        return ret;
    }

    // convert bytes to hex string
    private static String bytesToInt(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            int move = (b & 0xFF);
            int tens = move / 10;
            int ones = move % 10;
            int swappedMove = ones * 10 + tens;
            sb.append(swappedMove).append(" ");
        }
        return sb.toString();
    }

    private static void copyArray(double[][] source, double[][] destination, int destinationStart) {

    }

    public static final int[][] vectorIndices = {
            {0, 1, 2, 3, 4, 5, 6, 7},
            {8, 9, 10, 11, 12, 13, 14, 15},
            {16, 17, 18, 19, 20, 21, 22, 23},
            {24, 25, 26, 27, 28, 29, 30, 31},
            {32, 33, 34, 35, 36, 37, 38, 39},
            {40, 41, 42, 43, 44, 45, 46, 47},
            {48, 49, 50, 51, 52, 53, 54, 55},
            {56, 57, 58, 59, 60, 61, 62, 63}
    };



}

