package domain.ui;

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
    final int anchorx;
    final int anchory;
    final int gap;

    public HorizontalPlacement(Point topLeft, int width, int height, int gap) {
        this.x = this.anchorx = topLeft.x;
        this.y = this.anchory = topLeft.y;
        this.width = width;
        this.height = height;
        this.gap = gap;
    }

    public void reset(int m) {
        super.reset(m);
        this.x = anchorx;
        this.y = anchory;
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
