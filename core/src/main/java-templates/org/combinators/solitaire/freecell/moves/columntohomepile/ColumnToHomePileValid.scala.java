Card baseCard = destination.peek();
if (movingColumn.count() != 1) {
	return false;
}

if (destination.empty()) {
	if (movingColumn.rank() == Card.ACE) {
		return true;
	}
} else if (baseCard.getRank() == (movingColumn.rank() - 1) && baseCard.sameSuit(movingColumn.peek())) {
	return true;
}
