package othello.gamelogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomStrat implements Strategy{
    @Override
    public BoardSpace makeMove(BoardSpace[][] board, Player p1) {

        Map<BoardSpace, List<BoardSpace>> availableMoves = p1.getAvailableMoves(board);
        List<BoardSpace> list = new ArrayList<>(availableMoves.keySet());

        Random random = new Random();
        int randomIndex = random.nextInt(list.size());

        return list.get(randomIndex);
    }
}
