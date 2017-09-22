@(NumFreePiles: Expression, NumColumns:Expression)

public int numberVacant () {
	int numEmpty = 0;
	for (int f = 0; f < @Java(NumFreePiles); f++) {
		if (fieldFreePiles[f].empty()) numEmpty++;
	}

	// now count columns
	for (int c = 0; c < @Java(NumColumns); c++) {
		if (fieldColumns[c].empty()) numEmpty++;
	}

	return numEmpty;		
}

/**
 * A card is unneeded when no lower-rank cards of the opposite color remain 
 * in the playing area. 
 * <p>
 * Returns TRUE if cards of (rank-1) and opposite colored suit have both
 * already been played to the foundation.
 * <p>
 * Note that true is returned if an ACE is passed in.
 */
protected boolean unneeded(int rank, int suit) {
	// error situation.
	if (rank == Card.ACE) return true;

	// see if cards of next lower rank and opposite color are both played
	// in the foundation.
	int countOppositeColorLowerRank = 0;
	for (int b = 0; b < fieldHomePiles.length; b++) {
		if (fieldHomePiles[b].empty()) continue;

		Card bc = fieldHomePiles[b].peek();
		if (bc.oppositeColor (suit) && bc.getRank() >= rank-1) { 
			countOppositeColorLowerRank++;	
		}
	}

	// determine validity
	return (countOppositeColorLowerRank == 2);
}

// should be encapsulated out elswhere since this is standard logic...
public void tryAutoMoves() {
	Move m;
	do {
		m = autoMoveAvailable();
		if (m!= null) {
			if (m.doMove (this)) {
				pushMove (m);
				refreshWidgets();
			} else {
				// ERROR. Break now!
				break;
			}
		}
	} while (m != null);
}


/** For now, no automoves just yet... */
public Move autoMoveAvailable() {
	// opportunity for L2 inspection of elements to generate move codes...
	// NOTE: Here we embed 'field' because this is used as a parameter in the bindings.
	// 1. First see if any columnBaseMove allowed.
	for (int c = 0; c < fieldColumns.length; c++) {
		if (fieldColumns[c].empty()) continue;

		if (fieldColumns[c].rank() == Card.ACE) {

			// find the empty destination pile
			Pile emptyDest = null;
			for (int i = 0; i < fieldHomePiles.length; i++) {
				if (fieldHomePiles[i].empty()) {
					emptyDest = fieldHomePiles[i];
				}
			}

			// SANITY CHECK.
			if (emptyDest == null) {
				throw new IllegalStateException ("ACE is available to play but no open destination piles.");
			}

			return new PotentialBuildColumn (fieldColumns[c], emptyDest);
		}

		Card cc = fieldColumns[c].peek();

		// try to find a destination it goes to.
		Move theMove = null;
		boolean foundMove = false;
		for (int b = 0; b<fieldHomePiles.length; b++) {
			theMove = new PotentialBuildColumn (fieldColumns[c], fieldHomePiles[b]);
			if (theMove.valid (this)) {
				foundMove = true;
				break;
			}
		}

		// see if cards of next lower rank and opposite color are both played
		// in the foundation; we have to do this *two* levels since we need 
		// to know that all four suits are taken care of. Consider the decision
		// to place a 4H into a base pile; we need to know that both 3C and 3S
		// have been placed. We also need to know that the 2D has been played
		// (note: for a valid move we know that the 2H has been played).
		if (foundMove) {
			if (unneeded (cc.getRank(), cc.getSuit())) {
				int otherSuit = cc.getSuit();
				if ((otherSuit == Card.CLUBS) || (otherSuit == Card.SPADES)) {
					otherSuit = Card.HEARTS;  // arbitrary RED
				} else {
					otherSuit = Card.CLUBS; // arbitrary BLACK
				}

				// now go down one more level
				if (unneeded (cc.getRank()-1, otherSuit)) {
					return theMove;
				}
			}
		}
	}

	// 2. Second see if any FreeCellBaseMove allowed.
	Move theMove = null;
	boolean foundMove = false;
	Card bc = null;
	for (int f = 0; f < fieldFreePiles.length; f++) {
		if (fieldFreePiles[f].empty()) continue;

		// try to find a destination it goes to.
		for (int b = 0; b<fieldHomePiles.length; b++) {
			theMove = new PotentialBuildFreePileCard (fieldFreePiles[f], fieldHomePiles[b]);
			if (theMove.valid (this)) {
				bc = fieldFreePiles[f].peek();
				foundMove = true;
				break;
			}
		}

		if (foundMove) {
			if (unneeded(bc.getRank(), bc.getSuit())) {
				int otherSuit = bc.getSuit();
				if ((otherSuit == Card.CLUBS)
						|| (otherSuit == Card.SPADES)) {
					otherSuit = Card.HEARTS; // arbitrary RED
				} else {
					otherSuit = Card.CLUBS; // arbitrary BLACK
				}

				// ACEs can be moved immediately...
				if (bc.getRank() == Card.ACE) {
					return theMove;
				}

				// now go down one more level
				if (unneeded(bc.getRank() - 1, otherSuit)) {
					return theMove;
				}
			}

			// no move allowed.
			return null;
		}
	}

	// if nothing found, stop
	if (!foundMove) {
		theMove = null;
	}

	return theMove;	
}
