package domain;

import java.awt.*;

/**
 * A Widget is a Rectangle with a given integer identifier.
 *
 * The integer identifier is zero-based, and is intended to be used for situations
 * when you need the location of the nth widget for a container, for example.
 *
 * Note: This concept is only used for layout and not for any logical reason.
 */
public class Widget extends Rectangle {

    /** Index identifier into container for which Widget exists. */
    public final int idx;

    public Widget (int idx, int x, int y, int w, int h) {
        super (x,y,w,h);
        this.idx = idx;
    }
}
