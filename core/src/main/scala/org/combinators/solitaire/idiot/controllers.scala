package org.combinators.solitaire.idiot
import com.github.javaparser.ast.CompilationUnit

// name clash
import com.github.javaparser.ast.`type`.{Type => JType}

import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Constructor
import com.github.javaparser.ast.body.BodyDeclaration
import org.combinators.generic
import _root_.java.util.UUID
import domain._
import domain.constraints._
import domain.moves._
import domain.ui._
import scala.collection.mutable.ListBuffer

trait Controllers extends shared.Controller with shared.Moves with generic.JavaIdioms  {


  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Idiot Controller dynamic combinators.")

    // structural
    val ui = new UserInterface(s)

    val els_it = ui.controllers
    while (els_it.hasNext()) {
      val el = els_it.next()

      // Each of these controllers are expected in the game.
      if (el == "Deck") {
        updated = updated.    // HACK. Why special for Deck???
          addCombinator (new DeckController(Symbol(el)))
      } else if (el == "Column") {
        updated = updated.
          addCombinator (new WidgetController(Symbol(el), Symbol(el)))
      }
    }

    // not much to do, if no rules...
    if (s.getRules == null) {
      return updated
    }

    updated = createMoveClasses(updated, s)

    updated = createDragLogic(updated, s)

    updated = generateMoveLogic(updated, s)

    // Each move has a source and a target. The SOURCE is the locus
    // for the PRESS while the TARGET is the locus for the RELEASE.
    // These are handling the PRESS events... SHOULD BE ABLE TO
    // INFER THESE FROM THE AVAILABLE MOVES
    updated = updated
      .addCombinator (new SingleCardMoveHandler("Column", 'Column, 'Column))
      .addCombinator (new DealToTableauHandlerLocal())
      .addCombinator (new TryRemoveCardHandlerLocal('Column, 'Column))

    // Potential moves clarify structure (by type not instance). FIX ME
    // FIX ME FIX ME FIX ME
    updated = updated
      .addCombinator (new PotentialTypeConstructGen("Column", 'ColumnToColumn))

    // these identify the controller names. SHOULD INFER FROM DOMAIN MODEL. FIX ME
    updated = updated
      .addCombinator (new ControllerNaming('Column, 'Column, "Idiot"))

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

    val semanticType: Type = 'Deck ('Pressed) :&: 'NonEmptySeq
  }

  /**
    * HACK: Move this logic into class which is synthesized, rather than taking existing RSC class as is
    * from the template area.
    *
    * @param widgetType
    * @param source
    */
  class TryRemoveCardHandlerLocal(widgetType:Symbol, source:Symbol) {
    def apply():Seq[Statement] = {
      Java(s"""|Column srcColumn = (Column) src.getModelElement();
               |if (((org.combinators.solitaire.idiot.Idiot)theGame).isHigher(srcColumn)) {
               |Move m = new RemoveCard(srcColumn);
               |if (m.doMove(theGame)) {
               |   theGame.pushMove(m);
               |}
               |}
               |}""".stripMargin).statements()
    }

    val semanticType: Type = widgetType (source, 'Clicked) :&: 'NonEmptySeq
  }


  class PotentialDraggingVariableGeneratorLocal(m:Move, constructor:Constructor) {
    def apply(): SimpleName = {
      m match {
        case single: SingleCardMove => Java(s"""movingCard""").simpleName()
        case column: ColumnMove     => Java(s"""movingColumn""").simpleName()
      }
    }
    val semanticType: Type = constructor
  }

  // Note: while I can have code within the apply() method, the semanticType
  // is static, so that must be passed in as is. These clarify that a
  // potential moveOneCardFromStack is still a Column Type.
  class PotentialTypeConstructGenLocal(s:String, constructor:Constructor) {
    def apply(): JType = Java("Column").tpe()
    val semanticType: Type = 'Move (constructor, 'TypeConstruct)
  }
}


