package domain.deal;

import domain.Container;
import domain.ContainerType;

public class ElementTargetRange extends Target {
    public final ContainerType targetType;
    //public final Container target;
    public final int min;
    public final int max;


    /** Covers the range [min, max). */
    public ElementTargetRange(ContainerType type, int min, int max) {
        this.targetType = type;
        //this.target = target;
        this.min = min;
        this.max = max;
    }
}
