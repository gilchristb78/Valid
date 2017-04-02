@(ModelInit: Seq[Statement], ViewInit: Seq[Statement], ControlInit : Seq[Statement], SetupInitialState : Seq[Statement])

// Basic start of pretty much any solitaire game that requires a deck.
deck = new Deck ("deck");
int seed = getSeed();
deck.create(seed);
addModelElement (deck);

// Fields
CardImages ci = getCardImages();

int cw = ci.getWidth();
int ch = ci.getHeight();

@Java(ModelInit)

@Java(ViewInit)

@Java(ControlInit)

@Java(SetupInitialState)