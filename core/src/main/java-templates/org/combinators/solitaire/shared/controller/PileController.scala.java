@(RootPackage: Name,
        PileDesignate: SimpleName,
        NameOfTheGame: SimpleName,
        PileMouseClicked: Seq[Statement],
        PileMousePressed: (SimpleName, SimpleName) => Seq[Statement],
        PileMouseReleased: Seq[Statement])
package @{Java(RootPackage)}.controller;

// try this out...
import @{Java(RootPackage)}.*;
import @{Java(RootPackage)}.model.*;    // all moves are generated within this package

import java.awt.event.MouseEvent;
import ks.common.model.*;
import ks.common.view.*;
import ks.common.controller.*;

public class @{Java(PileDesignate)}PileController extends SolitaireReleasedAdapter {

  protected @Java(NameOfTheGame) theGame;
  protected PileView src;

  public @{Java(PileDesignate)}PileController(@Java(NameOfTheGame) theGame, PileView src) {
    super(theGame);

    this.theGame = theGame;
    this.src = src;
  }

  public void mousePressed(MouseEvent me) {
    Container c = theGame.getContainer();

    // Another Safety Check
    Widget w = c.getActiveDraggingObject();
    if (w != Container.getNothingBeingDragged()) {
        System.err.println("mousePressed: Unexpectedly encountered a Dragging Object during a Mouse press.");
        return;
    }

    // should we ignore this
    boolean me_ignore = true;
    Widget me_widget = null;

    // must both define me_ignore to false and set me_widget to valid widget
    @Java(PileMousePressed(Java("me_widget").simpleName(), Java("me_ignore").simpleName()))

    if (me_ignore) {
        return;
    }

    if (me_widget == null) {
        c.releaseDraggingObject();
    } else {
        // We tell the container what item is being dragged (and where in the Widget it was clicked)...
        c.setActiveDraggingObject(me_widget, me);

        // and where it came from
        c.setDragSource(src);
    }

    c.repaint();
  }

  public void mouseClicked(MouseEvent me) {
      @Java(PileMouseClicked)
  }

  public void mouseReleased(MouseEvent me) {
    Container c = theGame.getContainer();

    // Safety Check
    Widget w = c.getActiveDraggingObject();
    if (w == Container.getNothingBeingDragged()) {
        return;
    }

    @Java(PileMouseReleased)

    // release the dragging object since the move is now complete (this
    // will reset container's dragSource).
    c.releaseDraggingObject();

    c.repaint();
  }
}
