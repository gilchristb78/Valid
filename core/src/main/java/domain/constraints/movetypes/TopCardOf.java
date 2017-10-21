package domain.constraints.movetypes;

import domain.constraints.MoveInformation;

public class TopCardOf implements MoveInformation {

    /** Referent for the topcard query. */
    public final MoveInformation base;

    public TopCardOf(MoveInformation m) {
        this.base = m;
    }
}
