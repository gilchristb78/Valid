package org.combinators.solitaire.shared

import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Type
import org.combinators.templating.twirl.Java
import domain.Solitaire
import domain.deal._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, ExpressionCombinator, MapExpressionCombinator}
import org.combinators.cls.types.syntax._

import scala.collection.JavaConverters._

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
      for (step <- s.getDeal.asScala) {
        step match {
          case f: FilterStep =>
            val app = new ExpressionCombinator(f.constraint)
            val filterexp = app.apply(generators)

            // filter removes cards, but up to a certain limit only
            stmts = stmts + s"""{
                 |java.util.ArrayList<Card> tmp = new java.util.ArrayList<Card>();
                 |Stack keep = new Stack();
                 |int _limit = ${f.limit};
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
          case m: MapStep =>
            println("Map step:" + m)
            val payload = m.payload
            val flipWithSemi:String = if (payload.faceUp) { "" } else { "c.setFaceUp (false);" }
            val numCards = payload.numCards
            val app = new MapExpressionCombinator(m.map)
            val mapExpression = app.apply(mapGenerators)
            val name = m.target.targetType.getName
            stmts = stmts + s"""for (int i = 0; i < $numCards; i++) {
                               |    Card card = deck.get();
                               |    int _idx = $mapExpression;
                               |    $flipWithSemi
                               |    ConstraintHelper.$name(this)[_idx].add(card);
                               |}""".stripMargin


          // frames=0 means do no animation during the deal.
          case d: DealStep =>
            println("Deal step:" + d)
            val payload = d.payload
            val flipWithSemi:String = if (payload.faceUp) { "" } else { "c.setFaceUp (false);" }
            val numCards = payload.numCards
            d.target match {
              case ct:ContainerTarget =>
                val name = ct.targetType.getName
                stmts = stmts + s"""
                                 |for (int i = 0; i < $numCards; i++) {
                                 |    for (Stack st : ConstraintHelper.$name(this)) {
                                 |         Card c = deck.get();
                                 |         $flipWithSemi
                                 |         st.add(c);
                                 |    }
                                 |}""".stripMargin


              // just reach out to one in particular
              case et:ElementTarget =>
                val idx = et.idx
                val name = et.targetType.getName

                stmts = stmts + s"""
                         |for (int i = 0; i < $numCards; i++) {
                         |     Card c = deck.get();
                         |     $flipWithSemi
                         |     ConstraintHelper.$name(this)[$idx].add(c);
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
