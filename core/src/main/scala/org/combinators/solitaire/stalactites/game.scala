package org.combinators.solitaire.stalactites

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, NameExpr}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared.GameTemplate
import org.combinators.solitaire.shared.Score52


trait Game extends GameTemplate with Score52 {

  @combinator object NumReservePiles {
    def apply: Expression = Java("2").expression()
    val semanticType: Type = 'NumReservePiles
  }

  
  @combinator object NumFoundations {
    def apply: Expression = Java("4").expression()
    val semanticType: Type = 'NumFoundations
  }
  
  @combinator object NumColumns {
    def apply: Expression = Java("8").expression()
    val semanticType: Type = 'NumColumns
  }
  
  @combinator object RootPackage {
    def apply: NameExpr = {
      Java("org.combinators.solitaire.stalactites").nameExpression
    }
    val semanticType: Type = 'RootPackage
  }

  @combinator object NameOfTheGame {
    def apply: NameExpr = {
      Java("Stalactites").nameExpression
    }
    val semanticType: Type = 'NameOfTheGame
  }

  // NumColumns: Expression, NumReservePiles: Expression, NumFoundations: Expression)
  @combinator object Initialization {
    def apply(numColumns:Expression, numReservePiles: Expression, numFoundations:Expression): Seq[Statement] = {
      java.Initialization.render(numColumns, numReservePiles, numFoundations).statements()
    }
    val semanticType: Type = 'NumColumns =>: 'NumReservePiles =>: 'NumFoundations =>: 'Initialization :&: 'NonEmptySeq
  }

  @combinator object ExtraImports {
    def apply(): Seq[ImportDeclaration] = Seq.empty
    val semanticType: Type = 'ExtraImports
  }

  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = {
      java.ExtraMethods.render().classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])
    }
    val semanticType: Type = 'ExtraMethods
  }

  // @(NumColumns: Expression, NumReservePiles: Expression, NumFoundations: Expression)
  @combinator object ExtraFields {
    def apply(numColumns:Expression, numReservePiles: Expression, numFoundations:Expression): Seq[FieldDeclaration] = {
      java.ExtraFields
        .render(numColumns, numReservePiles, numFoundations)
        .classBodyDeclarations()
        .map(_.asInstanceOf[FieldDeclaration])
    }
    val semanticType: Type = 'NumColumns =>: 'NumReservePiles =>: 'NumFoundations =>: 'ExtraFields
  }
}
