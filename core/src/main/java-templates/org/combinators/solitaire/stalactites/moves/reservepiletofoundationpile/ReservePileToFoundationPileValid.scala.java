@(Root: Name, GameName: SimpleName)

Card baseCard = destination.peek();

@{Java(Root)}.@{Java(GameName)} stalactites = (@{Java(Root)}.@{Java(GameName)}) game;
int increment = stalactites.getIncrement();

// If empty, then must check corresponding base column card to check to see if next in line.
// Could be incrementing or decrementing, so must take into account that possibility as well
int idx = -1; 
for (idx = 0; idx < stalactites.cards.length; idx++) {
	if (stalactites.foundation[idx] == destination) {
		break; // find proper index.
	}
}

if (destination.empty()) {
	// If direction is + or undecided, return true if accepted
	if (increment == 0 || increment == 1) {
        if ((stalactites.cards[idx].getRank() % 13 + 1) == movingCard.getRank()) {
        	orientation = 1;
        	return true;
        }
    }
	
	// If direction is - or undecided, return true if accepted
    if (increment == 0 || increment == -1) {
        if (stalactites.cards[idx].getRank() == movingCard.getRank() % 13 + 1) {
        	orientation = -1;
        	return true;
        }
    }
} else if (destination.count() < 12) {
	// Direction MUST have been decided at this point (since not empty, right?)
	if (increment == 1) {
		orientation = 1;
		return ((baseCard.getRank() % 13 + 1) == movingCard.getRank());
	}
	if (increment == -1) {
		orientation = -1;
		return (baseCard.getRank() == movingCard.getRank() % 13 + 1);
	}
	
	// not valid
	return false;
}

