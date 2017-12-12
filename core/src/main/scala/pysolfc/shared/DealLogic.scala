package pysolfc.shared

import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.twirl.Python
import domain.{Solitaire, SolitaireContainerTypes}
import domain.deal._
import org.combinators.solitaire.shared.compilation.CodeGeneratorRegistry
import org.combinators.solitaire.shared.python.{ConstraintExpander, MapExpressionCombinator, PythonSemanticTypes, constraintCodeGenerators}
import de.tu_dortmund.cs.ls14.cls.types.syntax._

import scala.collection.JavaConverters._

trait DealLogic extends PythonSemanticTypes {
  class ProcessDeal(s:Solitaire) {

    def apply(mapGenerators: CodeGeneratorRegistry[Python],
              generators: CodeGeneratorRegistry[Python]): Python = {
      var stmts = ""
      for (step <- s.getDeal.asScala) {
        step match {
          case f: FilterStep =>
            val limit = f.limit
            val app = new ConstraintExpander(f.constraint, 'Intermediate)  // No symbol really needed. Could be anything
            val filterexp = app.apply(constraintCodeGenerators.generators)

            // all must start with blank line, so indentation is appropriate.
            stmts = stmts + s"""
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

          case m: MapStep =>
            println("Map step:" + m)
            val payload = m.payload

            val flip:String = if (payload.faceUp) { "1" } else { "0" }
            val numCards = payload.numCards
            val app = new MapExpressionCombinator(m.map)
            val mapExpression = app.apply(mapGenerators)
            val name = m.target.targetType.getName
            stmts = stmts + s"""
                               |for i in range($numCards):
                               |    card = self.s.talon.cards[-1]
                               |    self.s.talon.dealRow(rows=[self.s.reserves[card.rank]], flip=$flip, frames=0)
                               |""".stripMargin


          // frames=0 means do no animation during the deal.
          case d: DealStep =>
            println("Deal step:" + d)
            val payload = d.payload
            val flip:String = if (payload.faceUp) { "1" } else { "0" }
            val numCards = payload.numCards
            d.target match {
              case ct:ContainerTarget =>
                // clean up naming
                ct.targetType match {
                  case SolitaireContainerTypes.Foundation =>
                    stmts = stmts +
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=self.s.foundations, flip=$flip, frames=0)
                         |""".stripMargin

                  case SolitaireContainerTypes.Tableau =>
                    stmts = stmts +
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=self.s.rows, flip=$flip, frames=0)
                         |""".stripMargin

                  // use as [..waste..] since waste is a singular entity and dealRow needs list
                  case SolitaireContainerTypes.Waste =>
                    stmts = stmts +
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=[self.s.waste], flip=$flip, frames=0)
                         |""".stripMargin

                  // these are found in 'self' not 'self.s'
                  case _ =>
                    val field = ct.targetType.getName
                    stmts = stmts +
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=self.$field, flip=$flip, frames=0)
                             """.stripMargin

                }


              // just reach out to one in particular
              case et:ElementTarget =>
                val idx = et.idx

                et.targetType match {
                  case SolitaireContainerTypes.Foundation =>
                    stmts = stmts +
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=[self.s.foundations[$idx]], flip=$flip, frames=0)
                       """.stripMargin

                  case SolitaireContainerTypes.Tableau =>
                    stmts = stmts +
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=[self.s.rows[$idx]], flip=$flip, frames=0)
                        """.stripMargin

                  case SolitaireContainerTypes.Waste =>
                    stmts = stmts +
                      s"""
                         |for _ in range($numCards):
                         |    self.s.talon.dealRow(rows=[self.s.waste[$idx]], flip=$flip, frames=0)
                        """.stripMargin

                }
              // just a single element

            }

        }
      }

      stmts = "def startGame(self):" + Python(stmts).indent.toString()

      Python(stmts)
    }

    val semanticType:Type = constraints(constraints.map) =>:
      constraints(constraints.generator) =>: game(pysol.startGame)
  }

}
