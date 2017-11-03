package pysolfc.castle

import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.twirl.Python
import org.combinators.solitaire.shared.SolitaireDomain
import org.combinators.solitaire.shared.compilation.CodeGeneratorRegistry
import org.combinators.solitaire.shared.python.{PythonSemanticTypes, constraintCodeGenerators}
import pysolfc.shared.GameTemplate

// domain
import domain._

import scala.collection.JavaConverters._

/**
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class CastleDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with PythonSemanticTypes {

  /**
    * Convert ID into string. Each different variation adds a unique ID to the pygames grouping
   */
  @combinator object castleID extends IdForGame(pygames.castle);

  @combinator object OutputFile {
    def apply: String = "castle"
    val semanticType:Type = game(pysol.fileName)
  }

  /**
    * NO special constraints just yet
    */
  @combinator object DefaultGenerator {
    def apply: CodeGeneratorRegistry[Python] = constraintCodeGenerators.generators
    val semanticType: Type = constraints(constraints.generator)
  }

  /**
    * Knows that suits are identified by suit=i for 0..3
    *
    * @param c
    * @return
    */
  def layout_place_foundation(c: Container): Python = {

    var combined = ""
    for (r <- c.placements().asScala) {
      combined = combined +
        s"""
         |s.foundations.append(self.Foundation_Class(${r.x}, ${r.y}, self, suit=${r.idx}, max_move=0))
         |""".stripMargin
    }
    Python(combined)
  }

  // THESE ARE ROWS. How to show orientation
  def layout_place_tableau(c:Container): Python = {
    var combined = ""

    // tableau typically can be oriented vertically or horizontally
    /** Orientation. By default, vertical downwards. */
    val element:Element = c.iterator().next()

    val offsets = if (element.getVerticalOrientation()) {
      "stack.CARD_XOFFSET, stack.CARD_YOFFSET = 0, l.YOFFSET"
    } else {
      "stack.CARD_XOFFSET, stack.CARD_YOFFSET = l.XOFFSET, 0"
    }

    for (r <- c.placements().asScala) {
      combined = combined +
        s"""
         |stack = self.RowStack_Class(${r.x}, ${r.y}, self)
         |$offsets
         |s.rows.append(stack)""".stripMargin
    }
    Python(combined)
  }

  // trying to constrct a talong doesn't easily work.
  def layout_place_stock(c:Container): Python = {
    var combined = ""
    for (r <- c.placements().asScala) {
      combined = combined + s"""
                          |s.talon = self.Talon_Class(${r.x}, ${r.y}, self)
                          |""".stripMargin
    }
    Python(combined)
  }

  @combinator object InitView {
    def apply(): Python = {

      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      val found = solitaire.containers.get(SolitaireContainerTypes.Foundation)
      val stock = solitaire.containers.get(SolitaireContainerTypes.Stock)



      // start by constructing the DeckView
      // If deck is invisible, then place invisibly
      val dw:Python = if (stock.isInvisible) {
        Python(s"""|
                   |x, y = self.getInvisibleCoords()
                   |s.talon = InitialDealTalonStack(x, y, self)
                   |""".stripMargin)
      } else {
        layout_place_stock(stock)
      }

      // when placing a single element in Layout, use this API
      val fd:Python = layout_place_foundation(found)
      val cs:Python = layout_place_tableau(tableau)

      // Need way to simply concatenate Python blocks
      val comb = Python(dw.getCode.toString ++ cs.getCode.toString ++ fd.getCode.toString)
      comb
    }

    val

    semanticType: Type = game(game.view)
  }
}
