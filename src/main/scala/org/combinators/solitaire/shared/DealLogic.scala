package org.combinators.solitaire.shared

import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Type
import org.combinators.templating.twirl.Java
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, ExpressionCombinator, MapExpressionCombinator}
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.domain._


/** Everything having to do with dealing logic. */
trait DealLogic extends SemanticTypes with Base {
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    var updated = super.init(gamma, s)

    // Handle Dealing cards.
    updated = updated
      .addCombinator(new ProcessDeal(s))

    updated
  }

  /** Process deals in Java. */
  class ProcessDeal(s:Solitaire) {

    def apply(mapGenerators: CodeGeneratorRegistry[Expression],
              generators: CodeGeneratorRegistry[Expression]): Seq[Statement] = {
      var stmts = ""
      for (step <- s.deal) {
        step match {
          case FilterStep(constraint,limit) =>
            val app = new ExpressionCombinator(constraint)
            val filterexp = app.apply(generators)

            // filter removes cards, but up to a certain limit only
            stmts = stmts + s"""{
                 |java.util.ArrayList<Card> tmp = new java.util.ArrayList<Card>();
                 |Stack keep = new Stack();
                 |int _limit = $limit;
                 |while (!deck.empty()) {
                 |    Card card = deck.get();
                 |    if ($filterexp) {
                 |        if (_limit == 0) { keep.add(card); }
                 |        else {
                 |          _limit--;
                 |          tmp.add(card);
                 |        }
                 |    } else {
                 |        keep.add(card);
                 |    }
                 |}
                 |
                 |while (!keep.empty()) {
                 |  deck.add(keep.get());
                 |}
                 |for (Card c : tmp) { deck.add(c); }
                 |}""".stripMargin


          // Map step is like a DealStep, without the inner issue of going to a single target.
              // TODO: Make 'card' a parameter to the map expression
          case MapStep(target, payload, mapping) =>
            val flipWithSemi:String = if (payload.faceUp) { "" } else { "c.setFaceUp (false);" }
            val numCards = payload.numCards
            val app = new MapExpressionCombinator(mapping)
            val mapExpression = app.apply(mapGenerators)
            val name = target.name
            stmts = stmts + s"""for (int i = 0; i < $numCards; i++) {
                               |    Card card = deck.get();
                               |    int _idx = $mapExpression;
                               |    $flipWithSemi
                               |    ConstraintHelper.$name(this)[_idx].add(card);
                               |}""".stripMargin


          // frames=0 means do no animation during the deal.
          case DealStep(target, payload) =>
            val flipWithSemi:String = if (payload.faceUp) { "" } else { "c.setFaceUp (false);" }
            val numCards = payload.numCards
            target match {
              // just reach out to one in particular
              case ElementTarget(container,idx) =>
                val name = container.name

                stmts = stmts + s"""
                                   |for (int i = 0; i < $numCards; i++) {
                                   |     Card c = deck.get();
                                   |     $flipWithSemi
                                   |     ConstraintHelper.$name(this)[$idx].add(c);
                                   |}""".stripMargin

              case ContainerTarget(container) =>
                val name = container.name
                stmts = stmts + s"""
                                 |for (int i = 0; i < $numCards; i++) {
                                 |    for (Stack st : ConstraintHelper.$name(this)) {
                                 |         Card c = deck.get();
                                 |         $flipWithSemi
                                 |         st.add(c);
                                 |    }
                                 |}""".stripMargin
            }
        }
      }

      Java(stmts).statements()
    }

    val semanticType:Type = constraints(constraints.map) =>:
      constraints(constraints.generator) =>: game(game.deal)
  }
}
