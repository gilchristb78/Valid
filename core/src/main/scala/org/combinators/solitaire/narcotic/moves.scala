package org.combinators.solitaire.narcotic

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._
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

trait Moves extends shared.Controller with shared.Moves with generic.JavaIdioms {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
      ReflectedRepository[G] = {
      var updated = super.init(gamma, s)
      println (">>> Narcotic Controller dynamic combinators.")

     // structural
     val ui = new UserInterface(s)

     val els_it = ui.controllers
     while (els_it.hasNext()) {
       val el = els_it.next()

       // Each of these controllers are expected in the game.
       if (el == "Deck") {
         updated = updated.    // HACK. Why special for Deck???
             addCombinator (new WidgetControllerJustPress(Symbol(el), Symbol(el)))
       } else if (el == "Pile") {
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
       .addCombinator (new SingleCardMoveHandler('Pile, 'Pile))
       .addCombinator (new DealToTableauHandler())
//       .addCombinator (new TryRemoveCardHandler('Pile, 'Pile))

   // Potential moves clarify structure (by type not instance). FIX ME
   // FIX ME FIX ME FIX ME
   updated = updated
       .addCombinator (new PotentialTypeConstructGen("Pile", 'PileToPile))

   // these identify the controller names. SHOULD INFER FROM DOMAIN MODEL. FIX ME
   updated = updated
       .addCombinator (new ControllerNaming('Pile, 'Pile, "Narcotic"))

  // CASE STUDY: Add Automove logic at end of release handlers

   updated
  }

/**
 * When dealing card(s) from the stock to all elements in Tableau
 * SHOULD BE GENERIC
 */
class DealToTableauHandler() {
 def apply():Seq[Statement] = {
        Java(s"""|m = new DeckToColumn(theGame.deck, theGame.piles);
                 |if (m.doMove(theGame)) {
                 |   theGame.pushMove(m);
                 |}""".stripMargin).statements()
  }

  val semanticType: Type = 'Deck ('Pressed) :&: 'NonEmptySeq
}


/***************
  @combinator object RemoveCard {
    def apply(rootPackage: Name, numPiles: Expression): CompilationUnit = {
      moves.java.MoveRemoveCard.render(rootPackage, numPiles).compilationUnit()
    }
    val semanticType: Type = 'RootPackage =>: 'NumPiles =>: 'MoveRemoveCards
  }

  @combinator object PileMove {
    def apply(rootPackage: Name, pileCondition: Seq[Statement]): CompilationUnit = {
      moves.java.PileMove.render(rootPackage, pileCondition).compilationUnit()
    }
    val semanticType: Type = 'RootPackage =>: 'PileToPileCondition =>: 'PileMove
  }

  @combinator object PileToPileCondition {
    def apply(): Seq[Statement] = {
      moves.java.PileToPileCondition.render().statements()
    }
    val semanticType: Type = 'PileToPileCondition
  }

  @combinator object DeckPressedHandler {
    def apply(rootPackage: Name, nameOfTheGame: SimpleName): Seq[Statement] = {
      moves.java.DeckPressed.render(rootPackage, nameOfTheGame).statements()
    }
    val semanticType: Type = 'RootPackage =>: 'NameOfTheGame =>: 'Deck ('Pressed)
  }

  // HACK: Needs Fixing to new system.
  @combinator object NarcoticPileController extends WidgetController('NarcoticPile, 'NarcoticPile)

  @combinator object NarcoticPile {
    def apply(): SimpleName = Java("Narcotic").simpleName()
    val semanticType: Type = 'Pile ('NarcoticPile, 'ClassName)
  }

  @combinator object PilePressedHandler {
    def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        moves.java.PilePressed.render(widgetVariableName, ignoreWidgetVariableName).statements()
    }
    val semanticType: Type =
      'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
        'Pile ('NarcoticPile, 'Pressed) :&: 'NonEmptySeq
  }

  @combinator object PiledClickedHandler {
    def apply(rootPackage: Name, nameOfTheGame: SimpleName): Seq[Statement] = {
      moves.java.PileClicked.render(rootPackage, nameOfTheGame).statements()
    }
    val semanticType: Type =
      'RootPackage =>: 'NameOfTheGame =>: 'Pile ('NarcoticPile, 'Clicked) :&: 'NonEmptySeq
  }

  @combinator object PileReleasedHandler {
    def apply(rootPackage: Name): Seq[Statement] = {
      moves.java.PileReleased.render(rootPackage).statements()
    }
    val semanticType: Type =
      'RootPackage =>: 'Pile ('NarcoticPile, 'Released) :&: 'NonEmptySeq
  }

***************************/
}
