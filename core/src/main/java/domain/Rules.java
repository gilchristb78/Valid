package domain;

import java.util.*;

public class Rules implements Iterable<Move> {
    
    /** Winning logic. */
    Logic    logic;
    public void   setLogic(Logic log) { this.logic = log; }
    public Logic  getLogic() { return logic; }

    /** Eligible moves. */
    ArrayList<Move> moves = new ArrayList<Move>();
    public void addMove(Move m) { moves.add(m); }
    public Iterator<Move> iterator() { return moves.iterator(); }

}
