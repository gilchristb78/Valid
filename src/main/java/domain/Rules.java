package domain;

import java.util.*;

/**
 * Rules are divided into two sets
 *
 * Drag rules are those that respond to PRESS -> RELEASE on another widget
 * Press rules are those that respond to PRESS events on a single widget
 * Click rules are those that respond to CLICK events on a single widget
 */
public class Rules {

    enum GuestureType {
        DRAG,
        PRESS,
        CLICK
    }

    /** Eligible moves. */
    List<Move> dragMoves  = new ArrayList<>();
    List<Move> pressMoves = new ArrayList<>();
    List<Move> clickMoves = new ArrayList<>();

    /** Add designated move to specific action type. */
    public void addDragMove(Move m)  { dragMoves.add(m); }
    public void addPressMove(Move m) { pressMoves.add(m); }
    public void addClickMove(Move m) { clickMoves.add(m); }

    /** Retrieve specific moves based on the guesture. */
    public Iterator<Move> presses() { return pressMoves.iterator(); }
    public Iterator<Move> drags() { return dragMoves.iterator(); }
    public Iterator<Move> clicks() { return clickMoves.iterator(); }

    List<Move> byType(GuestureType t) {
        switch (t) {
            case DRAG: return dragMoves;
            case PRESS: return pressMoves;
            case CLICK: return clickMoves;
        }

        // never gets here
        return null;
    }

    /**
     * Modifies rules to add the given move (associated with GuestureType).
     *
     * @param changeMove    new move to be added to the rules
     * @param type          guesture type (i.e., drag, click, press)
     *
     * @return true if a previous move was removed; false otherwise.
     */
    boolean modify(Move changeMove, GuestureType type) {
        List<Move> base = byType(type);
        for (Move m : base) {
            if (m.equals(changeMove)) {
                base.remove(m);
                base.add(changeMove);
                return true;
            }
        }

        base.add(changeMove);
        return false;
    }
}
