package domain.deal;

import domain.constraints.MoveInformation;

/**
 * During the deal, each card is considered by itself.
 *
 * This enum exists to take advantage of the constraints capabilities.
 */
public enum DealComponents implements MoveInformation {
    Card("card");                   // Each card

    public final String name;

    DealComponents(String name) {
        this.name = name;
    }

    public String getName() { return name; }

}
