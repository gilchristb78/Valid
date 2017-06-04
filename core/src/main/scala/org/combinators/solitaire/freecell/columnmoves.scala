package org.combinators.solitaire.freecell

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.`type`.{Type => JType}
import com.github.javaparser.ast.body.BodyDeclaration
import com.github.javaparser.ast.expr.{Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import domain._
import domain.constraints._
import org.combinators.solitaire.shared
import org.combinators.solitaire.shared._
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.cls.types.Constructor


trait ColumnMoves extends shared.Moves {
  //val solitaire: Solitaire // the overall class provides this once woven in

 // dynamic combinators added as needed
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) :
      ReflectedRepository[G] = {
      var updated = super.init(gamma, s)
      println (">>> ColumnMoves dynamic combinators.")

 // Column to Column. These values are used by the conditionals. Too java-specific
      val truth = new ReturnConstraint (new ReturnTrueExpression)
      val falsehood = new ReturnConstraint (new ReturnFalseExpression)
      val isEmpty = new ElementEmpty ("destination")

      val descend = new Descending("movingColumn")
      val alternating = new AlternatingColors("movingColumn")
      val cc_and = new AndConstraint (descend, alternating)

      val if4_inner =
        new IfConstraint(new OppositeColor("movingColumn.peek(0)", "destination.peek()"),
          new IfConstraint(new NextRank("destination.peek()", "movingColumn.peek(0)")),
          falsehood)

     val if4 =
        new IfConstraint(descend,
          new IfConstraint(alternating,
            new IfConstraint(isEmpty,
              truth,   // not yet check number free
              if4_inner),
            falsehood),
            falsehood)

      updated = updated
          .addCombinator (new StatementCombinator(if4,
                         'Move ('ColumnToColumn, 'CheckValidStatements)))

      updated
    }
 

/////  @combinator object FreeCellColumnToColumnMoveObject extends SolitaireMove('ColumnToColumn)

///  @combinator object FreeCellColumnToColumn {
///    def apply: SimpleName = Java("ColumnToColumn").simpleName()
///
///    val semanticType: Type = 'Move ('ColumnToColumn, 'ClassName)
///  }

  @combinator object PotentialColumnToColumnMoveObject extends PotentialMoveOneCardFromStack('ColumnToColumn)

  @combinator object PotentialStackMoveColumn {
    def apply(): JType = Java("Column").tpe()
    val semanticType: Type = 'Move ('ColumnToColumn, 'TypeConstruct)
  }

  @combinator object PotentialColumnDraggingVariable {
    def apply(): SimpleName = Java("movingColumn").simpleName()
    val semanticType: Type = 'Move ('ColumnToColumn, 'DraggingCardVariableName)
  }


//  @combinator object ColumnToColumnMoveHelper {
//    def apply(name: SimpleName): Seq[BodyDeclaration[_]] = {
//      moves.columntocolumn.java.ColumnToColumnMoveHelper.render(name).classBodyDeclarations()
//    }
//    val semanticType: Type = 'Move ('ColumnToColumn, 'ClassName) =>: 'Move ('ColumnToColumn, 'HelperMethods)
//  }

//  @combinator object ColumnToColumnMoveDo {
//    def apply(): Seq[Statement] = Java("destination.push(movingColumn);").statements()
//    val semanticType: Type = 'Move ('ColumnToColumn, 'DoStatements)
//  }
//
//  @combinator object ColumnToColumnMoveUndo {
//    def apply(): Seq[Statement] = {
//      Java(
//        s"""
//           |destination.select(numInColumn);
//           |source.push(destination.getSelected());
//           """.stripMargin).statements()
//    }
//    val semanticType: Type = 'Move ('ColumnToColumn, 'UndoStatements)
//  }

}
