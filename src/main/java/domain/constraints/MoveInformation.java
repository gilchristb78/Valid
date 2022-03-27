package domain.constraints;

/** Base interface for all possible constraints used within the system. */
public interface MoveInformation {

    /**
     * Elements described in a move might be a single card or a collection of cards.
     *
     * Return true if given element is a single card; false if represents potentially multiple cards.
     */
    boolean isSingleCard();
}
