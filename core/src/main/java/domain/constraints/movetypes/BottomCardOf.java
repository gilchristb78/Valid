package domain.constraints.movetypes;

import domain.constraints.MoveInformation;

public class BottomCardOf implements MoveInformation {
    /** Referent for the bottomcard query. */
    public final MoveInformation base;

    public BottomCardOf(MoveInformation m) {
        this.base = m;
    }

    /** This is the bottom card, and is thus single. */
    public boolean isSingleCard() { return true; }
}
