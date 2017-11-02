package pysolfc

import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.twirl.Python
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.python.PythonSemanticTypes

// domain
import domain._
import scala.collection.JavaConverters._

/**
  * Define domain using Score52 since this is a single-deck solitaire game.
  * @param solitaire    Application domain object with details about solitaire variation.
  */
class CastleDomain(override val solitaire:Solitaire) extends SolitaireDomain(solitaire) with PythonSemanticTypes {

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

  def layout_place_tableau(c:Container): Python = {
    var combined = ""
    for (r <- c.placements().asScala) {
      combined = combined +
        s"""
         |stack = self.RowStack_Class(${r.x}, ${r.y}, self)
         |stack.CARD_XOFFSET, stack.CARD_YOFFSET = l.XOFFSET, 0
         |s.rows.append(stack)""".stripMargin
    }
    Python(combined)
  }

  @combinator object InitView {
    def apply(): Python = {

      val tableau = solitaire.containers.get(SolitaireContainerTypes.Tableau)
      val found = solitaire.containers.get(SolitaireContainerTypes.Foundation)


      // start by constructing the DeckView

      // when placing a single element in Layout, use this API
      val fd = layout_place_foundation(found)
      val cs = layout_place_tableau(tableau)

      // Need way to simply concatenate Python blocks
      val comb = Python(cs.getCode.toString ++ fd.getCode.toString)
      comb
    }

    val

    semanticType: Type = game(game.view)
  }
}
