@(RootPackage: Name, NameOfTheGame: SimpleName)

Card topCard = destination.peek();

// verify that column is in order and alternating suits
Column source;
Card movingCard;
int offset;

// moving column has everything
offset = movingColumn.count();
source = movingColumn;
movingCard = movingColumn.peek(0);

if (!source.descending (offset-numInColumn, offset) ||
		!source.alternatingColors(offset-numInColumn, offset))
	return false;

// validate that there are at least n-1 vacant spots for a 
// column of size n being moved
int numVacant = ((@{Java(RootPackage)}.@{Java(NameOfTheGame)})game).numberVacant();

// can't use the column into which we are moving as a count.
if (destination.empty()) { numVacant--; }
if (numVacant < numInColumn - 1)
	return false;

// moves to empty column are always allowed.
if (destination.empty())
	return true;

if (topCard.getRank() == (movingCard.getRank() + 1)
&& topCard.oppositeColor(movingCard)) {
	return true;
}

// if we get here, this falls through to "return false" so we do nothing.
