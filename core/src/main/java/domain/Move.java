package domain;

/**
 * A Move represents a potential move in a solitaire game. It consists
 * of two separate concepts.  First there is the logical construct
 * defining a source Element, the target Element, and
 * constraints/properties on the card(s) to be allowed to move between
 * them.
 *  
 * Domain modeling captures the semantic meaning of the moves, but relies
 * on regular programming to turn the logic into actual statements. For 
 * example, you can record that "a column of cards can be moved if the
 * column is descending in rank and contains alternating colors" but you 
 * don't have to actually complete this logic in the domain model. In this
 * regard, the domain model is truly an analysis document.
 *
 * While we could use some object-oriented modeling tool/language that 
 * includes multiple inheritance, for simplicity we choose a 
 * single-inheritance style because we use java to represent the model. 
 * Certainly, one could use a more complicated domain model (i.e., EMF)
 * and that would be a reasonable alternative to pursue.
 *
 * Moves are associated dynamically with domain model elements, which 
 * allows each to vary independently as needed to model the domain.
 *
 * Moves can be associated with individual elements or with an entire
 * container, which is a sort of short-cut to specifying each of he
 * available moves.
 * 
 * Note a UnaryMove class is a simpler concept required for moves
 * initiated without need for a terminating action. This includes (for
 * example), flipping a card.
 * 
 * @author heineman
 */
public abstract class Move {


}
