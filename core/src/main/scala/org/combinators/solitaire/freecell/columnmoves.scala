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
import org.combinators.solitaire.shared

trait ColumnMoves extends shared.Moves {
  val solitaire: Solitaire // the overall class provides this once woven in

  @combinator object FreeCellColumnToColumnMoveObject extends Move('ColumnToColumn)

  @combinator object FreeCellColumnToColumn {
    //def apply: SimpleName = Java("FreeCellColumnToColumn").simpleName()
    def apply: SimpleName = Java("ColumnToColumn").simpleName()

    val semanticType: Type = 'Move ('ColumnToColumn, 'ClassName)
  }

  @combinator object PotentialColumnToColumnMoveObject extends PotentialMoveOneCardFromStack('ColumnToColumn)

  @combinator object PotentialStackMoveColumn {
    def apply(): JType = Java("Column").tpe()
    val semanticType: Type = 'Move ('ColumnToColumn, 'TypeConstruct)
  }

  @combinator object PotentialColumnDraggingVariable {
    def apply(): SimpleName = Java("movingColumn").simpleName()
    val semanticType: Type = 'Move ('ColumnToColumn, 'DraggingCardVariableName)
  }


  @combinator object ColumnToColumnMoveHelper {
    def apply(name: SimpleName): Seq[BodyDeclaration[_]] = {
      moves.columntocolumn.java.ColumnToColumnMoveHelper.render(name).classBodyDeclarations()
    }
    val semanticType: Type = 'Move ('ColumnToColumn, 'ClassName) =>: 'Move ('ColumnToColumn, 'HelperMethods)
  }

  @combinator object ColumnToColumnMoveDo {
    def apply(): Seq[Statement] = Java("destination.push(movingColumn);").statements()
    val semanticType: Type = 'Move ('ColumnToColumn, 'DoStatements)
  }

  @combinator object ColumnToColumnMoveUndo {
    def apply(): Seq[Statement] = {
      Java(
        s"""
           |destination.select(numInColumn);
           |source.push(destination.getSelected());
           """.stripMargin).statements()
    }
    val semanticType: Type = 'Move ('ColumnToColumn, 'UndoStatements)
  }

//  @combinator object ColumnToColumnValid {
//    def apply(pkg: Name, name: SimpleName): Seq[Statement] = {
//      moves.columntocolumn.java.ColumnToColumnValid.render(pkg, name).statements()
//    }
//    val semanticType: Type = 'RootPackage =>: 'NameOfTheGame =>: 'Move ('ColumnToColumn, 'CheckValidStatements)
//  }

//  @combinator object ShortCut {
//    def apply(n0: Seq[Statement], n1: SimpleName, n5: Seq[Statement]): CompilationUnit = {
//      Java("public class A{}").compilationUnit()
//    }
//    val semanticType: Type =
//
//      'Move ('ColumnToColumn, 'UndoStatements) =>:
//        'Move ('ColumnToColumn, 'ClassName) =>:
//        'Move ('ColumnToColumn, 'CheckValidStatements) =>:
//        'ShortCut
//  }

  // to define this new class, don't we have to specify the 'FreCellColumnToColumn is a type of generic move?
  //	override val moveTaxonomy: Taxonomy =
  //    Taxonomy("GenericMove")
  //      .addSubtype("FreeCellColumnToColumn")
}
