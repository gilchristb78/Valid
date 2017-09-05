package org.combinators.solitaire.idiot
import com.github.javaparser.ast.CompilationUnit

// name clash
import com.github.javaparser.ast.`type`.{Type => JType}

import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Constructor
import com.github.javaparser.ast.body.BodyDeclaration
import org.combinators.generic
import _root_.java.util.UUID
import domain._
import domain.constraints._
import domain.moves._
import domain.ui._
import scala.collection.mutable.ListBuffer

trait Controllers extends shared.Controller with shared.Moves with generic.JavaIdioms  {


  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
      ReflectedRepository[G] = {
      var updated = super.init(gamma, s)
      println (">>> Idiot Controller dynamic combinators.")

     // structural 
     val ui = new UserInterface(s)

     val els_it = ui.controllers
     while (els_it.hasNext()) {
       val el = els_it.next()

       // Each of these controllers are expected in the game.
       if (el == "Deck") {
	 updated = updated.    // HACK. Why special for Deck???
             addCombinator (new DeckController(Symbol(el)))
       } else if (el == "Column") {
         updated = updated.
             addCombinator (new WidgetController(Symbol(el), Symbol(el)))
       }
     }

     // not much to do, if no rules...
     if (s.getRules == null) {
       return updated
     }

     updated = createMoveClasses(updated, s)
     
     updated = createDragLogic(updated, s)

     updated = generateMoveLogic(updated, s)

     // Each move has a source and a target. The SOURCE is the locus
     // for the PRESS while the TARGET is the locus for the RELEASE.
     // These are handling the PRESS events... SHOULD BE ABLE TO 
     // INFER THESE FROM THE AVAILABLE MOVES
     updated = updated
       .addCombinator (new SingleCardMoveHandlerLocal('Column, 'Column))
       .addCombinator (new DealToTableauHandlerLocal())
       .addCombinator (new TryRemoveCardHandlerLocal('Column, 'Column))

   // Potential moves clarify structure (by type not instance). FIX ME
   // FIX ME FIX ME FIX ME
   updated = updated
       .addCombinator (new PotentialTypeConstructGenLocal("Column", 'ColumnToColumn))

   // these identify the controller names. SHOULD INFER FROM DOMAIN MODEL. FIX ME
   updated = updated
       .addCombinator (new ControllerNaming('Column, 'Column, "Idiot"))   

  // CASE STUDY: Add Automove logic at end of release handlers

   updated
  }

class TryDebug3(symbol:Symbol, columnNameType:Symbol) {

  def apply(s:Seq[Statement]) = {
    Java(s"""package anything;public class Any { }""").compilationUnit()
  }

  val semanticType:Type = 'Column ('Column, 'Released) =>: 'Debug
}

class TryDebug2(columnType:Symbol) {

  def apply() = {
    Java(s"""package anything;public class Any { }""").compilationUnit()
  }

  val semanticType:Type = 'Debug
}

class TryDebug(columnType:Symbol) {

  def apply(columnMousePressed: (SimpleName, SimpleName) => Seq[Statement]) = {
    Java(s"""package anything;public class Any { }""").compilationUnit()
  }

  val semanticType:Type = ('Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>: 'Column (columnType, 'Pressed) :&: 'NonEmptySeq) =>: 'Debug

} 

//@combinator object RealDebug extends TryDebug('Column)
//@combinator object RealDebug2 extends TryDebug2('Column)
@combinator object RealDebug3 extends TryDebug3('Column, 'Column)


/**
 * When dealing card(s) from the stock to all elements in Tableau
 */
class DealToTableauHandlerLocal() {
 def apply():Seq[Statement] = {
        Java(s"""|m = new DealDeck(theGame.deck, theGame.fieldColumns);
                 |if (m.doMove(theGame)) {
		 |   theGame.pushMove(m);
                 |}""".stripMargin).statements()
  }

  val semanticType: Type = 'Deck ('Pressed) :&: 'NonEmptySeq
}

class TryRemoveCardHandlerLocal(widgetType:Symbol, source:Symbol) {
  def apply():Seq[Statement] = {
      Java(s"""|Column srcColumn = (Column) src.getModelElement();
	       |if (((org.combinators.solitaire.idiot.Idiot)theGame).isHigher(srcColumn)) {
	       |Move m = new RemoveSingleCard(srcColumn);
               |if (m.doMove(theGame)) {
               |   theGame.pushMove(m);
               |}
	       |}""".stripMargin).statements()
  }

  val semanticType: Type = widgetType (source, 'Clicked) :&: 'NonEmptySeq
}


/** 
 * When a single card is being removed from the top card of a pile.
 */
class SingleCardMoveHandlerLocal(t:Symbol, source:Symbol) {
  def apply(): (SimpleName, SimpleName) => Seq[Statement] = {
      (widgetVariableName: SimpleName, ignoreWidgetVariableName: SimpleName) =>
        Java(s"""|$ignoreWidgetVariableName = false;
		 |Column srcColumn = (Column) src.getModelElement();
		 |
		 |// Return in the case that the pile clicked on is empty
		 |if (srcColumn.count() == 0) {
        	 |  return;
		 |}
		 |$widgetVariableName = src.getCardViewForTopCard(me);
		 |if ($widgetVariableName == null) {
		 |  return;
		 |}""".stripMargin).statements()
  }

 val semanticType: Type =
    'Pair ('WidgetVariableName, 'IgnoreWidgetVariableName) =>:
      'Column (source, 'Pressed) :&: 'NonEmptySeq
}


class PotentialDraggingVariableGeneratorLocal(m:Move, constructor:Constructor) {
  def apply(): SimpleName = {
    m match {
      case single: SingleCardMove => Java(s"""movingCard""").simpleName()
      case column: ColumnMove     => Java(s"""movingColumn""").simpleName()
    }
  }
    val semanticType: Type = constructor
}

// Note: while I can have code within the apply() method, the semanticType
// is static, so that must be passed in as is. These clarify that a
// potential moveOneCardFromStack is still a Column Type.
class PotentialTypeConstructGenLocal(s:String, constructor:Constructor) {
    def apply(): JType = Java("Column").tpe()
    val semanticType: Type = 'Move (constructor, 'TypeConstruct)
}
}


