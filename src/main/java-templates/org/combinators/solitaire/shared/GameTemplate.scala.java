@(rootPackage:Name,
    nameParameter:SimpleName,
    winParameter:Seq[Statement],
    initializeSteps:Seq[Statement])

package @Java(rootPackage);

// these are still too many to include all at once.

import ks.common.view.*;
import ks.common.controller.*;
import ks.common.model.*;
import ks.common.games.*;
import ks.client.gamefactory.GameWindow;
import ks.launcher.Main;

import java.awt.event.*;
import java.awt.Dimension;

/**
 * The Game plugin is the constant upon which all other plugins are refined.
 * __p__
 * It is a fully working plugin that has absolutely no behavior or meaning. It won't have the
 * Score or NumberOfCardsLeft, but it will at least be able to show a blank playing field.
 * __p__
 * __author: George T. Heineman (heineman__cs.wpi.edu)
 */
public class @Java(nameParameter) extends Solitaire {

    /** Enable refinements to determine whether game has been won. */
    public boolean hasWon() {
        @Java(winParameter)
        return false;
    }

    /**
     * Refinement determines initializations.
     */
    public void initialize() {
        @Java(initializeSteps)

        // Cover the Container for any events not handled by a widget:
        getContainer().setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
        getContainer().setMouseAdapter(new SolitaireReleasedAdapter(this));
        getContainer().setUndoAdapter(new SolitaireUndoAdapter(this));
    }

    /**
     * Refinement determines name.
     */
    public String getName() {
        return "@nameParameter";   // special case to be handled in parser specially. Parser quotes.
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
        final GameWindow gw = Main.generateWindow(new @Java(nameParameter) (), Deck.OrderBySuit);
        // properly exist program once selected.
        gw.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        gw.setVisible(true);

    }
}
