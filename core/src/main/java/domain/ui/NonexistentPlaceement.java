package domain.ui;

import domain.Widget;

/**
 * Artificial class to represent container that has no visible presence.
 */
public class NonexistentPlaceement extends PlacementGenerator {

    @Override
    public boolean hasNext() {
        return false;
    }

    public Widget next() {
        return null;   // should never get here...
    }
}
