package pysolfc.shared

import org.combinators.cls.types.Type
import org.combinators.templating.twirl.Python
import org.combinators.solitaire.shared.compilation.CodeGeneratorRegistry
import org.combinators.solitaire.shared.python.{ConstraintExpander, MapExpressionCombinator, PythonSemanticTypes, constraintCodeGenerators}
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.domain._


trait DealLogic extends PythonSemanticTypes {
  class ProcessDeal(s:Solitaire) {

    def apply(mapGenerators: CodeGeneratorRegistry[Python],
              generators: CodeGeneratorRegistry[Python]): Python = {
      val stmts:String = s.deal.map(step =>
        step match {
          case FilterStep(constraint, limit) =>
            val app = new ConstraintExpander(constraint, 'Intermediate)  // No symbol really needed. Could be anything
            val filterexp = app.apply(constraintCodeGenerators.generators)

            // all must start with blank line, so indentation is appropriate.
            s"""
                 |tmp = []
                 |limit = $limit
                 |for i in range(len(self.s.talon.cards)-1,-1,-1):
                 |    card = self.s.talon.cards[i]
                 |    if $filterexp and (limit > 0):
                 |        tmp.append(card)
                 |        limit = limit -1
                 |        del self.s.talon.cards[i]
                 |tmp.sort(reverse=True, key=lambda c: c.suit)    # properly sorts as C/S/D/H
                 |for cd in tmp:
                 |    self.s.talon.cards.append(cd)
                 |del tmp
                 |""".stripMargin

          case MapStep(target, payload, mapping) =>

            val flip:String = if (payload.faceUp) { "1" } else { "0" }
            val numCards = payload.numCards
            val app = new MapExpressionCombinator(mapping)
            val mapExpression = app.apply(mapGenerators)
            val name = target.name
              s"""
                 |for i in range($numCards):
                 |    card = self.s.talon.cards[-1]
                 |    self.s.talon.dealRow(rows=[self.s.reserves[card.rank]], flip=$flip, frames=0)
                 |""".stripMargin


          // frames=0 means do no animation during the deal.
          case DealStep(target, payload) =>
            val flip:String = if (payload.faceUp) { "1" } else { "0" }
            val numCards = payload.numCards

            target match {
              case ContainerTarget(ct) =>
                // clean up naming
                ct  match {
                  case Foundation =>

                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=self.s.foundations, flip=$flip, frames=0)
                         |""".stripMargin

                  case Tableau =>
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=self.s.rows, flip=$flip, frames=0)
                         |""".stripMargin

                  // use as [..waste..] since waste is a singular entity and dealRow needs list
                  case Waste =>
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=[self.s.waste], flip=$flip, frames=0)
                         |""".stripMargin

                  // these are found in 'self' not 'self.s'
                  case _ =>
                    val field = ct.name  // TODO: FIX DOUBLE CEHCK
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=self.$field, flip=$flip, frames=0)
                             """.stripMargin

                }


              // just reach out to one in particular
              case ElementTarget(containerType, idx) =>

                containerType match {
                  case Foundation =>
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=[self.s.foundations[$idx]], flip=$flip, frames=0)
                       """.stripMargin

                  case Tableau =>
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=[self.s.rows[$idx]], flip=$flip, frames=0)
                        """.stripMargin

                  case Waste =>
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=self.s.waste[$idx], flip=$flip, frames=0)
                        """.stripMargin

                }
            }
        }).mkString("\n")

      Python("def startGame(self):" + Python(stmts).indent.toString())
    }

    val semanticType:Type = constraints(constraints.map) =>:
      constraints(constraints.generator) =>: game(pysol.startGame)
  }

}
