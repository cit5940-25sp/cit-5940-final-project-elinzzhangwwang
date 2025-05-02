package MLRegression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class WTHORReader {
    public static void main(String[] args) {
        File directory = new File("WTH_2001-2015");
        String out = "WTHORConverted";
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".wtb")) {
                        String fileName = file.getName();
                        int dotIndex = fileName.lastIndexOf('.');
                        String nameWithoutExtension;
                        if (dotIndex != -1) {
                            nameWithoutExtension = fileName.substring(0, dotIndex) + ".txt";
                        } else {
                            nameWithoutExtension = fileName + ".txt";
                        }
                        File newFile = new File(out, nameWithoutExtension);
                        convertFile(file, newFile);
                    }
                }
            }
        } else {
            System.out.println("Directory does not exist or is not a directory.");
        }

    }

    private static void convertFile(File file, File out) {
        byte[] headerBuffer = new byte[16];
        byte[] gameBuffer = new byte[68];

        try (FileInputStream input = new FileInputStream(file)) {
            int bytesRead = input.read(headerBuffer);

            if (bytesRead > 0) {
                procHeader(headerBuffer, out);
            } else {
                System.out.println("File is empty or couldn't read any bytes.");
            }
            while ((bytesRead = input.read(gameBuffer)) != -1) {
                if (bytesRead != 68) {
                    System.err.println("Error unexpected end of file");
                }
                procGame(gameBuffer,out);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void procHeader(byte[] buf, File out) {
        if (buf.length != 16) {
            System.err.println("Header must be 16 bytes");
            return;
        }

        try (FileWriter writer = new FileWriter(out)) {
            writer.write("Year: " + byteToInt(buf[0]) + byteToInt(buf[1]) + "\n");
            writer.write("Month: " + byteToInt(buf[2]) + "\n");
            writer.write("Day: " + byteToInt(buf[3]) + "\n");
            writer.write("Number of N1 records: " + bytesToInt(buf, 4, 7) + "\n");
            writer.write("Number of N2 records: " + bytesToInt(buf, 8, 9) + "\n");
            writer.write("Year of games: " + bytesToInt(buf, 10, 11) + "\n");
            writer.write("Game Board Size: " + byteToInt(buf[12]) + "\n");
            writer.write("Type of Games: " + byteToInt(buf[13]) + "\n");
            writer.write("Depth: " + byteToInt(buf[14]) + "\n");
            writer.write("Reserve: " + byteToInt(buf[15]) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void procGame(byte[] buf, File out) {
        if (buf.length != 68) {
            System.err.println("Game must be 68 bytes");
            return;
        }
        try (FileWriter writer = new FileWriter(out, true)) {
            writer.write("Game ID: " + bytesToInt(buf, 0, 5)+ "\n");
            writer.write("Actual Score: " + byteToInt(buf[6])+ "\n");
            writer.write("Theoretical Score: " + byteToInt(buf[7])+ "\n");
            byte[] moves = Arrays.copyOfRange(buf, 8, 67);
            writer.write("Moves: " + moves+ "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static int byteToInt(byte curByte) {
        return curByte & 0xFF;
    }

    private static long bytesToInt(byte[] buf, int start, int end) {
        if (buf == null || start < 0 || end >= buf.length || start > end) {
            throw new IllegalArgumentException("Invalid indices or byte array");
        }

        long result = 0;
        int shift = 0;

        for (int i = start; i <= end; i++) {
            result |= ((long) buf[i] & 0xFF) << shift;
            shift += 8;
        }

        return result;
    }
}
