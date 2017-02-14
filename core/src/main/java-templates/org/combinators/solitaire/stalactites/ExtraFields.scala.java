@(NumColumns: Expression, NumReservePiles: Expression, NumFoundations: Expression)

// every solitaire variation must have scoreview but numLeft is optional
// only show it if you need it
IntegerView scoreView;
IntegerView numLeftView;

public Deck deck;  // HACK: needs to be public b/c of deck controller.
DeckView deckView;

// HACK: Needs to be be public (for now) because of pile controller.
public Pile[] pile = new Pile[@Java(NumReservePiles)];
PileView[] pileView = new PileView[@Java(NumReservePiles)];

/** Columns in the tableau */
public Column tableau[] = new Column[@Java(NumColumns)];
protected ColumnView tableauViews[] = new ColumnView[@Java(NumColumns)];

/** Columns in the tableau */
public Pile reserve[] = new Pile[@Java(NumReservePiles)];
protected PileView reserveViews[] = new PileView[@Java(NumReservePiles)];

/** Foundation piles */
public Pile foundation[] = new Pile[@Java(NumFoundations)];
protected PileView foundationViews[] = new PileView[@Java(NumFoundations)];

/** Cards to display initial foundation */
public Card cards[] = new Card[@Java(NumFoundations)];
protected CardView cardViews[] = new CardView[@Java(NumFoundations)];
