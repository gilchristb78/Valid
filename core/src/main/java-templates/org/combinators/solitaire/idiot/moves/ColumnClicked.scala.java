@(RootPackage: NameExpr, NameOfTheGame: NameExpr)

// Point to our underlying model element.
Column column = (Column) src.getModelElement();
if (column.empty()) return;  // nothing to do on empty Column.

// Extract columns from game (a bit of a HACK)
Column col1 = (Column) theGame.getModelElement("Columns1");
Column col2 = (Column) theGame.getModelElement("Columns2");
Column col3 = (Column) theGame.getModelElement("Columns3");
Column col4 = (Column) theGame.getModelElement("Columns4");	
Move m = new @{Java(RootPackage)}.model.MoveRemoveCards (column, null, col1, col2, col3, col4);
    
if (m.doMove (theGame)) {
  // SUCCESS
  theGame.pushMove(m);
}
