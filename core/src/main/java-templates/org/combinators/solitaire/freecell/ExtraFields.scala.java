@(NumHomePiles: Expression, NumFreePiles: Expression, NumColumns:Expression)


//every solitaire variation must have scoreview but numLeft is optional
//only show it if you need it
IntegerView scoreView;
IntegerView numLeftView;

public Deck deck;  // HACK: needs to be public b/c of deck controller.
DeckView deckView;

//HACK: Needs to be be public (for now) because of pile controller.
protected static final String HomePilesPrefix = "HomePiles";
public Pile[] fieldHomePiles = new Pile[@Java(NumHomePiles)];
protected PileView[] fieldHomePileViews = new PileView[@Java(NumHomePiles)];

protected static final String FreePilesPrefix = "FreePiles";		
public Pile[] fieldFreePiles = new Pile[@Java(NumFreePiles)];
protected PileView[] fieldFreePileViews = new PileView[@Java(NumFreePiles)];

protected static final String ColumnsPrefix = "Columns";		
public Column[] fieldColumns = new Column[@Java(NumColumns)];
protected ColumnView[] fieldColumnViews = new ColumnView[@Java(NumColumns)];
