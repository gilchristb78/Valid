package domain.ui;

import domain.Widget;
import java.util.Iterator;

/**
 * Need an object that provides three kinds of placement iterators:
 *
 *   a. hasNext() always returns false. This is for containers that are not visible
 *   b. start with a topleft and extend to the right with fixed gap
 *   c. place arbitrary widgets at arbitrary locations, based on (x,y) tuple
 *
 * To determine how many items to iterate over, the 'max' field keeps track based
 * on invocations to reset.
 */
public abstract class PlacementGenerator implements Iterator<Widget> {

    /** Maximum number of iterations allowed in the given iterator. */
    protected int max;
    int idx = 0;

    public void reset(int m) {
        this.max = m;
        this.idx = 0;
    }

    @Override
    public boolean hasNext() {
        return idx < max;
    }
}
