package MLRegression;


import java.io.*;
import java.util.*;

import othello.gamelogic.BoardSpace;
import smile.regression.RegressionTree;


public class OthelloDecisionTree {
    RegressionTree tree;
    public static void main(String args[]) {
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
                if (scoreRead != 1 || movesRead != 60) {
                    System.err.println("Failed to read");
                    return;
                }
                //current game
                System.out.println("Processing Game : " + count);
                ++count;
                //System.out.println("F5: " + curMoves[0]);
                double[][] curFeatureVectors = vectorMoves(curMoves);
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
        RegressionTree tree = new RegressionTree(trimmedFeatures, trimmedTarget, 10);
        System.out.println("Saving Tree");


        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("RegressionTreeModel/tree.model"))) {
            out.writeObject(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public OthelloDecisionTree (int variant) {
        try (FileInputStream fileIn = new FileInputStream("RegressionTreeModel/tree.model");
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            this.tree = (RegressionTree) objectIn.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public Map<BoardSpace, Double> getPredictions (BoardSpace[][] board, Set<BoardSpace> moves, boolean isPlayerOne) {
        Map<BoardSpace, Double> predictions = new HashMap<>();
        //converts a 2-d array of boardspaces to a byte array
        byte[][] boardState = OthelloGameInterperter.boardStateConverter(board);

        //iterate over every move
        for (BoardSpace move : moves) {
            double[] featureVector = new double[66];
            for (int x = 0; x < 8; ++x) {
                for (int y = 0; y < 8; ++y) {
                    featureVector[vectorIndices[x][y]] = boardState[x][y];
                }
            }
            featureVector[64] = isPlayerOne?1.0:0.0;

            int moveX = move.getX();
            int moveY = move.getY();

            int moveYX = vectorIndices[moveY][moveX];
            //System.out.println(moveYX);
            featureVector[65] = moveYX/64.0;

            predictions.put(move, tree.predict(featureVector));
        }
        return predictions;
    }


    private static double[][] vectorMoves(byte[] moves) {
        double[][] ret;
        OthelloGameInterperter ogi = new OthelloGameInterperter(moves);

        int forfeitTurn = ogi.getForfeit();
        //gets an array of 2-D arrays of the board state of the game.
        byte[][][] boardStates = ogi.getBoardStates();
        //array of which player made each move
        boolean[] player = ogi.getPlayers();

        ret = new double[forfeitTurn][66];
        //iterate over all 60 turns of the game
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

            int curX = (int)moves[turn] % 10 -1;
            int curY = (int)moves[turn] / 10 -1;
            if (turn == 0) {
                //System.out.println((double) moves[turn]);
                //System.out.println(vectorIndices[curX][curY]);
            }
            curFeature[65] = (double)(vectorIndices[curX][curY]) / 64.0;

        }
        return ret;
    }

    // convert bytes to hex string swapped to xy format instead of yx
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

