@(RootPackage: NameExpr,
        NameOfTheGame: NameExpr,
        DeckMousePressed: Seq[Statement])
package @{Java(RootPackage)}.controller;

// try this out...
import @{Java(RootPackage)}.*;

import java.awt.event.MouseEvent;
import ks.common.model.*;
import ks.common.view.*;
import ks.common.controller.*;

/**
 * Controller for Decks. Typical actions involve just dealing cards from deck, so this
 * kind of controller is simpler to write. This standardized deck controller has only
 * press events (for now).
 */
public class DeckController extends SolitaireReleasedAdapter {
	protected @Java(NameOfTheGame) theGame;

	public DeckController(@Java(NameOfTheGame) theGame) {
		super(theGame);

		this.theGame = theGame;
	}

	// Deal cards
	public void mousePressed(MouseEvent me) {
	    Move m;

		// Action on press
		@Java(DeckMousePressed)

		// have solitaire game refresh widgets that were affected
		theGame.refreshWidgets();
	}
}