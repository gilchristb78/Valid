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

    /**
     * Sometimes (Archway), the developer wants to give a custom list of coordinates.
	 * Placement is set in the ProcessIterators below.
	 * @author jabortell
     */
    private enum Placement {
        STATIC,
        CUSTOM,
    }

	/**
	 * Add a Rectangle to the layout.
	 */
	public void add (String name, int x, int y, int width, int height) {
		origins.put(name, new Rectangle (x, y, width, height));
	}

	/**
	 * Returns Rectangular region of a Layout, indexed by name.
	 * @param name
	 * @return
	 */
	public Rectangle get(String name) {
		return origins.get(name);
	}

	public Iterator<String> iterator() {
		return origins.keySet().iterator();
	}

	class ProcessIterator implements Iterator<Widget> {

		int x;
		int y;

		Placement placement;

		Rectangle[] custom_coordinates;

		final int height;
		final int gap = 15; // HACK. Should be computed
		final int max;
		int idx = 0;

		public ProcessIterator(Container container, Rectangle rect, int height) {
		    this.placement = Placement.STATIC;
			this.x = rect.x;
			this.y = rect.y;
			this.height = height;
			this.max = container.size();
		}

		/**
		 * Called from `customPlacements(...)`, this constructor is used when
		 * providing an array of custom coordinates.
		 * Placement is set to CUSTOM, which is checked in `next()`.
		 * @param container Such as the Reserve or Foundation.
		 * @param rects Custom collection of coordinates.
		 * @param height Usually specifies either the card or column height.
		 *
		 * @author jabortell
		 */
		ProcessIterator(Container container, Rectangle[] rects, int height) {
		    this.placement = Placement.CUSTOM;
		    this.custom_coordinates = rects;
		    this.height = height;
		    this.max = container.size();
        }

		@Override
		public boolean hasNext() {
			return idx < max;
		}

		@Override
		public Widget next() {
            Widget r;
            // I chose to use an Enum in a State pattern, rather than merely
            // checking if custom_coordinates is set, because I do not
            // like checking if objects are null.
		    if (this.placement == Placement.CUSTOM) {
		        r = new Widget(idx, custom_coordinates[idx].x, custom_coordinates[idx].y, 73, height);
            }
            else {
                r = new Widget (idx, x, y, 73, height);   // WIDTH is 73. HACK!
                x += 73 + gap;
            }
			idx++;
			return r;
		}
	}

	/**
	 * Helper routine to return Iterator of rectangles given domain model container.
	 *
	 * HACK: assumes width is constant whereas height can change
	 */
	public Iterator<Widget> placements (String key, Container container, int height) {
		Rectangle rect = this.get(key);
		return new ProcessIterator(container, rect, height);
	}

	/**
	 * Provide custom placements for a container.
	 * First, create list of rectangles from x and y coordinates, then call ProcessIterator constructor.
	 * @param container Such as the Reserve or Tableau.
	 * @param x Array of x coordinates.
	 * @param y Array of y coordinates.
	 * @param height Usually specifies either the card or column height.
	 * @return ProcessIterator to provide layout parameters.
	 *
	 * @author jabortell
	 */
	public Iterator<Widget> customPlacements(Container container, int[] x, int[] y, int height) {
		// Double-check sizes of container, x, and y coordinates.
        if (container.size() != x.length && x.length != y.length) {
            throw new IllegalArgumentException("Can't add " + container + " : Container and coordinates have unequal lengths.");
        }

        // Make rectangles with x and y coordinates.
        Rectangle[] rects = new Rectangle[container.size()];
        for (int i = 0; i < x.length; i++) {
            rects[i] = new Rectangle(x[i], y[i], 73, height);
        }

	    return new ProcessIterator(container, rects, height);
    }


}
