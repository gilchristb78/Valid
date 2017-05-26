package domain;

import java.util.*;

/**

  This class models the top-level domain of the solitaire application
  space.

  Here is where you would find accurate descriptions of each variation,
  and it might be instructive to grab a handbook of solitaire variations
  and then convert the description "word for word" into different
  instances of the domain model.

  To convert a domain Model into a specific target, there needs to be 
  a separate entity that traverses the structure, perhaps as a Visitor,
  but perhaps not, and returns the appropriate entity.

  If a visitor, then each and every sub-element in the domain must either
  implement an interface or extend a class. Annoying but at least offers
  a standardized means of processing the domain model.

  Perhaps use standard Eclipse EMF notation? don't invent the wheel!

  The classes within the domain reflect a deep understanding of the
  domain. It may not be necessary to name the classes according to
  any existing name of classes.

*/

public class Solitaire implements Iterable<Container> {
	
    // Hack. Stash the constructed Solitaire object here 
    // will remove eventually
    //static Solitaire _inst = null;
    //public static void setInstance (Solitaire s) { _inst = s; System.out.println (_inst); }
    //public static Solitaire getInstance() { return _inst; }

    // number of decks
    //    int  numDecks = 1;   // default to 1
    //    public void setNumberDecks(int nd) { numDecks = nd; }
    //    public int  getNumberDecks() { return numDecks; }


    /** Hack. */
    public Iterator<Container> iterator() { 
	ArrayList<Container> ar = new ArrayList<Container>();
	if (tableau != null) { ar.add(tableau); }
	if (foundation != null) { ar.add(foundation); }
	if (reserve != null) { ar.add(reserve); }
	if (stock != null) { ar.add(stock); }
	if (waste != null) { ar.add(waste); }
	return ar.iterator();
    }

    Tableau       tableau;
    public Tableau getTableau () { return tableau; }
    public void setTableau(Tableau t) { tableau = t; }

    Foundation    foundation;
    public Foundation getFoundation() { return foundation; }
    public void setFoundation(Foundation f) { foundation = f; }
    
    Reserve       reserve;
    public Reserve getReserve() { return reserve; }
    public void setReserve(Reserve r) { reserve = r; }
    
    Stock         stock;
    public Stock  getStock() { return stock; }
    public void setStock(Stock s) { stock = s; }
    
    Rules         rules;
    public Rules  getRules() { return rules; }
    public void   setRules(Rules r) { rules = r; }

    // Chosen by player
    Layout        layout;
    public Layout getLayout() { return layout; }
    public void   setLayout(Layout lay) { layout = lay; }

    Waste         waste;
    public Waste  getWaste() { return waste; }
    public void   setWaste(Waste w) { waste = w; }

    // any variation-specific game state is placed here.
    // 
    State         state;

}
