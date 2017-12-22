package domain.win;

import domain.ContainerType;

public class BoardStatePair {
    public final ContainerType tpe;
    public final int total;

    public BoardStatePair (ContainerType tpe, int total) {
        this.tpe = tpe;
        this.total = total;
    }
}
