package domain.ui;

import domain.Container;
import domain.Solitaire;
import domain.Widget;

import java.awt.*;

/**
 * Standard horizontally-expanding container starting at fixed topleft and known width and height.
 */
public class HorizontalPlacement extends PlacementGenerator {

    int x;
    int y;
    final int width;
    final int height;

    final int gap;

    public HorizontalPlacement(Point topLeft, int width, int height, int gap) {
        this.x = topLeft.x;
        this.y = topLeft.y;
        this.width = width;
        this.height = height;
        this.gap = gap;
    }

    /** Note: Must manually increment idx from superclass. */
    @Override
    public Widget next() {
        Widget r = new Widget (idx, x, y, width, height);

        x += Solitaire.card_width + gap;
        idx++;
        return r;
    }
}
