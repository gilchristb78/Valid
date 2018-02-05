package domain;

@Deprecated
enum Rank {
    ACE(1),
	TWO(2),
	THREE(3),
	FOUR(4),
	FIVE(5),
	SIX(6),
	SEVEN(7),
	EIGHT(8),
	NINE(9),
	TEN(10),
	JACK(11),
	QUEEN(12),
	KING(13);

    final int rank;

    Rank(int rank) {
	this.rank = rank;
    }

    public int getRank() {
	return rank;
    }
//
//    public Rank next() {
//	switch (this) {
//	case ACE:
//	    return Rank.TWO;
//	case TWO:
//	    return Rank.THREE;
//	case THREE:
//	    return Rank.FOUR;
//	case FOUR:
//	    return Rank.FIVE;
//	case FIVE:
//	    return Rank.SIX;
//	case SIX:
//	    return Rank.SEVEN;
//	case SEVEN:
//	    return EIGHT;
//	case EIGHT:
//	    return NINE;
//	case NINE:
//	    return TEN;
//	case TEN:
//	    return JACK;
//	case JACK:
//	    return QUEEN;
//	case QUEEN:
//	    return KING;
//	default:  /** MUST BE KING. */
//	    return ACE;
//	}
//    }
//
//    public Rank previous() {
//	switch (this) {
//	case TWO:
//	    return Rank.ACE;
//	case THREE:
//	    return Rank.TWO;
//	case FOUR:
//	    return Rank.THREE;
//	case FIVE:
//	    return Rank.FOUR;
//	case SIX:
//	    return Rank.FIVE;
//	case SEVEN:
//	    return Rank.SIX;
//	case EIGHT:
//	    return Rank.SEVEN;
//	case NINE:
//	    return EIGHT;
//	case TEN:
//	    return NINE;
//	case JACK:
//	    return TEN;
//	case QUEEN:
//	    return JACK;
//	case KING:
//	    return QUEEN;
//	default: /** MUST BE ACE **/
//	    return KING;
//	}
//    }
}
