package othello.gamelogic;

import java.util.List;
import java.util.Map;

public class ComputerPlayerArena {
    public static void main (String[] args) {
        if (args.length != 2) {
            System.err.println("Usage Error: must have 2 arguments for strategy used");
        }
        long startTime = System.nanoTime();
        int totalScore = 0;
        for (int game = 0; game < 100; ++game) {
            System.out.print("Processing Game: " + game);
            ComputerPlayer player1 = new ComputerPlayer(args[0]);
            ComputerPlayer player2 = new ComputerPlayer(args[1]);
            player1.setColor(BoardSpace.SpaceType.BLACK);
            player2.setColor(BoardSpace.SpaceType.WHITE);
            ComputerPlayer currentPlayer = player1;
            ComputerPlayer otherPlayer = player2;

            OthelloGame og = new OthelloGame(player1, player2);
            int skippedTurns = 0;
            //iterate over turns of game
            for (int turn = 0; turn < 60; ++turn) {
                Map<BoardSpace, List<BoardSpace>> availableMoves = og.getAvailableMoves(currentPlayer);
                //player has no available moves
                if (availableMoves.isEmpty()) {
                    //check that game is not over
                    if (og.getPlayerOne().getPlayerOwnedSpacesSpaces().size() + og.getPlayerTwo().getPlayerOwnedSpacesSpaces().size() != 64 && skippedTurns != 2) {
                        //swap players and other player takes turn
                        ComputerPlayer temp = currentPlayer;
                        currentPlayer = otherPlayer;
                        otherPlayer = temp;
                        --turn;
                        skippedTurns++;
                        continue;
                    } else if (skippedTurns == 2 || og.getPlayerOne().getPlayerOwnedSpacesSpaces().size() + og.getPlayerTwo().getPlayerOwnedSpacesSpaces().size() == 64) {
                        //gameover
                        break;
                    }
                }
                BoardSpace chosenMove = og.computerDecision(currentPlayer);
                og.takeSpaces(currentPlayer, otherPlayer, availableMoves, chosenMove);
                ComputerPlayer temp = currentPlayer;
                currentPlayer = otherPlayer;
                otherPlayer = temp;
                skippedTurns = 0;
            }
            totalScore += og.getPlayerOne().getPlayerOwnedSpacesSpaces().size();
            System.out.println(" Final Score: " + og.getPlayerOne().getPlayerOwnedSpacesSpaces().size());
        }
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println(String.format("Strategy %s against Strategy %s scored average of %.2f points", args[0], args[1], (double)totalScore/100.0));
        System.out.println("Execution time: " + (duration/1000000.0) + "ms");
    }

    private static int getBlack(BoardSpace[][] board) {
        int ret = 0;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (board[i][j].getType() == BoardSpace.SpaceType.BLACK) {
                    ++ret;
                }
            }
        }
        return ret;
    }

}
