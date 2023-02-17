package org.combinators.solitaire.castle;

import ks.common.view.*;
import ks.common.controller.*;
import ks.common.model.*;
import ks.common.games.*;
import ks.client.gamefactory.GameWindow;
import ks.launcher.Main;
import java.awt.event.*;
import java.awt.Dimension;
import org.combinators.solitaire.castle.controller.*;
import org.combinators.solitaire.castle.model.*;

/**
 * The Game plugin is the constant upon which all other plugins are refined.
 * __p__
 * It is a fully working plugin that has absolutely no behavior or meaning. It won't have the
 * Score or NumberOfCardsLeft, but it will at least be able to show a blank playing field.
 * __p__
 * __author: George T. Heineman (heineman__cs.wpi.edu)
 */
public class Castle extends Solitaire implements SolvableSolitaire {

    /**
     * Enable refinements to determine whether game has been won.
     */
    public boolean hasWon() {
        boolean hasWon = true;
        {
            int _ct = 0;
            for (Stack st : tableau) {
                _ct += st.count();
            }
            if (_ct != 0) {
                return false;
            }
        }
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
        for (int j = 0; j < 8; j++) {
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
        tableauView[0].setBounds(100, 200, 73, 194);
        addViewWidget(tableauView[0]);
        tableauView[1].setBounds(203, 200, 73, 194);
        addViewWidget(tableauView[1]);
        tableauView[2].setBounds(306, 200, 73, 194);
        addViewWidget(tableauView[2]);
        tableauView[3].setBounds(409, 200, 73, 194);
        addViewWidget(tableauView[3]);
        tableauView[4].setBounds(615, 200, 73, 194);
        addViewWidget(tableauView[4]);
        tableauView[5].setBounds(718, 200, 73, 194);
        addViewWidget(tableauView[5]);
        tableauView[6].setBounds(821, 200, 73, 194);
        addViewWidget(tableauView[6]);
        tableauView[7].setBounds(924, 200, 73, 194);
        addViewWidget(tableauView[7]);
        foundationView[0].setBounds(512, 100, 73, 97);
        addViewWidget(foundationView[0]);
        foundationView[1].setBounds(512, 200, 73, 97);
        addViewWidget(foundationView[1]);
        foundationView[2].setBounds(512, 300, 73, 97);
        addViewWidget(foundationView[2]);
        foundationView[3].setBounds(512, 400, 73, 97);
        addViewWidget(foundationView[3]);
        for (int j = 0; j < 8; j++) {
            tableauView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            tableauView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            tableauView[j].setMouseAdapter(new ColumnController(this, tableauView[j]));
        }
        for (int j = 0; j < 4; j++) {
            foundationView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            foundationView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            foundationView[j].setMouseAdapter(new PileController(this, foundationView[j]));
        }
        {
            java.util.ArrayList<Card> tmp = new java.util.ArrayList<Card>();
            ks.common.model.Stack keep = new ks.common.model.Stack();
            int _limit = -1;
            while (!deck.empty()) {
                Card card = deck.get();
                if (card.getRank() == Card.ACE) {
                    if (_limit == 0) {
                        keep.add(card);
                    } else {
                        _limit--;
                        tmp.add(card);
                    }
                } else {
                    keep.add(card);
                }
            }
            while (!keep.empty()) {
                deck.add(keep.get());
            }
            for (Card c : tmp) {
                deck.add(c);
            }
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.foundation(this)[0].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.foundation(this)[1].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.foundation(this)[2].add(c);
        }
        for (int i = 0; i < 1; i++) {
            Card c = deck.get();
            ConstraintHelper.foundation(this)[3].add(c);
        }
        for (int i = 0; i < 6; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[0].add(c);
        }
        for (int i = 0; i < 6; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[1].add(c);
        }
        for (int i = 0; i < 6; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[2].add(c);
        }
        for (int i = 0; i < 6; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[3].add(c);
        }
        for (int i = 0; i < 6; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[4].add(c);
        }
        for (int i = 0; i < 6; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[5].add(c);
        }
        for (int i = 0; i < 6; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[6].add(c);
        }
        for (int i = 0; i < 6; i++) {
            Card c = deck.get();
            ConstraintHelper.tableau(this)[7].add(c);
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
        return "Castle";
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
        final GameWindow gw = Main.generateWindow(new Castle(), Deck.OrderBySuit);
        // properly exist program once selected.
        gw.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        gw.setVisible(true);
    }

    public java.util.Enumeration<Move> availableMoves() {
        java.util.Vector<Move> v = new java.util.Vector<Move>();
        for (Column c : tableau) {
            for (Pile p : foundation) {
                PotentialMoveCardFoundation pfm = new PotentialMoveCardFoundation(c, p);
                if (pfm.valid(this)) {
                    v.add(pfm);
                }
            }
        }
        if (v.isEmpty()) {
            for (Column c : tableau) {
                for (Column c2 : tableau) {
                    PotentialMoveCard pm = new PotentialMoveCard(c, c2);
                    if (pm.valid(this)) {
                        v.add(pm);
                    }
                }
            }
        }
        return v.elements();
    }

    IntegerView scoreView;

    IntegerView numLeftView;

    public Column[] tableau = new Column[8];

    public static final String tableauPrefix = "tableau";

    public ColumnView[] tableauView = new ColumnView[8];

    public Pile[] foundation = new Pile[4];

    public static final String foundationPrefix = "foundation";

    public PileView[] foundationView = new PileView[4];

    public Deck deck;

    DeckView deckView;

    @Override
    public Dimension getPreferredSize() {
        // default starting dimensions...
        return new Dimension(997, 635);
    }
}
