package domain.win;

import domain.SolitaireContainerTypes;

public class BoardStatePair {
    public final SolitaireContainerTypes tpe;
    public final int total;

    public BoardStatePair (SolitaireContainerTypes tpe, int total) {
        this.tpe = tpe;
        this.total = total;
    }
}
