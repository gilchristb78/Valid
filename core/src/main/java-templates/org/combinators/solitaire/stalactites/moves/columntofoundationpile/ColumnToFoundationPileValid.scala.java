@(Root: NameExpr, GameName: NameExpr)

Card baseCard = destination.peek();
if (movingColumn.count() != 1) {
	return false;
}

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

// TODO: Make sure don't EXCEED card match (or 13 cards in pile?)

if (destination.empty()) {
	if (increment == 0 || increment == 1) {
        return ((stalactites.cards[idx].getRank() % 13 + 1) == movingColumn.rank());
    }
    if (increment == 0 || increment == -1) {
        return (stalactites.cards[idx].getRank() == movingColumn.rank() % 13 + 1);
    }
} else  {
	// now check inc/dec
	if (increment == 0 || increment == 1) {
		return ((baseCard.getRank() % 13 + 1) == movingColumn.rank());
	}
	if (increment == 0 || increment == -1) {
		return (baseCard.getRank() == movingColumn.rank() % 13 + 1);
	}
	
	// not valid
	return false;
}
