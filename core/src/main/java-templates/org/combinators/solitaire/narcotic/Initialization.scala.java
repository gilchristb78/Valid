@(NumPiles: Expression)
// Fields
CardImages ci = getCardImages();

int cw = ci.getWidth();
int ch = ci.getHeight();

// Setup the scoreView
scoreView  = new IntegerView(score);
scoreView.setBounds (100 + (@Java(NumPiles) + 1)*cw, 20, 100, 60);
addViewWidget (scoreView);
scoreView.setMouseAdapter(new SolitaireReleasedAdapter(this));

// Setup numLeft
numLeftView = new IntegerView (getNumLeft());
numLeftView.setBounds (200 + (@Java(NumPiles) + 1)*cw, 20, 100, 60);
addViewWidget (numLeftView);
numLeftView.setMouseAdapter(new SolitaireReleasedAdapter(this));

// add to our model a deck, properly shuffled using the seed.
deck = new Deck();
deck.create(seed);
addModelElement(deck);
deckView = new DeckView(deck);
deckView.setBounds(20, 20, cw, ch);
addViewWidget(deckView);
deckView.setMouseAdapter(new solitaire.narcotic.controller.DeckController (this));

for (int i = 0; i < @Java(NumPiles) ; i++) {
    pile[i] = new Pile("pile" + i);
    addModelElement(pile[i]);

    pileView[i]= new PileView(pile[i]);
    pileView[i].setBounds(40 + i*20 + (i+1)*cw, 20, cw, ch);
    addViewWidget(pileView[i]);
    pileView[i].setMouseAdapter(new solitaire.narcotic.controller.NarcoticPileController (this, pileView[i]));
}

updateNumberCardsLeft(52);

// prepare game
// each column gets a card from the deck.
for (int i = 0; i < @Java(NumPiles); i++) {
    pile[i].add(deck.get());
    updateNumberCardsLeft(-1);
}