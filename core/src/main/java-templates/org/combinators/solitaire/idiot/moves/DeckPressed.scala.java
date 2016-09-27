@(RootPackage: NameExpr, NameOfTheGame: NameExpr)
//Respond to the mousePressed events only on the deck
// Deck deck = (Deck) src.getModelElement();

// Extract from game object (a bit of a HACK)
Column col1 = (Column) theGame.getModelElement ("Columns1");
Column col2 = (Column) theGame.getModelElement ("Columns2");
Column col3 = (Column) theGame.getModelElement ("Columns3");
Column col4 = (Column) theGame.getModelElement ("Columns4");		

//Move m = new DealFourMove (deck, col1, col2, col3, col4);
m = new @{Java(RootPackage)}.model.DealStacksMove(((@{Java(RootPackage)}.@{Java(NameOfTheGame)})theGame).deck, 
		(new Stack[]{col1, col2, col3, col4}));
if (m.doMove (theGame)) {

	// Successful DealFour Move
	theGame.pushMove (m);

	// refresh all widgets
	theGame.refreshWidgets();
} else {
    // Find the deck from our model and pile array. Frustrating that scope context could interfere. I guess
    // we could have placed this whole thing into its own block. This is issue with L1-language
    Move my=
        new @{Java(RootPackage)}.model.ResetDeck(((@{Java(RootPackage)}.@{Java(NameOfTheGame)})theGame).deck, 
        		(new Stack[]{col1, col2, col3, col4}));

    if (my.doMove(theGame)){
        // SUCCESS: have solitaire game store this move
        theGame.pushMove(my);
    }
}