package domain.ui;

import domain.Widget;

import java.awt.*;

/**
 * Given array of Points for placements.
 */
public class CalculatedPlacement extends PlacementGenerator {

    Point anchors[];
    final int width;
    final int height;

    public CalculatedPlacement(Point[] anchors, int width, int height) {
        this.anchors = anchors;
        this.width = width;
        this.height = height;
    }

    /** Note: Must manually increment idx from superclass. */
    @Override
    public Widget next() {
        Widget r = new Widget (idx, anchors[idx].x, anchors[idx].y, width, height);

        idx++;
        return r;
    }
}
