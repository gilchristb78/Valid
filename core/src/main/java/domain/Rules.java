package domain;

import java.util.*;

public class Rules {
    
    /** Winning logic. */
    Logic    logic;
    public void   setLogic(Logic log) { this.logic = log; }
    public Logic  getLogic() { return logic; }

    /** Eligible moves. */
    ArrayList<Move> moves = new ArrayList<Move>();
    public void addMove(Move m) { moves.add(m); }
    public Iterator<Move> getMoves() { return moves.iterator(); }

}
