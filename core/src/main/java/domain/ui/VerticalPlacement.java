package domain.ui;
//
//// aces
//        stmts = stmts ++ Java(s"""
//                |fieldPileViews[$idx].setBounds(240, $y, 73, 97);
//                |addViewWidget(fieldPileViews[$idx]);
//                """.stripMargin).statements()
//
//                // rowviews on the left
//                stmts = stmts ++ Java(s"""
//                |fieldRowViews[$idx].setBounds(10, $y, 180, 97);
//                |addViewWidget(fieldRowViews[$idx]);
//                """.stripMargin).statements()
//
//
//                // rowviews on the right
//                val offset = idx + 4
//                stmts = stmts ++ Java(s"""
//                |fieldRowViews[$offset].setBounds(380, $y, 180, 97);
//                |addViewWidget(fieldRowViews[$offset]);
//                """.stripMargin).statements()

import domain.Solitaire;
import domain.Widget;

import java.awt.*;

public class VerticalPlacement extends PlacementGenerator {

    int x;
    int y;
    final int width;
    final int height;
    final int anchorx;
    final int anchory;
    final int gap;

    public VerticalPlacement(Point topLeft, int width, int height, int gap) {
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

        y += Solitaire.card_height + gap;
        idx++;
        return r;
    }
}
