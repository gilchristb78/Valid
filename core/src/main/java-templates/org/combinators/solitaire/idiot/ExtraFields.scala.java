@(numberOfColumns: Expression)

// every solitaire variation must have scoreview but numLeft is optional
// only show it if you need it
IntegerView scoreView;
IntegerView numLeftView;

public Deck deck;  // HACK: needs to be public b/c of deck controller.
DeckView deckView;

// HACK: Needs to be be public (for now) because of pile controller.
protected static final String ColumnsPrefix = "Columns";	
public Column[] columns = new Column[@Java(numberOfColumns)];
ColumnView[] columnViews = new ColumnView[@Java(numberOfColumns)];

