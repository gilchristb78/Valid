@(RootPackage: NameExpr, NumColumns: Expression)
// Fields
CardImages ci = getCardImages();

int cw = ci.getWidth();
int ch = ci.getHeight();

// Setup the scoreView
scoreView  = new IntegerView(score);
scoreView.setBounds (840, 20, 100, 60);
addViewWidget (scoreView);
scoreView.setMouseAdapter(new SolitaireReleasedAdapter(this));

// Setup numLeft
numLeftView = new IntegerView (getNumLeft());
//numLeftView.setBounds (300 + (numberOfColumns + 1) * ci.getWidth(), 20, 100, 60);
numLeftView.setBounds (300 + (@Java(NumColumns) + 1) * cw, 20, 100, 60);
addViewWidget(numLeftView);
updateNumberCardsLeft(48);

// add to our model a deck, properly shuffled using the seed.
deck = new Deck();
deck.create(seed);
addModelElement(deck);
deckView = new DeckView(deck);
deckView.setBounds(20, 20, cw, ch);
addViewWidget(deckView);
//deckView.setMouseAdapter(new DeckController (this, deckView)); 
deckView.setMouseAdapter(new @{Java(RootPackage)}.controller.DeckController (this));

columns = new Column[@Java(NumColumns)];
columnViews = new ColumnView[@Java(NumColumns)];
	
for (int i = 0; i < @Java(NumColumns); i++) {
	columns[i] = new Column(ColumnsPrefix + (i+1));
	addModelElement (columns[i]);		
	
	columnViews[i] = new ColumnView(columns[i]);
	columnViews[i].setBounds(40 + (i + 1) * cw + i * 20, 10,
				cw, ch + ci.getOverlap() * 13);
	addViewWidget(columnViews[i]);
	
	// register controllers
	columnViews[i].setMouseMotionAdapter (new SolitaireMouseMotionAdapter (this));
	columnViews[i].setUndoAdapter (new SolitaireUndoAdapter (this));
	//columnViews[i].setMouseAdapter (new ColumnController (this, columnViews[i]));
	columnViews[i].setMouseAdapter (new @{Java(RootPackage)}.controller.IdiotColumnController (this, columnViews[i]));
}

// prepare game
// each column gets a card from the deck.
int cardsPerColumn = 1;

for (int i = 0; i < @Java(NumColumns); i++) {
	for (int j = 0; j < cardsPerColumn; j++) {
		columns[i].add(deck.get());
	}
}