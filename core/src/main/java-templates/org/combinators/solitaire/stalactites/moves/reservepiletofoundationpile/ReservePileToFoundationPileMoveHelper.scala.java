@(Name: SimpleName)

// will only be a single card
Card movingCard;


//// HACK: This should go into its own Implementation which somehow
//// is chained to this one or replaces it. Either way, we can't
//// put it here since that violates "Open for extension, closed
//// for modification
public @{Java(Name)}(Stack from, Card card, Stack to) {
	this(from, to);
	this.movingCard = card;
}
