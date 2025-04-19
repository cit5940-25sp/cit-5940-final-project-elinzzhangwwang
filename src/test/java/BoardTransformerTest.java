import org.junit.Test;
import othello.gamelogic.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class BoardTransformerTest {

    @Test
    public void testEncodeDecode() throws IOException {
        Player p1 = new HumanPlayer();
        Player p2 = new HumanPlayer();
        p1.setColor(BoardSpace.SpaceType.BLACK);
        p2.setColor(BoardSpace.SpaceType.WHITE);

        OthelloGame og = new OthelloGame(p1, p2);
        BoardSpace[][] board = og.getBoard();

        BoardTransformer bt = new BoardTransformer();

        String encodedSave = bt.encode(board, "target/saves",true);
        bt.decode(og, "target/saves/" + encodedSave);
        String doubleEncodedSave = bt.encode(board, "target/saves", true);
        bt.decode(og, "target/saves/" + doubleEncodedSave);
        File encoded1 = new File("target/saves/" + encodedSave);
        File encoded2 = new File("target/saves/" + doubleEncodedSave);
        assertTrue(filesEqual(encoded1, encoded2));
    }

    private boolean filesEqual(File f1, File f2) throws IOException {
        if (f1.length() != f2.length()) return false; // quick size check

        try (FileReader fr1 = new FileReader(f1);
             FileReader fr2 = new FileReader(f2)) {

            int ch1, ch2;
            while ((ch1 = fr1.read()) != -1) {
                ch2 = fr2.read();
                if (ch1 != ch2) {
                    return false;
                }
            }

            return fr2.read() == -1; // confirm both reached EOF
        }
    }
}
