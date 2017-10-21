package domain.idiot;

import domain.Constraint;
import domain.ConstraintStmt;
import domain.constraints.MoveInformation;

/**
 * Does there exist
 */
public class HigherRankSameSuit extends Constraint {

    public final MoveInformation cards;
    public final MoveInformation card;

    /**
     * Given that source exists within the given container, this determines whether there
     * exists another widget in the container (different from source) whose top (visible) card
     * is higher in rank and the same suit as the card.
     *
     */
    public HigherRankSameSuit(MoveInformation cards, MoveInformation card) {

        this.cards = cards;
        this.card = card;
    }
}