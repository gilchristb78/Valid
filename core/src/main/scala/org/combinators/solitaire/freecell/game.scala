package org.combinators.solitaire.freecell

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration, BodyDeclaration}
import com.github.javaparser.ast.expr.{Expression, NameExpr}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared.GameTemplate
import org.combinators.solitaire.shared.Score52


trait Game extends GameTemplate with Score52 {

  @combinator object NumHomePiles {
    def apply: Expression = Java("4").expression()
    val semanticType: Type = 'NumHomePiles
  }

  @combinator object NumFreePiles {
    def apply: Expression = Java("4").expression()
    val semanticType: Type = 'NumFreePiles
  }
  
  @combinator object NumColumns {
    def apply: Expression = Java("8").expression()
    val semanticType: Type = 'NumColumns
  }
  
  @combinator object RootPackage {
    def apply: NameExpr = {
      Java("org.combinators.solitaire.freecell").nameExpression
    }
    val semanticType: Type = 'RootPackage
  }

  @combinator object NameOfTheGame {
    def apply: NameExpr = {
      Java("FreeCell").nameExpression
    }
    val semanticType: Type = 'NameOfTheGame
  }

  // @(NameOfTheGame: String, NumColumns:Expression, NumHomePiles: Expression, NumFreePiles: Expression)

  
  @combinator object Initialization {
    def apply(nameOfTheGame: NameExpr, numColumns:Expression, numHomePiles:Expression, numFreePiles: Expression): Seq[Statement] = {
      
      java.Initialization.render(nameOfTheGame.toString(), numColumns, numHomePiles, numFreePiles).statements()
    }
    val semanticType: Type = 'NameOfTheGame =>: 'NumColumns =>: 'NumHomePiles =>: 'NumFreePiles =>: 'Initialization :&: 'NonEmptySeq
  }

  @combinator object ExtraImports {
    def apply(nameExpr:NameExpr): Seq[ImportDeclaration] = {
      Seq(Java("import " + nameExpr.toString() + ".controller.*;").importDeclaration(),
          Java("import " + nameExpr.toString() + ".model.*;").importDeclaration()
      )
    }
    val semanticType: Type = 'RootPackage =>: 'ExtraImports
  }

  @combinator object ExtraMethods {
     def apply(numFreePiles:Expression, numColumns: Expression): Seq[MethodDeclaration] = {
      
      java.ExtraMethods.render(numFreePiles, numColumns).classBodyDeclarations().map(_.asInstanceOf[MethodDeclaration])
    }
    val semanticType: Type = 'NumFreePiles =>: 'NumColumns =>: 'ExtraMethods :&: 'AvailableMoves
  }

  
  @combinator object EmptyExtraMethods {
    def apply(): Seq[MethodDeclaration] = Seq.empty
    val semanticType: Type = 'ExtraMethodsBad
  }
  
  
  // @(NumHomePiles: Expression, NumFreePiles: Expression, NumColumns:Expression)
  @combinator object ExtraFields {
    def apply(numHomePiles:Expression, numFreePiles:Expression, numColumns: Expression): Seq[FieldDeclaration] = {
      java.ExtraFields
        .render(numHomePiles, numFreePiles, numColumns)
        .classBodyDeclarations()
        .map(_.asInstanceOf[FieldDeclaration])
    }
    val semanticType: Type = 'NumHomePiles =>: 'NumFreePiles =>: 'NumColumns=>: 'ExtraFields
  }
}
