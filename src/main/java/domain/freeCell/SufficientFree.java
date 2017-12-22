package domain.freeCell;

import domain.*;
import domain.constraints.MoveInformation;

/**
 *  To be used in FreeCell. Must take SPECIAL CARE to handle the cases where
 *
 *  empty column is destination
 *  empty column is the source (you just emptied it)
 */
public class SufficientFree extends Constraint {

    public final MoveInformation reserve;
    public final MoveInformation tableau;
    public final MoveInformation src;
    public final MoveInformation destination;
    public final MoveInformation column;

    public SufficientFree (MoveInformation column, MoveInformation src, MoveInformation destination, MoveInformation reserve, MoveInformation tableau){
        this.reserve = reserve;
        this.tableau = tableau;
        this.src = src;
        this.destination = destination;
        this.column = column;
    }
}
