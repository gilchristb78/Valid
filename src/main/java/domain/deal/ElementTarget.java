package domain.deal;

import domain.Container;
import domain.ContainerType;

public class ElementTarget extends Target {
    public final ContainerType targetType;
    //public final Container target;
    public final int idx;

    public ElementTarget(ContainerType type, int idx) {
        this.targetType = type;
        //this.target = target;
        this.idx = idx;
    }
}
