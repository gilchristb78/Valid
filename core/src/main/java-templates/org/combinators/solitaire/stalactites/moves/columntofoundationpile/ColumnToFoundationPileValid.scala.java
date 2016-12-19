Card baseCard = destination.peek();
if (movingColumn.count() != 1) {
	return false;
}

int increment = game.getIncrement();

// If empty, then must check corresponding base column card to check to see if next in line.
// Could be incrementing or decrementing, so must take into account that possibility as well
int idx = -1;
for (idx = 0; idx < game.cards.length; idx++) {
	if (foundation[idx] == destination) {
		break; // find proper index.
	}
}

if (destination.empty()) {
	if (movingColumn.rank() == Card.ACE) {
		return true;
	}
} else if (baseCard.sameSuit(movingColumn.peek())) {
	// now check inc/dec
	if (game.getIncrement() == 0) || (game.getIncre)
	return true;
}
