package domain.ui;


/**
 * Class to manage the proper layout of invidual elements. There are several
 * different prototype base layouts:
 *
 *   Foundation / Tableau / Score / NumCardsLeft
 *   Foundation / Reserve / Tableau / Score / NumCardsLeft
 *
 * Each manages a "rectangle" or perhaps just an anchor point, from which
 * all other offsets are managed. I prefer the rectangle since then the 
 * offset can be easily computed from the card width; otherwise you have
 * to magically select an offset which would change based on the size of
 * the card icons.
 *
 */

import java.awt.Rectangle;
import java.util.*;

import domain.*;

public class Layout implements Iterable<String> {

	// default constructs
	public static final String Foundation    = "Foundation";
	public static final String Tableau       = "Tableau";
	public static final String Reserve       = "Reserve";
	public static final String WastePile     = "WastePile";
        public static final String Stock         = "Stock";

	public static final String Score         = "Score";
	public static final String NumCardsLeft  = "NumCardsLeft";

	public Hashtable<String,Rectangle> origins = 
			new Hashtable<String,Rectangle>();

	public void add (String name, int x, int y, int width, int height) {
		origins.put(name, new Rectangle (x, y, width, height));
	}

	public Rectangle get(String name) {
		return origins.get(name);
	}

	public Iterator<String> iterator() {
		return origins.keySet().iterator();
	}
	
	class ProcessIterator implements Iterator<Rectangle> {

		int x;
		int y;
		final int height;
		final int gap = 15; // HACK. Should be computed
		final int max;
		int idx = 0;
		
		public ProcessIterator(Container container, Rectangle rect, int height) {
			this.x = rect.x;
			this.y = rect.y;
			this.height = height;
			this.max = container.size();
		}

		@Override
		public boolean hasNext() {
			return idx < max;
		}

		@Override
		public Rectangle next() {
			Rectangle r = new Rectangle (x, y, 73, height);   // WIDTH is 73. HACK!
			x += 73 + gap;
			idx++;
			return r;
		}
		
	}
	
	/**
	 * Helper routine to return Iterator of rectangles given domain model container.
	 * 
	 * HACK: assumes width is constant whereas height can change
	 */
	public Iterator<Rectangle> placements (String key, Container container, int height) {
		Rectangle rect = this.get(key);
		return new ProcessIterator(container, rect, height);
	}
}
