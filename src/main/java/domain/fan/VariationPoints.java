package domain.fan;

import domain.Constraint;
import domain.constraints.MoveInformation;
import domain.ui.Layout;

public interface VariationPoints {
    Layout getLayout();
    boolean hasReserve();
    void init();
    Constraint buildOnFoundation(MoveInformation bottom);
    Constraint buildOnTableau(MoveInformation bottom);
}
