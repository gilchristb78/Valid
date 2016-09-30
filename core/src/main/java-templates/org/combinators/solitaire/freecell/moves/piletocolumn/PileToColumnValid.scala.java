Card topCard = destination.peek();

if (destination.empty()) {
   return true;
} 
	
Card baseCard = destination.peek();
if (baseCard.getRank() == (movingCard.getRank() + 1) && !baseCard.sameColor(movingCard)) {
    return true;
}