package org.combinators.solitaire.spanish_patience;

import ks.common.view.*;
import ks.common.controller.*;
import ks.common.model.*;
import ks.common.games.*;
import ks.client.gamefactory.GameWindow;
import ks.launcher.Main;
import java.awt.event.*;
import java.awt.Dimension;
import org.combinators.solitaire.spanish_patience.controller.*;
import org.combinators.solitaire.spanish_patience.model.*;

/**
 * The Game plugin is the constant upon which all other plugins are refined.
 * __p__
 * It is a fully working plugin that has absolutely no behavior or meaning. It won't have the
 * Score or NumberOfCardsLeft, but it will at least be able to show a blank playing field.
 * __p__
 * __author: George T. Heineman (heineman__cs.wpi.edu)
 */
public class Spanish_Patience extends Solitaire {

    /**
     * Enable refinements to determine whether game has been won.
     */
    public boolean hasWon() {
        boolean hasWon = true;
        {
            int _ct = 0;
            for (Stack st : foundation) {
                _ct += st.count();
            }
            if (_ct != 52) {
                return false;
            }
        }
        if (hasWon) {
            return true;
        }
        return false;
    }

    /**
     * Refinement determines initializations.
     */
    public void initialize() {
        // Fields
        CardImages ci = getCardImages();
        int cw = ci.getWidth();
        int ch = ci.getHeight();
        for (int j = 0; j < 13; j++) {
            tableau[j] = new Column(tableauPrefix + (j + 1));
            addModelElement(tableau[j]);
            tableauView[j] = new ColumnView(tableau[j]);
        }
        for (int j = 0; j < 4; j++) {
            foundation[j] = new Pile(foundationPrefix + (j + 1));
            addModelElement(foundation[j]);
            foundationView[j] = new PileView(foundation[j]);
        }
        // Single deck instantiated as is
        // Basic start of pretty much any solitaire game that requires a deck.
        deck = new Deck("deck");
        int seed = getSeed();
        deck.create(seed);
        addModelElement(deck);
        tableauView[0].setBounds(15, 200, 73, 1261);
        addViewWidget(tableauView[0]);
        tableauView[1].setBounds(103, 200, 73, 1261);
        addViewWidget(tableauView[1]);
        tableauView[2].setBounds(191, 200, 73, 1261);
        addViewWidget(tableauView[2]);
        tableauView[3].setBounds(279, 200, 73, 1261);
        addViewWidget(tableauView[3]);
        tableauView[4].setBounds(367, 200, 73, 1261);
        addViewWidget(tableauView[4]);
        tableauView[5].setBounds(455, 200, 73, 1261);
        addViewWidget(tableauView[5]);
        tableauView[6].setBounds(543, 200, 73, 1261);
        addViewWidget(tableauView[6]);
        tableauView[7].setBounds(631, 200, 73, 1261);
        addViewWidget(tableauView[7]);
        tableauView[8].setBounds(719, 200, 73, 1261);
        addViewWidget(tableauView[8]);
        tableauView[9].setBounds(807, 200, 73, 1261);
        addViewWidget(tableauView[9]);
        tableauView[10].setBounds(895, 200, 73, 1261);
        addViewWidget(tableauView[10]);
        tableauView[11].setBounds(983, 200, 73, 1261);
        addViewWidget(tableauView[11]);
        tableauView[12].setBounds(1071, 200, 73, 1261);
        addViewWidget(tableauView[12]);
        foundationView[0].setBounds(293, 20, 73, 97);
        addViewWidget(foundationView[0]);
        foundationView[1].setBounds(381, 20, 73, 97);
        addViewWidget(foundationView[1]);
        foundationView[2].setBounds(469, 20, 73, 97);
        addViewWidget(foundationView[2]);
        foundationView[3].setBounds(557, 20, 73, 97);
        addViewWidget(foundationView[3]);
        for (int j = 0; j < 13; j++) {
            tableauView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            tableauView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            tableauView[j].setMouseAdapter(new ColumnController(this, tableauView[j]));
        }
        for (int j = 0; j < 4; j++) {
            foundationView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            foundationView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            foundationView[j].setMouseAdapter(new PileController(this, foundationView[j]));
        }
        for (int i = 0; i < 4; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[0].add(c);
        }
        for (int i = 0; i < 4; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[1].add(c);
        }
        for (int i = 0; i < 4; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[2].add(c);
        }
        for (int i = 0; i < 4; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[3].add(c);
        }
        for (int i = 0; i < 4; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[4].add(c);
        }
        for (int i = 0; i < 4; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[5].add(c);
        }
        for (int i = 0; i < 4; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[6].add(c);
        }
        for (int i = 0; i < 4; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[7].add(c);
        }
        for (int i = 0; i < 4; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[8].add(c);
        }
        for (int i = 0; i < 4; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[9].add(c);
        }
        for (int i = 0; i < 4; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[10].add(c);
        }
        for (int i = 0; i < 4; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[11].add(c);
        }
        for (int i = 0; i < 4; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[12].add(c);
        }
        // Cover the Container for any events not handled by a widget:
        getContainer().setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
        getContainer().setMouseAdapter(new SolitaireReleasedAdapter(this));
        getContainer().setUndoAdapter(new SolitaireUndoAdapter(this));
    }

    /**
     * Refinement determines name.
     */
    public String getName() {
        // special case to be handled in parser specially. Parser quotes.
        return "Spanish_Patience";
    }

    /**
     * Helper routine for setting default widgets. This is defined so that any future layer
     * can use this method to define a reasonable default set of controllers for the widget.
     */
    protected void setDefaultControllers(Widget w) {
        w.setMouseMotionAdapter(new ks.common.controller.SolitaireMouseMotionAdapter(this));
        w.setMouseAdapter(new ks.common.controller.SolitaireReleasedAdapter(this));
        w.setUndoAdapter(new SolitaireUndoAdapter(this));
    }

    // force to be able to launch directly.
    public static void main(String[] args) {
        final GameWindow gw = Main.generateWindow(new Spanish_Patience(), Deck.OrderBySuit);
        // properly exist program once selected.
        gw.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        gw.setVisible(true);
    }

    public boolean allSameSuit(Stack col) {
        if (col.empty() || col.count() == 1) {
            return true;
        } else {
            Card c1, c2;
            int size = col.count();
            for (int i = 1; i < size; i++) {
                c1 = col.peek(i - 1);
                c2 = col.peek(i);
                if (c1.getSuit() != c2.getSuit()) {
                    return false;
                }
            }
            return true;
        }
    }

    public boolean allSameRank(Stack col) {
        if (col.empty() || col.count() == 1) {
            return true;
        } else {
            Card c1, c2;
            int size = col.count();
            for (int i = 1; i < size; i++) {
                c1 = col.peek(i - 1);
                c2 = col.peek(i);
                if (c1.getRank() != c2.getRank()) {
                    return false;
                }
            }
            return true;
        }
    }

    public boolean emptyPiles(Stack[] group) {
        for (int i = 0; i < group.length; i++) {
            if (group[i].empty()) {
                return true;
            }
        }
        return false;
    }

    IntegerView scoreView;

    IntegerView numLeftView;

    public Column[] tableau = new Column[13];

    public static final String tableauPrefix = "tableau";

    public ColumnView[] tableauView = new ColumnView[13];

    public Pile[] foundation = new Pile[4];

    public static final String foundationPrefix = "foundation";

    public PileView[] foundationView = new PileView[4];

    public Deck deck;

    DeckView deckView;

    @Override
    public Dimension getPreferredSize() {
        // default starting dimensions...
        return new Dimension(1144, 1461);
    }
}
