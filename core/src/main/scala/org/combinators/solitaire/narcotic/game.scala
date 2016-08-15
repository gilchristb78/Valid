package org.combinators.solitaire.narcotic

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

  @combinator object NumNarcoticPiles {
    def apply: Expression = Java("4").expression()
    val semanticType: Type = 'NumPiles
  }

  @combinator object RootPackage {
    def apply: NameExpr = {
      Java("org.combinators.solitaire.narcotic").nameExpression
    }
    val semanticType: Type = 'RootPackage
  }

  @combinator object NameOfTheGame {
    def apply: NameExpr = {
      Java("Narcotic").nameExpression
    }
    val semanticType: Type = 'NameOfTheGame
  }

  @combinator object Initialization {
    def apply(rootPackage: NameExpr, numberOfPiles: Expression): Seq[Statement] = {
      java.Initialization.render(rootPackage, numberOfPiles).statements()
    }
    val semanticType: Type = 'RootPackage =>: 'NumPiles =>: 'Initialization :&: 'NonEmptySeq
  }

  @combinator object ExtraImports {
    def apply(): Seq[ImportDeclaration] = Seq.empty
    val semanticType: Type = 'ExtraImports
  }

  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = Seq.empty
    val semanticType: Type = 'ExtraMethods
  }

  @combinator object ExtraFields {
    def apply(numberOfPiles: Expression): Seq[FieldDeclaration] = {
      java.ExtraFields
        .render(numberOfPiles)
        .classBodyDeclarations()
        .map(_.asInstanceOf[FieldDeclaration])
    }
    val semanticType: Type = 'NumPiles =>: 'ExtraFields
  }
}
