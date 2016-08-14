@(RootPackage: NameExpr, NameOfTheGame: NameExpr)
// Try to make the move
Move m =
    new solitaire.narcotic.model.MoveRemoveCards(
        ((@{Java(RootPackage)}.@{Java(NameOfTheGame)})theGame).pile);

if (m.doMove (theGame)) {
  // SUCCESS
  theGame.pushMove(m);
}