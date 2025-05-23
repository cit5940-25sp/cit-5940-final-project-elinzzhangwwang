package dataProcessing;

import java.io.*;

public class HeuristicGameProcessor {
    public static void main(String[] args) {
        //file for combined binary of games
        File gamesFile = new File("WHTHORCombined/combined_output.bin");
        long numGames = gamesFile.length();
        if (numGames % 61 != 0) {
            System.err.println("file must contain a multiple of 61 bytes");
            return;
        }

        //main 2-D array that represents "heat" of each space. "Heat" is the sum of each game's
        // final score multiplied by negative 1 if player 2 played it. 2-D array is in form
        // Array[Column][Row]

        int[][] moveHeatMap = new int[8][8];

        int count = 0;
        try (
                FileInputStream gameFileReader = new FileInputStream(gamesFile)
        ) {
            byte curScore;
            byte[] curMoves = new byte[60];
            byte[] gameBuffer = new byte[61];
            int gameRead;

            while ((gameRead = gameFileReader.read(gameBuffer)) != -1) {
                System.out.println("Processing Game: " + count);
                if (gameRead != 61) {
                    System.err.println("Failed to read");
                    return;
                }
                curScore = gameBuffer[0];
                System.arraycopy(gameBuffer, 1, curMoves, 0, 60);
                //current game
                ++count;

                OthelloGameInterperter ogi = new OthelloGameInterperter(curMoves, curScore);
                int[][] curHeat = ogi.getHeatmap();
                addMatricesInPlace(moveHeatMap, curHeat);
            }

        } catch (IOException e) {
            System.err.println("Error could not open file");
        }
        moveHeatMap = normalizeHeatMap(moveHeatMap);

        File out = new File("HeatMapOut/heatmap.bin");
        encodeHeatMap(moveHeatMap, out);
    }

    private static void addMatricesInPlace(int[][] a, int[][] b) {
        int rows = a.length;
        int cols = a[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                a[i][j] += b[i][j];
            }
        }
    }

    public static int[][] normalizeHeatMap(int[][] array) {

        int[][] scaled = new int[8][8];
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        // First pass: find min and max, skipping center
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isCenter(i, j)) {
                    continue;
                }
                int val = array[i][j];
                if (val < min) {
                    min = val;
                }
                if (val > max) {
                    max = val;
                }
            }
        }

        // Avoid division by zero
        if (max == min) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (!isCenter(i, j)) {
                        scaled[i][j] = 0;
                    }
                }
            }
            return scaled;
        }

        // Second pass: scale to [-100, 100], skipping center
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isCenter(i, j)) {
                    continue;
                }
                double norm = (array[i][j] - min) / (double)(max - min);
                scaled[i][j] = (int)Math.round(norm * 100.0);
            }
        }

        return scaled;
    }

    private static boolean isCenter(int i, int j) {
        return (i == 3 || i == 4) && (j == 3 || j == 4);
    }

    private static void encodeHeatMap(int[][] array, File outFile) {
        try (FileOutputStream fos = new FileOutputStream(outFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            for (int[] row : array) {
                for (int val : row) {
                    if (val < 0 || val > 100) {
                        throw new IllegalArgumentException("Values must be between 0 and 100");
                    }
                    bos.write(val);
                }
            }
        } catch (IOException e) {
            System.err.println("Error could not open file");
        }
    }

    public static int[][] decodeHeatMap(File inFile) {
        int[][] array = new int[8][8];

        try (FileInputStream fis = new FileInputStream(inFile);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            int rowIndex = 0;
            int colIndex = 0;
            int value;

            while ((value = bis.read()) != -1) {
                array[rowIndex][colIndex] = value;

                colIndex++;
                if (colIndex >= 8) {
                    colIndex = 0;
                    rowIndex++;
                }

                if (rowIndex >= 8) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error could not open file");
        }

        return array;
    }


}
