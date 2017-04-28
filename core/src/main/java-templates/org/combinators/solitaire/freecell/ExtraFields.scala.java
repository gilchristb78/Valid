@(NumHomePiles: Expression, NumFreePiles: Expression, NumColumns:Expression)


//every solitaire variation must have scoreview but numLeft is optional
//only show it if you need it
IntegerView scoreView;
IntegerView numLeftView;

public Deck deck;  // HACK: needs to be public b/c of deck controller.
DeckView deckView;

protected static final String FreePilesPrefix = "FreePiles";		
public Pile[] fieldFreePiles = new Pile[@Java(NumFreePiles)];
protected PileView[] fieldFreePileViews = new PileView[@Java(NumFreePiles)];

protected static final String ColumnsPrefix = "Columns";		
public Column[] fieldColumns = new Column[@Java(NumColumns)];
protected ColumnView[] fieldColumnViews = new ColumnView[@Java(NumColumns)];
