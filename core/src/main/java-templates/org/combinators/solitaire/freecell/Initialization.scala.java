@(NameOfTheGame: String, NumColumns:Expression, NumHomePiles: Expression, NumFreePiles: Expression)

//how to set up the cards for FREE CELL
deck = new Deck ("deck");
int seed = getSeed();
deck.create(seed);
addModelElement (deck);

// Fields
CardImages ci = getCardImages();

int cw = ci.getWidth();
int ch = ci.getHeight();

// Setup the scoreView
scoreView  = new IntegerView(score);
scoreView.setFontSize (24);
scoreView.setBounds (75 + 4*cw, 30, 30, 30);
addViewWidget (scoreView);

// setup cards
for (int i = 0; i < @Java(NumColumns); i++) {
	fieldColumns[i] = new Column(ColumnsPrefix + (i+1));
	addModelElement (fieldColumns[i]);		
	fieldColumnViews[i] = new ColumnView(fieldColumns[i]);
	fieldColumnViews[i].setBounds(45+15*i+i*cw, 40 + ch, cw, 13*ch);
	addViewWidget (fieldColumnViews[i]);

	// register controllers
	fieldColumnViews[i].setMouseMotionAdapter (new SolitaireMouseMotionAdapter (this));
	fieldColumnViews[i].setUndoAdapter (new SolitaireUndoAdapter (this));
	fieldColumnViews[i].setMouseAdapter (new @(NameOfTheGame)ColumnController (this, fieldColumnViews[i]));
}

for (int i = 0; i < @Java(NumHomePiles); i++) {
	fieldHomePiles[i] = new Pile (HomePilesPrefix + (i+1));
	addModelElement (fieldHomePiles[i]); 
	fieldHomePileViews[i] = new PileView(fieldHomePiles[i]);
	fieldHomePileViews[i].setBounds(125+15*i+(i+4)*cw, 20, cw, ch);
	addViewWidget (fieldHomePileViews[i]);

	// register controllers
	fieldHomePileViews[i].setMouseMotionAdapter (new SolitaireMouseMotionAdapter (this));
	fieldHomePileViews[i].setUndoAdapter (new SolitaireUndoAdapter (this));
	fieldHomePileViews[i].setMouseAdapter (new HomePileController (this, fieldHomePileViews[i]));
}

for (int i = 0; i < @Java(NumFreePiles); i++) {
	fieldFreePiles[i] = new Pile (FreePilesPrefix + (i+1));
	addModelElement (fieldFreePiles[i]);
	fieldFreePileViews[i] = new PileView (fieldFreePiles[i]);
	fieldFreePileViews[i].setBounds(10+15*i+i*cw, 20, cw, ch);
	addViewWidget (fieldFreePileViews[i]);

	// register controllers
	fieldFreePileViews[i].setMouseMotionAdapter (new SolitaireMouseMotionAdapter (this));
	fieldFreePileViews[i].setUndoAdapter (new SolitaireUndoAdapter (this));
	fieldFreePileViews[i].setMouseAdapter (new FreeCellPileController (this, fieldFreePileViews[i]));
}

for (int j = 0; j < 4; j++) {
	for (int i = 0; i < @Java(NumColumns)-1; i++) {
		fieldColumns[j].add(deck.get());
	}
}

for (int j = 4; j < 8; j++) {
	for (int i = 0; i < @Java(NumColumns)-2; i++) {
		fieldColumns[j].add(deck.get());
	}
}
