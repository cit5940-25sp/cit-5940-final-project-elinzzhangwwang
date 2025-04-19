package othello.gamelogic;

import java.util.BitSet;
import java.io.*;
import java.util.Objects;

public class BoardTransformer {
    public String encode(BoardSpace[][] board, String outDir, boolean playerOne) {
        BitSet bits = new BitSet(128);
        int index = 0;
        for (int x = 0; x < board.length; ++x) {
            for (int y = 0; y < board[x].length; ++y) {
                switch (board[x][y].getType()) {

                    case EMPTY:
                        break;
                    case BLACK:
                        bits.set(index);
                        break;
                    case WHITE:
                        bits.set(index + 1);
                        break;
                    default:
                        System.err.println("Error in encode method");
                        return null;
                }
                index += 2;
            }
        }

        try {
            File saveDir = new File(outDir);
            int saveNum = getSaves(saveDir);
            File newSave = new File(saveDir, "save" + String.format("%02d", saveNum) + ".txt");
            try (FileWriter fw = new FileWriter(newSave)) {
                for (int i = 0; i < 128; i = i + 16) {
                    fw.write(bitSetToChar(bits, i));
                }
                if (playerOne) {
                    fw.write('1');
                } else {
                    fw.write('2');
                }
            }
            return newSave.getName();
        }
        catch (Exception e) {
            System.err.println("Method encode could not find directory outDir");
            return null;
        }
    }

    public int decode(OthelloGame og, String saveFilePath) {
        char[] chars = new char[8];
        char player;
        BitSet bits;
        System.out.println("opening file");
        try {
            File saveFile = new File(saveFilePath);
            try (FileReader fr = new FileReader(saveFile)) {
                int nextChar;
                for (int i = 0; i < 8; ++i) {
                    if ((nextChar = fr.read()) == -1) {
                        System.err.println("Invalid File");
                        return -1;
                    }
                    chars[i] = (char) nextChar;
                }
                if ((nextChar = fr.read()) == -1) {
                    System.err.println("Invalid File");
                    return -1;
                }
                player = (char) nextChar;

            }
        }
        catch (Exception e) {
            System.err.println("Method decode could not find file at saveFilePath");
            return -1;
        }
        if (player != '1' && player != '2') {
            System.err.println("Invalid File");
            return -1;
        }
        System.out.println("succesfully opened file");
        bits = charsToBitset(chars);
        int index = 0;
        BoardSpace[][] board = og.getBoard();
        og.getPlayerOne().getPlayerOwnedSpacesSpaces().clear();
        og.getPlayerTwo().getPlayerOwnedSpacesSpaces().clear();
//        for (int x = 0; x < board.length; ++x) {
//            for (int y = 0; y < board[x].length; ++y) {
//                System.out.print(board[x][y].getType());
//            }
//            System.out.println();
//        }
        for (int x = 0; x < board.length; ++x) {
            for (int y = 0; y < board[x].length; ++y) {
                board[x][y].setType(BoardSpace.SpaceType.EMPTY);
            }
        }

        for (int x = 0; x < board.length; ++x) {
            for (int y = 0; y < board[x].length; ++y) {
                boolean[] boardSpaceBits = {bits.get(index), bits.get(index + 1)};
                if (boardSpaceBits[0] && boardSpaceBits[1]) {
                    System.err.println("File is in wrong format");
                    return -1;
                }
                if (boardSpaceBits[0] && !boardSpaceBits[1]) {
                    og.takeSpace(og.getPlayerOne(), og.getPlayerTwo(), x, y);
                }
                if (!boardSpaceBits[0] && boardSpaceBits[1]) {
                    og.takeSpace(og.getPlayerTwo(), og.getPlayerOne(), x, y);
                }
                index = index + 2;
            }
        }
        return player - '0';

    }
    /**
     * returns the lowest available save file
    * @param outDir the directory to search for save files
    * @return the lowest available save file number. returns -1 on failure
    */
    private int getSaves(final File outDir) {

        boolean[] files = new boolean[100];
        if (!outDir.isDirectory()) {
            System.err.println("File object is not a directory");
            return -1;
        }

        for (File f: Objects.requireNonNull(outDir.listFiles())) {
            if (f.getName().length() == 10 && f.getName().startsWith("save")) {
                String saveNum = f.getName().substring(4, 6);
                if (stringIsInt(saveNum)) {
                    files[Integer.parseInt(saveNum)] = true;
                }
            }
        }
        for (int i = 0; i <= 99; ++i) {
            if (!files[i]) {
                return i;
            }
        }
        return -1;
    }

    private boolean stringIsInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static char bitSetToChar(BitSet bitSet, int start) {
        int value = 0;
        for (int i = 0; i < 16; i++) {
            if (bitSet.get(start + i)) {
                value |= (1 << i);
            }
        }
        return (char) value;
    }
    private static BitSet charsToBitset(char[] chars) {
        BitSet bitSet = new BitSet(128);
        int bitIndex = 0;

        for (char c : chars) {
            for (int i = 0; i < 16; ++i) {
                boolean bit = ((c >> i) & 1) == 1;
                bitSet.set(bitIndex, bit);
                ++bitIndex;
            }
        }

        return bitSet;
    }
}
