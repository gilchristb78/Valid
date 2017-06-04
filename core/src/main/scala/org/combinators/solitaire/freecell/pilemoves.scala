package org.combinators.solitaire.freecell

import com.github.javaparser.ast.`type`.{Type => JType}
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._
import domain.constraints._
import domain._
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import com.github.javaparser.ast.CompilationUnit


trait PileMoves extends shared.Moves {

  // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
      var updated = super.init(gamma, s)
      println (">>>> PileMoves dynamic combinators")
      
      val truth = new ReturnConstraint (new ReturnTrueExpression)
      val falsehood = new ReturnConstraint (new ReturnFalseExpression)
      val isEmpty = new ElementEmpty ("destination")

         
      // FreePile to FreePile logic
      val if0 = new IfConstraint(isEmpty)
      updated = updated
         .addCombinator (new StatementCombinator (if0,
			 'Move ('FreePileToFreePile, 'CheckValidStatements)))

 
      // Column To Free Pile Logic
      val isSingle = new ExpressionConstraint("movingColumn.count()", "==", "1")
      val if1 = new IfConstraint(isEmpty, 
		  new IfConstraint(isSingle),
                falsehood)

      updated = updated
          .addCombinator (new StatementCombinator (if1, 
	   	          'Move ('ColumnToFreePile, 'CheckValidStatements)))

      // Column To Home Pile logic. Just grab first column
      val aCol = s.getTableau().iterator().next()

      val if2 = 
        new IfConstraint(isEmpty, 
           new IfConstraint(new IsAce(aCol, "movingColumn")),
           new IfConstraint(new NextRank("movingColumn.peek()", "destination.peek()"),
	       new IfConstraint(new SameSuit("movingColumn.peek()", "destination.peek()")),
	       falsehood))

      updated = updated
          .addCombinator (new StatementCombinator(if2, 
			 'Move ('ColumnToHomePile, 'CheckValidStatements)))

      // FreePile to HomePile 
      val aCard = new Card
      val nonEmpty = new ExpressionConstraint("destination.count()", "!=", "0")

      val if3 = 
         new IfConstraint(isEmpty,
	    new IfConstraint(new IsAce(aCard, "movingCard")),
            new IfConstraint(new NextRank("movingCard", "destination.peek()"),
	      new IfConstraint(new SameSuit("movingCard", "destination.peek()")),
            falsehood))

      updated = updated
          .addCombinator (new StatementCombinator(if3,
                         'Move ('FreePileToHomePile, 'CheckValidStatements)))

      // FreePile to Column.
     val if5_inner =
          new IfConstraint(new OppositeColor("movingCard", "destination.peek()"),
            new IfConstraint(new NextRank("destination.peek()", "movingCard")),
            falsehood)

      val if5 = new IfConstraint(isEmpty, truth, if5_inner)
 
      updated = updated
          .addCombinator (new StatementCombinator (if5, 
			  'Move ('FreePileToColumn, 'CheckValidStatements)))

      updated
  }

  

  //		val semanticType: Type =
  //				'RootPackage =>:
  //					'Move(semanticMoveNameType, 'ClassName) =>:
  //				  'Move(semanticMoveNameType, 'HelperMethods) =>:
  //					'Move(semanticMoveNameType, 'DoStatements) =>:
  //					'Move(semanticMoveNameType, 'UndoStatements) =>:
  //					'Move(semanticMoveNameType, 'CheckValidStatements) =>:
  //					'Move(semanticMoveNameType :&: 'GenericMove, 'CompleteMove)

// Had to rename to avoid name clashes with Java domain-class names.
/////  @combinator object FreePileToColumnMoveObject extends SolitaireMove('FreePileToColumn)
/////  @combinator object ColumnToFreePileMoveObject extends SolitaireMove('ColumnToFreePile)
/////  @combinator object ColumnToHomePileMoveObject extends SolitaireMove('ColumnToHomePile)
/////  @combinator object FreePileToHomePileMoveObject extends SolitaireMove('FreePileToHomePile)
/////  @combinator object FreePileToFreePileMoveObject extends SolitaireMove('FreePileToFreePile)

//  @combinator object FreePileToColumn {
//    def apply: SimpleName = Java("FreePileToColumn").simpleName()
//    val semanticType: Type = 'Move ('FreePileToColumn, 'ClassName)
//  }
//  @combinator object ColumnToHomePile {
//    def apply: SimpleName = Java("ColumnToHomePile").simpleName()
//    val semanticType: Type = 'Move ('ColumnToHomePile, 'ClassName)
//  }
//  @combinator object FreePileToHomePile {
//    def apply: SimpleName = Java("FreePileToHomePile").simpleName()
//    val semanticType: Type = 'Move ('FreePileToHomePile, 'ClassName)
//  }
//  @combinator object ColumnToFreePile {
//    def apply: SimpleName = Java("ColumnToFreePile").simpleName()
//    val semanticType: Type = 'Move ('ColumnToFreePile, 'ClassName)
//  }
//  @combinator object FreePileToFreePile {
//    def apply: SimpleName = Java("FreePileToFreePile").simpleName()
//    val semanticType: Type = 'Move ('FreePileToFreePile, 'ClassName)
//  }

  @combinator object PotentialPileToColumnMoveObject extends PotentialMove('FreePileToColumn)
  @combinator object PotentialFreePileToHomePileMoveObject extends PotentialMove('FreePileToHomePile)
  @combinator object PotentialFreePileToFreePileMoveObject extends PotentialMove('FreePileToFreePile)

 @combinator object PotentialColumnToFreePileMoveObject extends PotentialMoveOneCardFromStack('ColumnToFreePile)
  @combinator object PotentialColumnToHomePileMoveObject extends PotentialMoveOneCardFromStack('ColumnToHomePile)

  @combinator object PotentialStackMoveFree {
    def apply(): JType = Java("Column").tpe()
    val semanticType: Type = 'Move ('ColumnToFreePile, 'TypeConstruct)
  }
  @combinator object PotentialStackMoveHome {
    def apply(): JType = Java("Column").tpe()
    val semanticType: Type = 'Move ('ColumnToHomePile, 'TypeConstruct)
  }

//  @combinator object PotentialFreeCellPileDraggingVariable {
//    def apply(): SimpleName = Java("movingCard").simpleName()
//    val semanticType: Type = 'Move ('FreePileToColumn, 'DraggingCardVariableName)
//  }
//  @combinator object PotentialColumnToHomePileDraggingVariable {
//    def apply(): SimpleName = Java("movingColumn").simpleName()
//    val semanticType: Type = 'Move ('ColumnToHomePile, 'DraggingCardVariableName)
//  }
//  @combinator object PotentialFreePileToHomePileDraggingVariable {
//    def apply(): SimpleName = Java("movingCard").simpleName()
//    val semanticType: Type = 'Move ('FreePileToHomePile, 'DraggingCardVariableName)
//  }
//  @combinator object PotentialColumnToFreePileDraggingVariable {
//    def apply(): SimpleName = Java("movingColumn").simpleName()
//    val semanticType: Type = 'Move ('ColumnToFreePile, 'DraggingCardVariableName)
//  }
//  @combinator object PotentialFreePileToFreePileDraggingVariable {
//    def apply(): SimpleName = Java("movingCard").simpleName()
//    val semanticType: Type = 'Move ('FreePileToFreePile, 'DraggingCardVariableName)
//  }
//
  //	val semanticType: Type =
  //      'RootPackage =>:
  //      'Move(semanticMoveNameType, 'ClassName) =>:
  //      'Move(semanticMoveNameType, 'DraggingCardVariableName) =>:
  //      'Move(semanticMoveNameType :&: 'PotentialMove, 'CompleteMove)

////  @combinator object PileToColumnMoveHelper {
////    def apply(name: SimpleName): Seq[BodyDeclaration[_]] = {
////      moves.piletocolumn.java.PileToColumnMoveHelper.render(name).classBodyDeclarations()
////    }
////    val semanticType: Type = 'Move ('FreePileToColumn, 'ClassName) =>: 'Move ('FreePileToColumn, 'HelperMethods)
////  }
////  @combinator object ColumnToHomePileMoveHelper {
////    def apply(name: SimpleName): Seq[BodyDeclaration[_]] = {
////      moves.columntohomepile.java.ColumnToHomePileMoveHelper.render(name).classBodyDeclarations()
////    }
////    val semanticType: Type = 'Move ('ColumnToHomePile, 'ClassName) =>: 'Move ('ColumnToHomePile, 'HelperMethods)
////  }
////  @combinator object FreePileToHomePileMoveHelper {
////    def apply(name: SimpleName): Seq[BodyDeclaration[_]] = {
////      moves.freecelltohomepile.java.FreePileToHomePileMoveHelper.render(name).classBodyDeclarations()
////    }
////    val semanticType: Type = 'Move ('FreePileToHomePile, 'ClassName) =>: 'Move ('FreePileToHomePile, 'HelperMethods)
////  }
////  @combinator object ColumnToFreePileMoveHelper {
 ////   def apply(name: SimpleName): Seq[BodyDeclaration[_]] = {
////      moves.columntofreepile.java.ColumnToFreePileMoveHelper.render(name).classBodyDeclarations()
////    }
////    val semanticType: Type = 'Move ('ColumnToFreePile, 'ClassName) =>: 'Move ('ColumnToFreePile, 'HelperMethods)
////  }
////  @combinator object FreePileToFreePileMoveHelper {
////    def apply(name: SimpleName): Seq[BodyDeclaration[_]] = {
////      moves.freepiletofreepile.java.FreePileToFreePileMoveHelper.render(name).classBodyDeclarations()
////    }
////    val semanticType: Type = 'Move ('FreePileToFreePile, 'ClassName) =>: 'Move ('FreePileToFreePile, 'HelperMethods)
////  }



//  @combinator object PileToColumnMoveDo {
//    def apply(): Seq[Statement] = Java("destination.add(movingCard);").statements()
//    val semanticType: Type = 'Move ('FreePileToColumn, 'DoStatements)
//  }
//  @combinator object ColumnToHomePileMoveDo {
//    def apply(): Seq[Statement] = Java("destination.push(movingColumn);").statements()
//    val semanticType: Type = 'Move ('ColumnToHomePile, 'DoStatements)
//  }
//  @combinator object FreePileToHomePileMoveDo {
//    def apply(): Seq[Statement] = Java("destination.add(movingCard);").statements()
//    val semanticType: Type = 'Move ('FreePileToHomePile, 'DoStatements)
//  }
//  @combinator object ColumnToFreePileMoveDo {
//    def apply(): Seq[Statement] = Java("destination.push(movingColumn);").statements()
//   val semanticType: Type = 'Move ('ColumnToFreePile, 'DoStatements)
//  }
//  @combinator object FreePileToFreePileMoveDo {
//    def apply(): Seq[Statement] = Java("destination.add(movingCard);").statements()
//    val semanticType: Type = 'Move ('FreePileToFreePile, 'DoStatements)
//  }




//  @combinator object PileToColumnMoveUndo {
//    def apply(): Seq[Statement] = Java("""source.add(destination.get());""").statements()
//    val semanticType: Type = 'Move ('FreePileToColumn, 'UndoStatements)
//  }
//  @combinator object ColumnToHomePileMoveUndo {
//    def apply(): Seq[Statement] = Java("""source.add(destination.get());""").statements()
//    val semanticType: Type = 'Move ('ColumnToHomePile, 'UndoStatements)
//  }
//  @combinator object FreePileToHomePileMoveUndo {
//    def apply(): Seq[Statement] = Java("""source.add(destination.get());""").statements()
//    val semanticType: Type = 'Move ('FreePileToHomePile, 'UndoStatements)
//  }
//  @combinator object ColumnToFreePileMoveUndo {
//    def apply(): Seq[Statement] = Java("""source.add(destination.get());""").statements()
//    val semanticType: Type = 'Move ('ColumnToFreePile, 'UndoStatements)
//  }
//
//  @combinator object FreePileToFreePileMoveUndo {
//    def apply(): Seq[Statement] = Java("""source.add(destination.get());""").statements()
//
//    val semanticType: Type = 'Move ('FreePileToFreePile, 'UndoStatements)
//  }

}
