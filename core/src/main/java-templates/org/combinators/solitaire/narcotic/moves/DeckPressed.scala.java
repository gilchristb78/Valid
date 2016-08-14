@(RootPackage: NameExpr, NameOfTheGame: NameExpr)
Move mx =
    new solitaire.narcotic.model.DealStacksMove(
        (@{Java(RootPackage)}.@{Java(NameOfTheGame)}).deck,
        (@{Java(RootPackage)}.@{Java(NameOfTheGame)}).pile);

if (mx.doMove(theGame)) {
    // SUCCESS: have solitaire game store this move
    theGame.pushMove(mx);
} else {
    // Find the deck from our model and pile array. Frustrating that scope context could interfere. I guess
    // we could have placed this whole thing into its own block. This is issue with L1-language
    Move my=
        new solitaire.narcotic.model.ResetDeck(
            (@{Java(RootPackage)}.@{Java(NameOfTheGame)}).deck,
            (@{Java(RootPackage)}.@{Java(NameOfTheGame)}).pile);

    if (my.doMove(theGame)){
        // SUCCESS: have solitaire game store this move
        theGame.pushMove(my);
    }
}