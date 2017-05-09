@(RootPackage: Name, NameOfTheGame: SimpleName)
// Try to make the move
Move m =
    new @{Java(RootPackage)}.model.MoveRemoveCards(
        ((@{Java(RootPackage)}.@{Java(NameOfTheGame)})theGame).pile);

if (m.doMove (theGame)) {
  // SUCCESS
  theGame.pushMove(m);
}