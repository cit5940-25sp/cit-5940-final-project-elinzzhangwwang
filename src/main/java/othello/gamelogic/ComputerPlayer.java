package othello.gamelogic;

/**
 * Represents a computer player that will make decisions autonomously during their turns.
 * Employs a specific computer strategy passed in through program arguments.
 */
public class ComputerPlayer extends Player{
    private Strategy strategy;

    public ComputerPlayer(String strategyName) {
        // PART 2
        // TODO: Use the strategyName input to create a specific strategy class for this computer
        // This input should match the ones specified in App.java!
        if (strategyName.equalsIgnoreCase("minimax")) {
            this.strategy = new Minimax();
        } else if (strategyName.equalsIgnoreCase("mcts")) {
            this.strategy = new MCTS();
        } else if (strategyName.equalsIgnoreCase("custom")) {
            this.strategy = new Regression();
        }
    }

    // PART 2
    // TODO: implement a method that returns a BoardSpace that a strategy selects
    /**
     * Make a move using the strategy
     * @param board The current board state
     * @return The BoardSpace representing the chosen move
     */
    public BoardSpace makeMove(BoardSpace[][] board) {
        return strategy.makeMove(board, this);
    }
}