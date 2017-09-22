@(RootPackage: Name)
package @{Java(RootPackage)}.model;
import ks.common.model.*;
import ks.common.games.Solitaire;

/**
 * Remove a single card.
 */
public class RemoveSingleCard extends ks.common.model.Move {

  /** Container of cards. */
  protected Stack stack;
  protected Card card;

  /**
   * Remove single card from stack.
   */
  public RemoveSingleCard(Stack s) {
    super();

    this.stack = s;
  }

  /**
   * To undo this move, simply put card back.
   */
  public boolean undo(ks.common.games.Solitaire game) {

    // move back
    stack.add(card);
    return true;
  }

  /**
   * Execute the move
   * @@see ks.common.model.Move#doMove(ks.games.Solitaire)
   */
  public boolean doMove(Solitaire game) {
    if (!valid (game)) {
      return false;
    }

    // EXECUTE
    card = stack.get();
    return true;
  }

  /**
   * Validate the move.
   * @@see ks.common.model.Move#valid(ks.games.Solitaire)
   */
  public boolean valid(Solitaire game) {
    return !stack.empty();
  }
}
