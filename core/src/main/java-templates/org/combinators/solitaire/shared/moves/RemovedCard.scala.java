@(RootPackage: Name)
package @{Java(RootPackage)}.model;

import ks.common.model.Card;

public class RemovedCard {
	Card card;
	String source;

	public RemovedCard(Card card, String source) {
		this.card = card;
		this.source = source;
	}

	public Card getCard() {
		return card;
	}

	public String getSource() {
		return source;
	}
}
