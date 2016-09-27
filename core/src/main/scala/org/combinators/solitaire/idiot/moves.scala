package org.combinators.solitaire.idiot

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{Expression, NameExpr}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared

trait Moves extends shared.Moves {
  @combinator object RemoveCard {
    def apply(rootPackage: NameExpr, numberOfColumns: Expression): CompilationUnit = {
      moves.java.MoveRemoveCard.render(rootPackage, numberOfColumns).compilationUnit()
    }
    val semanticType: Type = 'RootPackage =>: 'NumColumns =>: 'MoveRemoveCards
  }

  @combinator object ColumnMove {
    def apply(rootPackage: NameExpr, columnCondition: Seq[Statement]): CompilationUnit = {
      moves.java.ColumnMove.render(rootPackage, columnCondition).compilationUnit()
    }
    val semanticType: Type = 'RootPackage =>: 'ColumnToColumnCondition =>: 'ColumnMove
  }

  @combinator object ColumnToColumnCondition {
    def apply(): Seq[Statement] = {
      moves.java.ColumnToColumnCondition.render().statements()
    }
    val semanticType: Type = 'ColumnToColumnCondition
  }

  @combinator object DeckPressedHandler {
    def apply(rootPackage: NameExpr, nameOfTheGame: NameExpr): Seq[Statement] = {
      moves.java.DeckPressed.render(rootPackage, nameOfTheGame).statements()
    }
    val semanticType: Type = 'RootPackage =>: 'NameOfTheGame =>: 'Deck('Pressed)
  }

}