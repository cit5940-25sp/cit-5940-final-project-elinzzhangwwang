package MLRegression;
import java.io.*;
public class OthelloDecisionTree {
    public static void main(String[] args) {

        File file = new File("WHTHORCombined/combined_output.bin"); // Replace with your actual file

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[60];
            int bytesRead;
            int chunkNumber = 0;

            while ((bytesRead = fis.read(buffer)) != -1) {
                System.out.println("Chunk " + (++chunkNumber) + ": " + bytesRead + " bytes");
                // Process buffer here
                // If bytesRead < 60, you may want to trim the buffer:
                byte[] actualData = new byte[bytesRead];
                System.arraycopy(buffer, 0, actualData, 0, bytesRead);
                // For example, print as hex or string:
                System.out.println(bytesToInt(actualData));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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


}

