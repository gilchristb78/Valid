package domain.spider;

import domain.*;
import domain.Constraint;
import domain.constraints.*;
import domain.deal.*;


public interface VariationPoints {

    /** Initial deal can differ between variations. */
    Deal getDeal();

    /** Different variations of spider can use different numbers of decks */
    Stock getStock();

    /** Determine the logic of moving columns from the tableau. Changes depending on suit #, for example */
    Constraint moveColumn(MoveInformation column);
}
