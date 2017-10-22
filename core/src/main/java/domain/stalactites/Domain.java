package domain.stalactites;

import domain.*;
import domain.constraints.*;
import domain.freeCell.FreePile;
import domain.freeCell.HomePile;
import domain.moves.ColumnMove;
import domain.moves.SingleCardMove;
import domain.ui.HorizontalPlacement;
import domain.ui.PlacementGenerator;
import org.combinators.solitaire.stalactites.StalactitesContainerTypes;

import java.awt.Point;
import java.util.Iterator;


/**
 * Programmatically construct full domain model for Stalactites.
 */
public class Domain extends Solitaire {

	public static void main (String[] args) {
		Domain sfc = new Domain();

		System.out.println("Available Moves:");
		for (Iterator<Move> it = sfc.getRules().drags(); it.hasNext(); ) {
			System.out.println("  " + it.next());
		}
	}

	public Domain() {

		PlacementGenerator places = new HorizontalPlacement(new Point (200, 120),
				card_width, card_height, card_gap);

		Foundation found = new Foundation(places);
		found.add (new Pile());
		found.add (new Pile());
		found.add (new Pile());
		found.add (new Pile());
		containers.put(SolitaireContainerTypes.Foundation, found);

		places = new HorizontalPlacement(new Point (15, 20),
				card_width, card_height, card_gap);
		Reserve reserve = new Reserve(places);
		reserve.add (new ReservePile());
		reserve.add (new ReservePile());
		containers.put(SolitaireContainerTypes.Reserve, reserve);

		places = new HorizontalPlacement(new Point (200, 20),
				card_width, card_height, card_gap);
		Container base = new Container(places);
		base.add (new Card());
        base.add (new Card());
        base.add (new Card());
        base.add (new Card());
		containers.put(StalactitesContainerTypes.Base, base);

        places = new HorizontalPlacement(new Point (15, 260),
                card_width, 8*card_height, card_gap);
        Tableau tableau = new Tableau(places);
        tableau.add (new Column());
        tableau.add (new Column());
        tableau.add (new Column());
        tableau.add (new Column());
        tableau.add (new Column());
        tableau.add (new Column());
        tableau.add (new Column());
        tableau.add (new Column());
        containers.put(SolitaireContainerTypes.Tableau, tableau);

		// defaults to 1 deck.
		Stock stock = new Stock();
		containers.put(SolitaireContainerTypes.Stock, stock);

		// wins once foundation contains same number of cards as stock
		Rules rules = new Rules();

        /**
         * Wikipedia description:
         *
         * The player deals four cards from the deck. These four cards form the (base for ) foundations.
         * These are placed in four CardView elements which are the "Base"
         The rest of the cards are dealt into eight columns of six cards each on the tableau. These cards can only be built up on the foundations regardless of suit and they cannot be built on each other.
         The initial layout of a game of Stalactites.

         Before the game starts, the player can decide on how the foundations should be built. Building can be either in ones (A-2-3-4-5-6-7-8-9-10-J-Q-K) or in twos (A-3-5-7-9-J-K-2-4-6-8-10-Q). Once the player makes up his mind, he begins building on the foundations from the cards on the tableau. The foundations are built, as already mentioned, up regardless of suit, and it goes round the corner, building from King to Ace (if building by ones) or from Queen to Ace (if building by twos) if necessary. The foundation cards turned sideways, though not necessarily be done, is a reminder of the last card's rank on each foundation.

         The cards in the tableau should be placed in the foundations according to the building method the player decides to use. But when there are cards that cannot (or does not want to) be moved to the foundations, certain cards can be placed on a reserve. Any card can be placed on the reserve. But once a card is placed on the reserve, it must be built on a foundation; it should never return to the tableau. Furthermore, the reserve can only hold two cards.

         The game is won when all cards are built onto the foundations, each having 13 cards. The four starting cards in the foundations don't have to be of the same rank; so results vary with each won game.

         */

		setRules(rules);

		// Not doing rules since changing to AST-based logic

	}
}
