package domain.archway;

import domain.*;
import domain.Container;
import domain.ui.CalculatedPlacement;
import domain.ui.HorizontalPlacement;
import domain.ui.Layout;
import domain.ui.PlacementGenerator;
import pysolfc.archway.Archway;

import java.awt.*;

/**
 * Layout of Archway assumes the existence of a number of container types.
 *
 */
public class ArchwayLayout extends Layout {

    /** Used for calculations in layout. */
    int scale = 27;

    public ArchwayLayout() {
        int xs[] = new int[]{2, 5, 2, 5};
        int ys[] = new int[]{23, 23, 27, 27};
        Point[] anchors = new Point[xs.length];
        for (int i = 0; i < xs.length; i++) {
            anchors[i] = new Point(xs[i] * scale, ys[i] * scale);
        }

        add(SolitaireContainerTypes.Foundation,
                new CalculatedPlacement(anchors, Solitaire.card_width, Solitaire.card_height));

        xs = new int[]{2, 2, 2, 2, 4, 10, 14, 18, 24, 26, 26, 26, 26};
        ys = new int[]{19, 15, 11, 7, 3, 1, 1, 1, 3, 7, 11, 15, 19};
        anchors = new Point[xs.length];
        for (int i = 0; i < xs.length; i++) {
            anchors[i] = new Point(xs[i] * scale, ys[i] * scale);
        }

        add(SolitaireContainerTypes.Reserve,
                new CalculatedPlacement(anchors, Solitaire.card_width, Solitaire.card_height));

        xs = new int[]{23, 26, 23, 26};
        ys = new int[]{23, 23, 27, 27};
        anchors = new Point[xs.length];
        for (int i = 0; i < xs.length; i++) {
            anchors[i] = new Point(xs[i] * scale, ys[i] * scale);
        }

        add(ArchwayContainerTypes.KingsDown,
                new CalculatedPlacement(anchors, Solitaire.card_width, Solitaire.card_height));

        add(SolitaireContainerTypes.Tableau,
                new HorizontalPlacement(new Point(10 * scale, 10 * scale), Solitaire.card_width, 8 * Solitaire.card_height, Solitaire.card_gap));
    }
}
