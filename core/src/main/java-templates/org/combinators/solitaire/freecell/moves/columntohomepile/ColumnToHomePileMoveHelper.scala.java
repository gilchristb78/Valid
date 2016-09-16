@(Name: NameExpr)

// will only be a single card
Column movingColumn;


//// HACK: This should go into its own Implementation which somehow
//// is chained to this one or replaces it. Either way, we can't
//// put it here since that violates "Open for extension, closed
//// for modification
public @{Java(Name)}(Stack from, Column cards, Stack to) {
	this(from, to);
	this.movingColumn = cards;
}
