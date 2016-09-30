System.out.println ("FIX ME!!!!");
Card baseCard = destination.peek();

if (destination.empty()) {
	if (movingCard.getRank() == Card.ACE) {
		return true;
	}
} else if (baseCard.getRank() == (movingCard.getRank() - 1) && baseCard.sameSuit(movingCard)) {
	return true;
}
