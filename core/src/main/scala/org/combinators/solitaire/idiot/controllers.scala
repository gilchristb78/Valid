package org.combinators.solitaire.idiot

import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import org.combinators.generic
import domain._
import domain.ui._

trait controllers extends shared.Controller with shared.Moves with generic.JavaIdioms  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Idiot Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)

    updated = createDragLogic(updated, s)

    updated = generateMoveLogic(updated, s)

    // Each move has a source and a target. The SOURCE is the locus
    // for the PRESS while the TARGET is the locus for the RELEASE.
    // These are handling the PRESS events... SHOULD BE ABLE TO
    // INFER THESE FROM THE AVAILABLE MOVES
    updated = updated
      .addCombinator (new SingleCardMoveHandler('Column))
      .addCombinator (new DealToTableauHandlerLocal())
      .addCombinator (new TryRemoveCardHandlerLocal('Column))

    // Potential moves clarify structure (by type not instance). FIX ME
    // FIX ME FIX ME FIX ME
    updated = updated
      .addCombinator (new PotentialMultipleCardMove("Column", 'ColumnToColumn))

    // these identify the controller names. SHOULD INFER FROM DOMAIN MODEL. FIX ME
//    updated = updated
//      .addCombinator (new ControllerNaming('Column, 'Column, "Idiot"))

    // CASE STUDY: Add Automove logic at end of release handlers

    updated
  }

  /**
    * When dealing card(s) from the stock to all elements in Tableau
    */
  class DealToTableauHandlerLocal() {
    def apply():Seq[Statement] = {
      Java(s"""|m = new DealDeck(theGame.deck, theGame.fieldColumns);
               |if (m.doMove(theGame)) {
               |   theGame.pushMove(m);
               |}""".stripMargin).statements()
    }

    val semanticType: Type = drag(drag.variable, drag.ignore) =>: controller(deck, controller.pressed)

  }

  /**
    * HACK: Move this logic into class which is synthesized, rather than taking existing RSC class as is
    * from the template area.
    *
    * @param source
    */
  class TryRemoveCardHandlerLocal(source:Symbol) {
    def apply():Seq[Statement] = {
      Java(s"""|Column srcColumn = (Column) src.getModelElement();
               |Move m = new RemoveCard(srcColumn);
               |if (m.doMove(theGame)) {
               |   theGame.pushMove(m);
               |}""".stripMargin).statements()
    }

    val semanticType: Type = controller (source, controller.clicked)
  }
}


