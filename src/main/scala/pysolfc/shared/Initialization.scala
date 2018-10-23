package pysolfc.shared

import org.combinators.cls.types.Type
import org.combinators.solitaire.domain._
import org.combinators.templating.twirl.Python
import org.combinators.solitaire.shared.python.PythonSemanticTypes

trait Initialization extends PythonSemanticTypes{

//  def pysolClassGivenType(element:Element): String = {
//    element match {
//      case c:Column =>
//        "SequenceRowStack"
//
//      case p:Pile =>
//        "AbstractFoundationStack"
//
//      case bp:BuildablePile =>
//       "SequenceRowStack"
//
//      case _ =>
//        "ReserveStack"
//    }
//  }

  /**
   * There are distinct destinations that cannot change:
    *
    * s.talon      -- stock (even when invisible)
    * s.rows       -- tableau
    * s.foundation -- foundation
    * s.reserves   -- reserve
    * s.waste      -- waste
   */
  def layout_place(s:Solitaire, ct: ContainerType, elements:Seq[Element]): Python = {
    var combined = ""
    val elt = elements.head

    // tableau typically can be oriented vertically or horizontally
    /** Orientation. By default, vertical downwards. */
   // val element:Element = c.iterator().next()

    // handles vertical/horizontal orientation
    val objName:String = "_obj"
    val offsets = if (elt.viewOneAtATime) {
      s"$objName.CARD_XOFFSET, $objName.CARD_YOFFSET = 0, 0"
    } else {
      if (elt.verticalOrientation) {
        s"$objName.CARD_XOFFSET, $objName.CARD_YOFFSET = 0, l.YOFFSET"
      } else {
        s"$objName.CARD_XOFFSET, $objName.CARD_YOFFSET = l.XOFFSET, 0"
      }
    }

    for ((r,idx) <- s.layout.places(ct).zipWithIndex) {
      var suitCheck:String = ""
      var destination:String = ""

      // These are the confirmed concepts that PySolFC uses. Note Waste and Talon are singletons, while others are lists []
      var singleton = false
      ct match {
        case Foundation =>
          suitCheck = s", suit=$idx"
          destination = "s.foundations"

        case Tableau =>
          destination = "s.rows"

        case StockContainer =>
          destination = "s.talon"
          singleton = true

        case Waste =>
          destination = "s.waste"
          singleton = true

        case Reserve =>
          destination = "s.reserves"

          // any user-defined containers are relegated to the self.* fields
        case _ =>
          destination = s"self.${ct.name}"
      }

      combined = combined +
        s"""|$objName=My${elt.name}Stack(${r.x}, ${r.y}, self $suitCheck, max_move=0)
            |$offsets
            |""".stripMargin

      if (singleton) {
        combined = combined + s"""|
                                   |$destination = $objName
                                  |""".stripMargin
      } else {
        combined = combined + s"""|
                                   |$destination.append($objName)
                                  |""".stripMargin
      }
    }
    Python(combined)
  }

  // Note: Decks are synthesized with MyDeckStack
  def layout_place_stock(s:Solitaire, ct:ContainerType): Python = {
    var combined = ""
    for (r <- s.layout.places(ct)) {
      // was renamed from MyDeckStack to MyStockStack during Scala remodeling
      combined = combined + s"""
                               |s.talon = MyStockStack(${r.x}, ${r.y}, self)
                               |""".stripMargin
    }
    Python(combined)
  }


  /**
    * In PySolFC, there is a special situation when there is both a Stock and a WastePile, so this
    * must be addressed specially.
    *
    * @param sol  DomainModel for Solitaire
    */
  class ProcessView(sol:Solitaire) {
    def apply(): Python = {

      var stmts: Python = Python("")
      sol.structure.foreach { case (ct, elements) =>
          ct match {
            case StockContainer =>
              if (!sol.layout.isVisible(ct)) { // If invisible, can't be part of stock/waste pairing
                val dw: Python = Python(
                  s"""|
                      |x, y = self.getInvisibleCoords()
                      |s.talon = InitialDealTalonStack(x, y, self)
                      |""".stripMargin)
                stmts = Python(stmts.getCode.toString ++ dw.getCode.toString)
              } else {
                //stock = Some(s)
                val code:Python = layout_place_stock(sol, ct)
                stmts = Python(stmts.getCode.toString ++ code.getCode.toString)
              }

            case _ =>
              // everyone else gets a chance
              val code:Python = layout_place(sol, ct, sol.structure(ct))
              stmts = Python(stmts.getCode.toString ++ code.getCode.toString)
          }
      }

      stmts
    }

    val semanticType: Type = game(game.view)
  }
}
