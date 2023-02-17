package org.combinators.solitaire.archway;

import ks.common.view.*;
import ks.common.controller.*;
import ks.common.model.*;
import ks.common.games.*;
import ks.client.gamefactory.GameWindow;
import ks.launcher.Main;
import java.awt.event.*;
import java.awt.Dimension;
import org.combinators.solitaire.archway.controller.*;
import org.combinators.solitaire.archway.model.*;

/**
 * The Game plugin is the constant upon which all other plugins are refined.
 * __p__
 * It is a fully working plugin that has absolutely no behavior or meaning. It won't have the
 * Score or NumberOfCardsLeft, but it will at least be able to show a blank playing field.
 * __p__
 * __author: George T. Heineman (heineman__cs.wpi.edu)
 */
public class Archway extends Solitaire implements SolvableSolitaire {

    /**
     * Enable refinements to determine whether game has been won.
     */
    public boolean hasWon() {
        boolean hasWon = true;
        {
            int _ct = 0;
            for (Stack st : kingsdownfoundation) {
                _ct += st.count();
            }
            if (_ct != 52) {
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
        for (int j = 0; j < 13; j++) {
            reserve[j] = new Pile(reservePrefix + (j + 1));
            addModelElement(reserve[j]);
            reserveView[j] = new PileView(reserve[j]);
        }
        // Multi-decks are constructed from stock size.
        // Basic start of pretty much any solitaire game that requires a deck.
        deck = new MultiDeck("deck", 2);
        int seed = getSeed();
        deck.create(seed);
        addModelElement(deck);
        for (int j = 0; j < 4; j++) {
            tableau[j] = new Column(tableauPrefix + (j + 1));
            addModelElement(tableau[j]);
            tableauView[j] = new ColumnView(tableau[j]);
        }
        for (int j = 0; j < 4; j++) {
            foundation[j] = new AcesUpPile(foundationPrefix + (j + 1));
            addModelElement(foundation[j]);
            foundationView[j] = new AcesUpPileView(foundation[j]);
        }
        for (int j = 0; j < 4; j++) {
            kingsdownfoundation[j] = new KingsDownPile(kingsdownfoundationPrefix + (j + 1));
            addModelElement(kingsdownfoundation[j]);
            kingsdownfoundationView[j] = new KingsDownPileView(kingsdownfoundation[j]);
        }
        reserveView[0].setBounds(54, 513, 73, 97);
        addViewWidget(reserveView[0]);
        reserveView[1].setBounds(54, 405, 73, 97);
        addViewWidget(reserveView[1]);
        reserveView[2].setBounds(54, 297, 73, 97);
        addViewWidget(reserveView[2]);
        reserveView[3].setBounds(54, 189, 73, 97);
        addViewWidget(reserveView[3]);
        reserveView[4].setBounds(108, 81, 73, 97);
        addViewWidget(reserveView[4]);
        reserveView[5].setBounds(270, 27, 73, 97);
        addViewWidget(reserveView[5]);
        reserveView[6].setBounds(378, 27, 73, 97);
        addViewWidget(reserveView[6]);
        reserveView[7].setBounds(486, 27, 73, 97);
        addViewWidget(reserveView[7]);
        reserveView[8].setBounds(648, 81, 73, 97);
        addViewWidget(reserveView[8]);
        reserveView[9].setBounds(702, 189, 73, 97);
        addViewWidget(reserveView[9]);
        reserveView[10].setBounds(702, 297, 73, 97);
        addViewWidget(reserveView[10]);
        reserveView[11].setBounds(702, 405, 73, 97);
        addViewWidget(reserveView[11]);
        reserveView[12].setBounds(702, 513, 73, 97);
        addViewWidget(reserveView[12]);
        tableauView[0].setBounds(270, 270, 73, 776);
        addViewWidget(tableauView[0]);
        tableauView[1].setBounds(358, 270, 73, 776);
        addViewWidget(tableauView[1]);
        tableauView[2].setBounds(446, 270, 73, 776);
        addViewWidget(tableauView[2]);
        tableauView[3].setBounds(534, 270, 73, 776);
        addViewWidget(tableauView[3]);
        foundationView[0].setBounds(54, 621, 73, 97);
        addViewWidget(foundationView[0]);
        foundationView[1].setBounds(135, 621, 73, 97);
        addViewWidget(foundationView[1]);
        foundationView[2].setBounds(54, 729, 73, 97);
        addViewWidget(foundationView[2]);
        foundationView[3].setBounds(135, 729, 73, 97);
        addViewWidget(foundationView[3]);
        kingsdownfoundationView[0].setBounds(621, 621, 73, 97);
        addViewWidget(kingsdownfoundationView[0]);
        kingsdownfoundationView[1].setBounds(702, 621, 73, 97);
        addViewWidget(kingsdownfoundationView[1]);
        kingsdownfoundationView[2].setBounds(621, 729, 73, 97);
        addViewWidget(kingsdownfoundationView[2]);
        kingsdownfoundationView[3].setBounds(702, 729, 73, 97);
        addViewWidget(kingsdownfoundationView[3]);
        for (int j = 0; j < 13; j++) {
            reserveView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            reserveView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            reserveView[j].setMouseAdapter(new PileController(this, reserveView[j]));
        }
        for (int j = 0; j < 4; j++) {
            tableauView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            tableauView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            tableauView[j].setMouseAdapter(new ColumnController(this, tableauView[j]));
        }
        for (int j = 0; j < 4; j++) {
            foundationView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            foundationView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            foundationView[j].setMouseAdapter(new AcesUpPileController(this, foundationView[j]));
        }
        for (int j = 0; j < 4; j++) {
            kingsdownfoundationView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            kingsdownfoundationView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            kingsdownfoundationView[j].setMouseAdapter(new KingsDownPileController(this, kingsdownfoundationView[j]));
        }
        {
            java.util.ArrayList<Card> tmp = new java.util.ArrayList<Card>();
            ks.common.model.Stack keep = new ks.common.model.Stack();
            int _limit = 1;
            while (!deck.empty()) {
                Card card = deck.get();
                if ((card.getRank() == 1 && card.getSuit() == 4)) {
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
        {
            java.util.ArrayList<Card> tmp = new java.util.ArrayList<Card>();
            ks.common.model.Stack keep = new ks.common.model.Stack();
            int _limit = 1;
            while (!deck.empty()) {
                Card card = deck.get();
                if ((card.getRank() == 1 && card.getSuit() == 3)) {
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
        {
            java.util.ArrayList<Card> tmp = new java.util.ArrayList<Card>();
            ks.common.model.Stack keep = new ks.common.model.Stack();
            int _limit = 1;
            while (!deck.empty()) {
                Card card = deck.get();
                if ((card.getRank() == 1 && card.getSuit() == 2)) {
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
        {
            java.util.ArrayList<Card> tmp = new java.util.ArrayList<Card>();
            ks.common.model.Stack keep = new ks.common.model.Stack();
            int _limit = 1;
            while (!deck.empty()) {
                Card card = deck.get();
                if ((card.getRank() == 1 && card.getSuit() == 1)) {
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
            for (Stack st : ConstraintHelper.foundation(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        {
            java.util.ArrayList<Card> tmp = new java.util.ArrayList<Card>();
            ks.common.model.Stack keep = new ks.common.model.Stack();
            int _limit = 1;
            while (!deck.empty()) {
                Card card = deck.get();
                if ((card.getRank() == 13 && card.getSuit() == 4)) {
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
        {
            java.util.ArrayList<Card> tmp = new java.util.ArrayList<Card>();
            ks.common.model.Stack keep = new ks.common.model.Stack();
            int _limit = 1;
            while (!deck.empty()) {
                Card card = deck.get();
                if ((card.getRank() == 13 && card.getSuit() == 3)) {
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
        {
            java.util.ArrayList<Card> tmp = new java.util.ArrayList<Card>();
            ks.common.model.Stack keep = new ks.common.model.Stack();
            int _limit = 1;
            while (!deck.empty()) {
                Card card = deck.get();
                if ((card.getRank() == 13 && card.getSuit() == 2)) {
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
        {
            java.util.ArrayList<Card> tmp = new java.util.ArrayList<Card>();
            ks.common.model.Stack keep = new ks.common.model.Stack();
            int _limit = 1;
            while (!deck.empty()) {
                Card card = deck.get();
                if ((card.getRank() == 13 && card.getSuit() == 1)) {
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
            for (Stack st : ConstraintHelper.kingsdownfoundation(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        for (int i = 0; i < 12; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
                Card c = deck.get();
                st.add(c);
            }
        }
        for (int i = 0; i < 48; i++) {
            Card card = deck.get();
            int _idx = card.getRank() - Card.ACE;
            ConstraintHelper.reserve(this)[_idx].add(card);
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
        return "Archway";
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
        final GameWindow gw = Main.generateWindow(new Archway(), Deck.OrderBySuit);
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
        // Try all moves from the Reserve to the Aces and Kings Foundation and the Tableau.
        for (Pile r : reserve) {
            for (AcesUpPile a : foundation) {
                ReserveToFoundation rtf = new PotentialReserveToFoundation(r, a);
                if (rtf.valid(this)) {
                    v.add(rtf);
                }
            }
            for (KingsDownPile k : kingsdownfoundation) {
                ReserveToKingsFoundation rkf = new PotentialReserveToKingsFoundation(r, k);
                if (rkf.valid(this)) {
                    v.add(rkf);
                }
            }
            for (Column t : tableau) {
                ReserveToTableau rt = new PotentialReserveToTableau(r, t);
                if (rt.valid(this)) {
                    v.add(rt);
                }
            }
        }
        // Try all moves from the Tableau to the Aces and Kings Foundation.
        for (Column t : tableau) {
            for (AcesUpPile a : foundation) {
                TableauToFoundation tf = new PotentialTableauToFoundation(t, a);
                if (tf.valid(this)) {
                    v.add(tf);
                }
            }
            // TODO: The 3H is duplicated when returned to the Tableau.
            for (KingsDownPile k : kingsdownfoundation) {
                TableauToKingsFoundation tk = new PotentialTableauToKingsFoundation(t, k);
                if (tk.valid(this)) {
                    v.add(tk);
                }
            }
        }
        return v.elements();
    }

    IntegerView scoreView;

    IntegerView numLeftView;

    public Pile[] reserve = new Pile[13];

    public static final String reservePrefix = "reserve";

    public PileView[] reserveView = new PileView[13];

    public Deck deck;

    DeckView deckView;

    public Column[] tableau = new Column[4];

    public static final String tableauPrefix = "tableau";

    public ColumnView[] tableauView = new ColumnView[4];

    public AcesUpPile[] foundation = new AcesUpPile[4];

    public static final String foundationPrefix = "foundation";

    public AcesUpPileView[] foundationView = new AcesUpPileView[4];

    public KingsDownPile[] kingsdownfoundation = new KingsDownPile[4];

    public static final String kingsdownfoundationPrefix = "kingsdownfoundation";

    public KingsDownPileView[] kingsdownfoundationView = new KingsDownPileView[4];

    @Override
    public Dimension getPreferredSize() {
        // default starting dimensions...
        return new Dimension(775, 1046);
    }
}
