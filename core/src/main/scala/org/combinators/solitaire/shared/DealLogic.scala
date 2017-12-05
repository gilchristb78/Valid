package org.combinators.solitaire.shared

import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.twirl.Java
import domain.{Solitaire, SolitaireContainerTypes}
import domain.deal.{ContainerTarget, DealStep, ElementTarget, FilterStep}
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, ExpressionCombinator, constraintCodeGenerators}

import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._

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

    def apply(generators: CodeGeneratorRegistry[Expression]): Seq[Statement] = {
      var stmts = ""
      for (step <- s.getDeal.asScala) {
        step match {
          case f: FilterStep => {
            val app = new ExpressionCombinator(f.constraint)
            val filterexp = app.apply(constraintCodeGenerators.generators)

            // filter removes cards
            stmts = stmts +
              s"""
                 |java.util.ArrayList<Card> tmp = new java.util.ArrayList<Card>();
                 |java.util.ArrayList<Card> keep = new java.util.ArrayList<Card>();
                 |while (!deck.empty()) {
                 |    Card card = deck.get();
                 |    if ($filterexp) {
                 |        tmp.add(card);
                 |    } else {
                 |        keep.add(card);
                 |    }
                 |}
                 |for (Card c : keep) { deck.add(c); }
                 |for (Card c : tmp) { deck.add(c); }
                 |""".stripMargin

          }

          // frames=0 means do no animation during the deal.
          case d: DealStep => {
            println("Deal step:" + d)
            val payload = d.payload
            val flip:String = if (payload.faceUp) { "" } else { "c.setFaceUp (false);" }
            val numCards = payload.numCards
            d.target match {
              case ct:ContainerTarget => {
                ct.targetType match {
                  case SolitaireContainerTypes.Foundation => {
                    stmts = stmts +
                      s"""
                         |for (int i = 0; i < $numCards; i++) {
                         |    for (Stack st : ConstraintHelper.foundation(this)) {
                         |         Card c = deck.get();
                         |         $flip
                         |         st.add(c);
                         |    }
                         |}""".stripMargin
                  }
                  case SolitaireContainerTypes.Tableau => {
                    stmts = stmts +
                      s"""
                         |for (int i = 0; i < $numCards; i++) {
                         |    for (Stack st : ConstraintHelper.tableau(this)) {
                         |         Card c = deck.get();
                         |         $flip
                         |         st.add(c);
                         |    }
                         |}""".stripMargin
                  }
                  case SolitaireContainerTypes.Waste => {
                    stmts = stmts +
                      s"""
                         |for (int i = 0; i < $numCards; i++) {
                         |    for (Stack st : ConstraintHelper.wastePile(this)) {
                         |         Card c = deck.get();
                         |         $flip
                         |         st.add(c);
                         |    }
                         |}""".stripMargin
                  }
                }
              }
              // just reach out to one in particular
              case et:ElementTarget => {
                val idx = et.idx

                et.targetType match {
                  case SolitaireContainerTypes.Foundation => {
                    stmts = stmts +
                      s"""
                         |for (int i = 0; i < $numCards; i++) {
                         |     Card c = deck.get();
                         |     $flip
                         |     ConstraintHelper.foundation(this)[$idx].add(c);
                         |}""".stripMargin
                  }
                  case SolitaireContainerTypes.Tableau => {
                    stmts = stmts +
                      s"""
                         |for (int i = 0; i < $numCards; i++) {
                         |     Card c = deck.get();
                         |     $flip
                         |     ConstraintHelper.tableau(this)[$idx].add(c);
                         |}""".stripMargin
                  }
                  case SolitaireContainerTypes.Waste => {
                    stmts = stmts +
                      s"""
                         |for (int i = 0; i < $numCards; i++) {
                         |     Card c = deck.get();
                         |     $flip
                         |     ConstraintHelper.wastePile(this)[$idx].add(c);
                         |}""".stripMargin
                  }
                }
                // just a single element
              }
            }
          }
        }
      }

      Java(stmts).statements()
    }

    val semanticType:Type = constraints(constraints.generator) =>: game(game.deal)
  }
}
