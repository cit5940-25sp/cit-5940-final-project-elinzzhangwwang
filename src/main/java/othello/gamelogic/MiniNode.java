package othello.gamelogic;

import java.util.*;

/**
 * Node class for the minimax tree.
 * The boardstate is the value, and its children are possible future boardstates we can achieve with the next available move
 */
public class MiniNode {
    BoardSpace[][] boardValue; // The board state is a value
    List<MiniNode> children;   // Children nodes representing future board states

    /**
     * Constructor to create a node with a given board state
     * @param boardValue The board state represented by this node
     */
    public MiniNode(BoardSpace[][] boardValue) {
        this.boardValue = boardValue;
        this.children = new ArrayList<>(); // Initialize the children list
    }

    /**
     * Add a child node to this node
     * @param node The child node to add
     */
    public void addNode(MiniNode node) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(node);
    }

    /**
     * Get the number of children for this node
     * @return The number of children
     */
    public int getChildCount() {
        return children != null ? children.size() : 0;
    }

    /**
     * Check if this node is a leaf node (has no children)
     * @return True if this is a leaf node, false otherwise
     */
    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }

    /**
     * Get the child at the specified index
     * @param index Index of the child to get
     * @return The child node at the specified index
     */
    public MiniNode getChild(int index) {
        if (children != null && index >= 0 && index < children.size()) {
            return children.get(index);
        }
        return null;
    }
}