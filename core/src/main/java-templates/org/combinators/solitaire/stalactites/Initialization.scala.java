@(NumColumns: Expression, NumReservePiles: Expression, NumFoundations: Expression)

// Fields
CardImages ci = getCardImages();

int cw = ci.getWidth();
int ch = ci.getHeight();

deck = new Deck("d");
deck.create(seed);
model.addElement(deck); 

// start with neutral increment
setIncrement(0);

// 48 cards to start once dealt
this.updateNumberCardsLeft(48);

// developing foundations
for (int i = 0; i < @Java(NumFoundations); i++) {
	foundation[i] = new Pile("foundation " + i);
	addModelElement(foundation[i]);
	
	foundationViews[i] = new PileView(foundation[i]);
	foundationViews[i].setBounds((i + 5) * (cw + 10) - 35, 120, cw, ch);
	addViewWidget(foundationViews[i]);
}



// get cards that represent foundation
for (int i = 0; i < @Java(NumFoundations); i++) {
	Card card = deck.get();
	cards[i] = new Card(card);

	cardViews[i] = new CardView(cards[i]);
	cardViews[i].setBounds((i + 5) * (cw + 10) - 35, 20, cw, ch);
	addViewWidget(cardViews[i]);
}


// developing tableau
for (int i = 0; i < @Java(NumColumns); i++) {
	tableau[i] = new Column("tableau " + i);
	addModelElement(tableau[i]);
	
	tableauViews[i] = new ColumnView(tableau[i]);
	tableauViews[i].setBounds((i +1) * (cw + 10) - 35, 240, cw, 2 * ch + 20);
	addViewWidget(tableauViews[i]);
}

// developing reserve
for (int i = 0; i < @Java(NumReservePiles); i++) {
	reserve[i] = new Pile("reserve " + i);
	addModelElement(reserve[i]);
	
	reserveViews[i] = new PileView(reserve[i]);
	reserveViews[i].setBounds((i+1) * (cw + 10) - 35, 120, cw, ch);
	addViewWidget(reserveViews[i]);
}

// now deal out the initial cards
//prepare game dealing faceup cards to each column

for (int height = 0; height < 6; height++) {
	for (int col = 0; col < @Java(NumColumns); col++) {
		Card c = deck.get();
		tableau[col].add(c);
	}
}