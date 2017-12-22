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

    /** Eligible moves. */
    List<Move> dragMoves  = new ArrayList<>();
    List<Move> pressMoves = new ArrayList<>();
    List<Move> clickMoves = new ArrayList<>();

    /** Add designated move to specific action type. */
    public void addDragMove(Move m)  { dragMoves.add(m); }
    public void addPressMove(Move m) { pressMoves.add(m); }
    public void addClickMove(Move m) { clickMoves.add(m); }

    public Iterator<Move> presses() { return pressMoves.iterator(); }
    public Iterator<Move> drags() { return dragMoves.iterator(); }
    public Iterator<Move> clicks() { return clickMoves.iterator(); }

}
