package org.combinators.solitaire.idiot

import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, NameExpr}

import com.github.javaparser.ast.{CompilationUnit, ImportDeclaration}
import com.github.javaparser.ast.stmt.Statement
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import de.tu_dortmund.cs.ls14.twirl.Java
import org.combinators.solitaire.shared.GameTemplate
import org.combinators.solitaire.shared.Score52


trait Game extends GameTemplate with Score52 {

  @combinator object NumIdiotColumns {
    def apply: Expression = Java("4").expression()
    val semanticType: Type = 'NumColumns
  }

  @combinator object RootPackage {
    def apply: NameExpr = {
      Java("org.combinators.solitaire.idiot").nameExpression
    }
    val semanticType: Type = 'RootPackage
  }

  @combinator object NameOfTheGame {
    def apply: NameExpr = {
      Java("Idiot").nameExpression
    }
    val semanticType: Type = 'NameOfTheGame
  }

  @combinator object Initialization {
    def apply(rootPackage: NameExpr, numberOfColumns: Expression): Seq[Statement] = {
      java.Initialization.render(rootPackage, numberOfColumns).statements()
    }
    val semanticType: Type = 'RootPackage =>: 'NumColumns =>: 'Initialization :&: 'NonEmptySeq
  }

  @combinator object ExtraImports {
    def apply(): Seq[ImportDeclaration] = Seq.empty
    val semanticType: Type = 'ExtraImports
  }

  /*
  @combinator object ShortCut {
    def apply(seq : Seq[Statement]): CompilationUnit = {
      Java("public class A{}").compilationUnit()
    }
    val semanticType: Type = 'MoveRemoveCards =>: 'ShortCut
  }
  */
  
  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] = Seq.empty
    val semanticType: Type = 'ExtraMethods
  }

  @combinator object ExtraFields {
    def apply(numberOfColumns: Expression): Seq[FieldDeclaration] = {
      java.ExtraFields
        .render(numberOfColumns)
        .classBodyDeclarations()
        .map(_.asInstanceOf[FieldDeclaration])
    }
    val semanticType: Type = 'NumColumns =>: 'ExtraFields
  }
}
