package org.combinators.solitaire.fan

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import org.combinators.solitaire.domain._
import org.combinators.cls.interpreter.{ReflectedRepository, combinator}
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.generic
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._
import org.combinators.templating.twirl.Java
import org.combinators.solitaire.fanfreepile.fanfreepile

/** Defines Fan's controllers and their behaviors.
  *
  * Every controller requires definitions for three actions:
  *   - Click (no dragging, like clicking on a deck to deal more cards)
  *   - Press (click, drag, release)
  *   - Release (release after press)
  *
  * Either a rule must be associated with an action, or the action must be
  * explicity ignored. See FanRules in game.scala.
  */
trait controllers extends shared.Controller  with GameTemplate with shared.Moves with generic.JavaCodeIdioms  {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
  ReflectedRepository[G] = {
    var updated = super.init(gamma, s)
    println (">>> Fan Controller dynamic combinators.")

    updated = createMoveClasses(updated, s)
    updated = createDragLogic(updated, s)
    updated = generateMoveLogic(updated, s)
    updated = generateExtendedClasses(updated, s)


    updated = updated
      .addCombinator (new IgnoreClickedHandler(column))
      .addCombinator (new SingleCardMoveHandler(column))
    updated = updated
      .addCombinator (new IgnoreClickedHandler('AlexColumn))
      .addCombinator (new SingleCardMoveHandler('AlexColumn))


    updated = updated
      .addCombinator (new IgnorePressedHandler(pile))
      .addCombinator (new IgnoreClickedHandler(pile))

    s match {
      case fanfreepile => {
        updated = updated.addCombinator(new IgnoreClickedHandler('FreePile))
        updated = updated.addCombinator(new SingleCardMoveHandler('FreePile))
      }
    }


    updated = updated
      .addCombinator (new IgnoreClickedHandler(deck))
      .addCombinator (new IgnoreReleasedHandler(deck))

    updated = updated
      .addCombinator(new DealToTableauHandlerLocal())

    updated = createWinLogic(updated, s)

    // move these to shared area
    updated = updated
      .addCombinator (new DefineRootPackage(s))
      .addCombinator (new DefineNameOfTheGame(s))
      .addCombinator (new ProcessModel(s))
      .addCombinator (new ProcessView(s))
      .addCombinator (new ProcessControl(s))
      .addCombinator (new ProcessFields(s))



    updated
  }

    /**
      * When dealing card(s) from the stock to all elements in Tableau
      * If deck is empty, then reset.
      * NOTE: How to make this more compositional?
      */
    class DealToTableauHandlerLocal() {
      def apply(): (SimpleName, SimpleName) => Seq[Statement] = (widget, ignore) => {
        Java(s"""|{Move m = new DealDeck(theGame.deck, theGame.tableau);
                 |if (m.doMove(theGame)) {
                 |   theGame.pushMove(m);
                 |   // have solitaire game refresh widgets that were affected
                 |   theGame.refreshWidgets();
                 |   return;
                 |}}""".stripMargin
        ).statements()
      }

      val semanticType: Type = drag(drag.variable, drag.ignore) =>: controller(deck, controller.pressed)
    }

  /** New Sinan combinator. */

  /** Each Solitaire variation must provide default conversion for moves. */
  @combinator
  object Earth {
    def apply:Expression = {
      Java(s""""Earth"""").expression()
    }
    val semanticType: Type = 'Planet
  }

  @combinator
  object Mars {
    def apply:Expression = {
      Java(s""""Mars"""").expression()
    }
    val semanticType: Type = 'Planet
  }

  @combinator
  object NewSinanCombinator {
    def apply(expr:Expression) :CompilationUnit = {
      Java(
        s"""
           |package something;
           |
           |public class NewClass {
           |  public static void main (String[] args) {
           |    System.out.println($expr);
           |  }
           |}
           |""".stripMargin).compilationUnit()
    }

    val semanticType: Type = 'Planet =>: sinan(sinan.begin)
  }

  @combinator object TemplateLoad {
    def apply(expr:Expression) :CompilationUnit = {

      shared.java.SampleTemplate.render(expr).compilationUnit()
    }
    val semanticType: Type = 'Planet =>: sinan(sinan.begin)
  }
}


