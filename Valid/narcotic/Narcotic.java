package org.combinators.solitaire.narcotic;

import ks.common.view.*;
import ks.common.controller.*;
import ks.common.model.*;
import ks.common.games.*;
import ks.client.gamefactory.GameWindow;
import ks.launcher.Main;
import java.awt.event.*;
import java.awt.Dimension;
import org.combinators.solitaire.narcotic.controller.*;
import org.combinators.solitaire.narcotic.model.*;

/**
 * The Game plugin is the constant upon which all other plugins are refined.
 * __p__
 * It is a fully working plugin that has absolutely no behavior or meaning. It won't have the
 * Score or NumberOfCardsLeft, but it will at least be able to show a blank playing field.
 * __p__
 * __author: George T. Heineman (heineman__cs.wpi.edu)
 */
public class Narcotic extends Solitaire {

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
        if (deck.count() != 0) {
            return false;
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
        for (int j = 0; j < 4; j++) {
            tableau[j] = new Pile(tableauPrefix + (j + 1));
            addModelElement(tableau[j]);
            tableauView[j] = new PileView(tableau[j]);
        }
        // Single deck instantiated as is
        // Basic start of pretty much any solitaire game that requires a deck.
        deck = new Deck("deck");
        int seed = getSeed();
        deck.create(seed);
        addModelElement(deck);
        tableauView[0].setBounds(120, 20, 73, 97);
        addViewWidget(tableauView[0]);
        tableauView[1].setBounds(208, 20, 73, 97);
        addViewWidget(tableauView[1]);
        tableauView[2].setBounds(296, 20, 73, 97);
        addViewWidget(tableauView[2]);
        tableauView[3].setBounds(384, 20, 73, 97);
        addViewWidget(tableauView[3]);
        deckView = new DeckView(deck);
        deckView.setBounds(15, 20, 73, 97);
        addViewWidget(deckView);
        for (int j = 0; j < 4; j++) {
            tableauView[j].setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
            tableauView[j].setUndoAdapter(new SolitaireUndoAdapter(this));
            tableauView[j].setMouseAdapter(new PileController(this, tableauView[j]));
        }
        deckView.setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
        deckView.setUndoAdapter(new SolitaireUndoAdapter(this));
        deckView.setMouseAdapter(new DeckController(this, deckView));
        for (int i = 0; i < 1; i++) {
            for (Stack st : ConstraintHelper.tableau(this)) {
                Card c = deck.get();
                st.add(c);
            }
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
        return "Narcotic";
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
        final GameWindow gw = Main.generateWindow(new Narcotic(), Deck.OrderBySuit);
        // properly exist program once selected.
        gw.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        gw.setVisible(true);
    }

    public boolean toLeftOf(Stack target, Stack src) {
        // Check whether target is to left of src
        for (int i = 0; i < tableau.length; i++) {
            if (tableau[i] == target) {
                // found target first (in left-right)
                return true;
            }
            if (tableau[i] == src) {
                // found src first
                return false;
            }
        }
        // will never get here
        return false;
    }

    public boolean allSameRank(Stack[] group) {
        if (group[0].empty()) {
            return false;
        }
        // Check whether tops of all piles are same rank
        for (int i = 1; i < group.length; i++) {
            if (group[i].empty()) {
                return false;
            }
            if (group[i].rank() != group[i - 1].rank()) {
                return false;
            }
        }
        // looks good
        return true;
    }

    IntegerView scoreView;

    IntegerView numLeftView;

    public Pile[] tableau = new Pile[4];

    public static final String tableauPrefix = "tableau";

    public PileView[] tableauView = new PileView[4];

    public Deck deck;

    DeckView deckView;
}
