package pysolfc.shared

import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.twirl.Python
import domain._
import org.combinators.solitaire.shared.python.PythonSemanticTypes

import scala.collection.JavaConverters._

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
  def layout_place(s:Solitaire, containerName:String, c: Container, e:Element): Python = {
    var combined = ""
    val name = e.getClass.getSimpleName

    // tableau typically can be oriented vertically or horizontally
    /** Orientation. By default, vertical downwards. */
    val element:Element = c.iterator().next()

    // handles vertical/horizontal orientation
    val objName:String = "_obj"
    val offsets = if (element.viewOneAtATime()) {
      s"$objName.CARD_XOFFSET, $objName.CARD_YOFFSET = 0, 0"
    } else {
      if (element.getVerticalOrientation) {
        s"$objName.CARD_XOFFSET, $objName.CARD_YOFFSET = 0, l.YOFFSET"
      } else {
        s"$objName.CARD_XOFFSET, $objName.CARD_YOFFSET = l.XOFFSET, 0"
      }
    }

    for (r <- s.placements(c).asScala) {
      var suitCheck:String = ""
      var destination:String = ""

      // These are the confirmed concepts that PySolFC uses. Note Waste and Talon are singletons, while others are lists []
      var singleton = false
      c match {
        case _:Foundation =>
          suitCheck = s", suit=${r.idx}"
          destination = "s.foundations"

        case _:Tableau =>
          destination = "s.rows"

        case _:Stock =>
          destination = "s.talon"
          singleton = true

        case _:Waste =>
          destination = "s.waste"
          singleton = true

        case _:Reserve =>
          destination = "s.reserves"

          // any user-defined containers are relegated to the self.* fields
        case _ =>
          destination = s"self.$containerName"
      }

      combined = combined +
        s"""|$objName=My${name}Stack(${r.x}, ${r.y}, self $suitCheck, max_move=0)
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

//  /**
//    * Knows that suits are identified by suit=i for 0..3
//    */
//  def layout_place_foundation(s:Solitaire, c: Container): Python = {
//
//    var combined = ""
//    //for (r <- c.placements().asScala) {
//    for (r <- s.placements(c).asScala) {
//      combined = combined +
//        s"""
//           |s.foundations.append(self.Foundation_Class(${r.x}, ${r.y}, self, suit=${r.idx}, max_move=0))
//           |""".stripMargin
//    }
//    Python(combined)
//  }
//
//  def layout_place_pile(s:Solitaire, c: Container): Python = {
//
//    var combined = ""
//    for (r <- s.placements(c).asScala) {
//      combined = combined +
//        s"""
//           |s.foundations.append(ReserveStack(${r.x}, ${r.y}, self, max_move=0))
//           |""".stripMargin
//    }
//    Python(combined)
//  }


//  // THESE ARE ROWS. How to show orientation
//  def layout_place_column(s:Solitaire, c:Container): Python = {
//    var combined = ""
//
//    // tableau typically can be oriented vertically or horizontally
//    /** Orientation. By default, vertical downwards. */
//    val element:Element = c.iterator().next()
//
//    val offsets = if (element.getVerticalOrientation) {
//      "stack.CARD_XOFFSET, stack.CARD_YOFFSET = 0, l.YOFFSET"
//    } else {
//      "stack.CARD_XOFFSET, stack.CARD_YOFFSET = l.XOFFSET, 0"
//    }
//
//    //for (r <- c.placements().asScala) {
//    for (r <- s.placements(c).asScala) {
//      combined = combined +
//        s"""
//           |stack = self.RowStack_Class(${r.x}, ${r.y}, self)
//           |$offsets
//           |s.rows.append(stack)""".stripMargin
//    }
//    Python(combined)
//  }


  // Note: Decks are synthesized with MyDeckStack
  def layout_place_stock(s:Solitaire, c:Container): Python = {
    var combined = ""
    for (r <- s.placements(c).asScala) {
      combined = combined + s"""
                               |s.talon = MyDeckStack(${r.x}, ${r.y}, self)
                               |""".stripMargin
    }
    Python(combined)
  }

//  /** Waste takes its structure from existing classWasteStack. Need to deal with rounds/num deal at a time. */
//  def layout_place_stock_and_waste(s:Solitaire, stock:Container, waste:Container): Python = {
//    var combined = ""
//
//    for (r <- s.placements(stock).asScala) {
//      combined = combined +
//        s"""
//           |s.talon =  WasteTalonStack(${r.x}, ${r.y}, self, max_rounds=1, num_deal=1)
//                  """.stripMargin
//    }
//
//    for (r <- s.placements(waste).asScala) {
//      combined = combined +
//        s"""
//           |s.waste =  WasteStack(${r.x}, ${r.y}, self)
//            """.stripMargin
//    }
//    Python(combined)
//  }

//  /** Some games use a stock only to store cards which are all dealt out. */
//  def layout_invisible_stock(stock:Container): Python = {
//    Python(s"""
//              |x, y = self.getInvisibleCoords()
//              |s.talon = TalonStack(x, y, self)
//              |""".stripMargin)
//  }  /** Some games use a stock only to store cards which are all dealt out. */
  //  def layout_invisible_stock(stock:Container): Python = {
  //    Python(s"""
  //              |x, y = self.getInvisibleCoords()
  //              |s.talon = TalonStack(x, y, self)
  //              |""".stripMargin)
  //  }

  /**
    * In PySolFC, there is a special situation when there is both a Stock and a WastePile, so this
    * must be addressed specially.
    *
    * @param sol  DomainModel for Solitaire
    */
  class ProcessView(sol:Solitaire) {
    def apply(): Python = {

      var stmts: Python = Python("")
//      var stock:Option[Stock] = None
//      var waste:Option[Waste] = None

      for (containerType: ContainerType <- sol.containers.keySet.asScala) {
        val container = sol.containers.get(containerType)
        val name = containerType.getName
        container match {

//          case f: Foundation =>
//            val fd: Python = layout_place_foundation(sol, f)
//            stmts = Python(stmts.getCode.toString ++ fd.getCode.toString)
//
//          case t: Tableau =>
//            val tb: Python = layout_place_tableau(sol, t)
//            stmts = Python(stmts.getCode.toString ++ tb.getCode.toString)
//
//          case r: Reserve =>
//            val rp: Python = layout_place_reserve(sol, r)
//            stmts = Python(stmts.getCode.toString ++ rp.getCode.toString)

            // If we see a Waste, there must be a Stock as well.
//          case w: Waste =>
//            waste = Some(w)

          case s: Stock =>
            if (!sol.isVisible(s)) { // If invisible, can't be part of stock/waste pairing
              val dw: Python = Python(
                s"""|
                    |x, y = self.getInvisibleCoords()
                    |s.talon = InitialDealTalonStack(x, y, self)
                    |""".stripMargin)
              stmts = Python(stmts.getCode.toString ++ dw.getCode.toString)
            } else {
              //stock = Some(s)
              val code:Python = layout_place_stock(sol, container)
              stmts = Python(stmts.getCode.toString ++ code.getCode.toString)
            }

          case _ =>
            // everyone else gets a chance
            val element:Element = container.iterator.next
            val code:Python = layout_place(sol, name, container, element)
            stmts = Python(stmts.getCode.toString ++ code.getCode.toString)
        }
      }

      // Create generic TalonStock and handle deals with DealRow_StackMethods
      // (def dealToStacks(self, stacks, flip=1, reverse=0, frames=-1):)


      // if we get here and there is BOTH a Stock and Waste, then we need special one just for those two
//      if (stock.isDefined && waste.isDefined) {
//        val sw:Python = layout_place_stock_and_waste(sol, stock.get, waste.get)
//        stmts = Python(stmts.getCode.toString ++ sw.getCode.toString)
//      } else {
//        // must be just the stock
//        if (stock.isDefined) {
//          stmts = Python(stmts.getCode.toString ++ layout_place_stock(sol, stock.get).getCode.toString)
//        }
//      }

      stmts
    }

    val semanticType: Type = game(game.view)
  }
}
