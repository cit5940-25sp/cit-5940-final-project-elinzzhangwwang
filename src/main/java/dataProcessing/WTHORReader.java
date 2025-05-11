package dataProcessing;

import java.io.*;
import java.util.Arrays;

public class WTHORReader {
    public static void main(String[] args) {
        //Converts files in directory in to readable form in directory WTHORConverted
        File in = new File("WTH_2001-2015");
        File readableOut = new File("WTHORConverted");
        readableOut.mkdir();
        convertReadable(in, readableOut);

        //convert files in directory to binary files with the score as 1 byte and moves as 60 bytes
        File rawOut = new File("WTHORGameBin");
        rawOut.mkdir();
        convertRawGamesFile(in, rawOut);

        //combines the binary files into one large binary file with all games in score(1) then moves(60) format. Will
        // be divisible by 60 bytes
        File combined = new File("WHTHORCombined");
        combineRawGameFiles(rawOut, combined);


    }

    /**
     * Combines the raw game Files into one large file
     * @param in File directory where all files are stored
     * @param out Output directory where large file will be stored
     */
    private static void combineRawGameFiles(File in, File out)  {


        if (in == null || !in.exists() || !in.isDirectory()) {
            System.err.println("Invalid input directory: " + in);
            return;
        }

        if (out == null) {
            System.err.println("Output directory is null.");
            return;
        }

        if (!out.exists()) {
            if (!out.mkdirs()) {
                System.err.println("Failed to create output directory: " + out);
                return;
            }
        }

        //gets input files as list of Files
        File[] inputFiles = in.listFiles((dir, name) -> name.toLowerCase().endsWith(".bin"));

        //makes sure that the input files has files
        if (inputFiles == null || inputFiles.length == 0) {
            System.out.println("No .bin files found in directory: " + in);
            return;
        }

        //new file that will be output
        File outputFile = new File(out, "combined_output.bin");


        //create output stream to write to output file
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            //iterate over input files
            for (File file : inputFiles) {
                //long to check that files were read correctly
                long errCheck = 0;

                try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                    byte[] buffer = new byte[4096];
                    //stores actual number of bytes read
                    int bytesRead;
                    //outputs bytes read to outputStream
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        errCheck += bytesRead;
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    System.err.println("Failed to read file: " + file.getName() + " (" + e.getMessage() + ")");
                }
                //makes sure that files have games which are divisible by 61
                if (errCheck % 61 != 0) {
                    System.out.println("Error, file has unexpected number of bytes: " + file.getName());
                }
            }
            System.out.println("Successfully wrote to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to write output file: " + e.getMessage());
        }

    }

    /**
     * Converts files to raw bytes in the form of score (1 byte) plus game (60 bytes)
     *
     * @param in File object that is directory of files to be processed
     * @param out File object that is directory targeted as output
     */
    private static void convertRawGamesFile(File in, File out) {
        //check that file exists
        if (in.exists() && in.isDirectory()) {
            File[] files = in.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".wtb")) {
                        String fileName = file.getName();
                        int dotIndex = fileName.lastIndexOf('.');
                        String nameWithoutExtension;
                        if (dotIndex != -1) {
                            nameWithoutExtension = fileName.substring(0, dotIndex) + "_Raw.bin";
                        } else {
                            nameWithoutExtension = fileName + "_Raw.bin";
                        }
                        File newFile = new File(out, nameWithoutExtension);
                        //clears new bin file if it already exists
                        try (FileWriter fw = new FileWriter(newFile, false)) {
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //converts file to raw
                        convertFileRaw(file, newFile);
                    }
                }
            }
        } else {
            System.out.println("Directory does not exist or is not a directory.");
        }
    }
    private static void convertReadable(File in, File out) {
        if (in.exists() && in.isDirectory()) {
            File[] files = in.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".wtb")) {
                        String fileName = file.getName();
                        int dotIndex = fileName.lastIndexOf('.');
                        String nameWithoutExtension;
                        if (dotIndex != -1) {
                            nameWithoutExtension = fileName.substring(0, dotIndex) + "_Readable.txt";
                        } else {
                            nameWithoutExtension = fileName + "_Readable.txt";
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

    /**
     * Converts each game in file to raw bytes.
     * @param in file to be read
     * @param out file to be written to
     */
    private static void convertFileRaw(File in, File out) {
        byte[] headerBuffer = new byte[16];
        byte[] gameBuffer = new byte[68];

        try (FileInputStream input = new FileInputStream(in)) {
            //reads header to skip it
            int bytesRead = input.read(headerBuffer);

            if (bytesRead != 16) {
                System.out.println("File is empty or couldn't read any bytes.");
            }

            //checks
            while ((bytesRead = input.read(gameBuffer)) != -1) {
                if (bytesRead != 68) {
                    System.err.println("Error unexpected end of file");
                }
                //processes the game to out
                procGameRaw(gameBuffer,out);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * converts game given by 68 length byte array to raw bytes to be written to file. Format score (1) moves (60)
     *
     * @param buf buffer input that represents a game
     * @param out file to be written to
     */
    private static void procGameRaw(byte[] buf, File out) {
        if (buf.length != 68) {
            System.err.println("Game must be 68 bytes");
            return;
        }
        try (FileOutputStream os = new FileOutputStream(out, true)) {
            byte[] moves = Arrays.copyOfRange(buf, 8, 68);
            byte[] score = Arrays.copyOfRange(buf, 6, 7);
            os.write(score);
            os.write(moves);
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
