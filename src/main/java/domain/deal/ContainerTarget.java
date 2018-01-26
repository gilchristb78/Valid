package domain.deal;

import domain.Container;
import domain.ContainerType;

public class ContainerTarget extends Target {
    public final ContainerType targetType;
    //public final Container target;

    public ContainerTarget(ContainerType type) { // }, Container target) {
        this.targetType = type;
       // this.target = target;

    }
}
