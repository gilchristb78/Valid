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

    /** Determines the logic of moving columns from the tableau. Changes depending on suit #, for example */
    Constraint moveColumn();

    /** Determines the number of buildable piles */
    Tableau getTableau();
}
