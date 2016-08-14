package org.combinators.solitaire.shared

import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.{CompilationUnit, ImportDeclaration}
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared

trait GameTemplate {
  @combinator object MainGame {

    def apply(rootPackage: NameExpr,
      nameParameter: NameExpr,
      extraImports: Seq[ImportDeclaration],
      extraFields: Seq[FieldDeclaration],
      extraMethods: Seq[MethodDeclaration],
      initializeSteps: Seq[Statement],
      winParameter: Seq[Statement]): CompilationUnit = {

      shared.java.GameTemplate
        .render(
          rootPackage = rootPackage,
          extraImports = extraImports,
          nameParameter = nameParameter,
          extraFields = extraFields,
          extraMethods = extraMethods,
          winParameter = winParameter,
          initializeSteps = initializeSteps)
        .compilationUnit()
    }

    val semanticType: Type =
      'RootPackage =>:
        'NameOfTheGame =>:
        'ExtraImports =>:
        'ExtraFields =>:
        'ExtraMethods =>:
        'Initialization :&: 'NonEmptySeq =>:
        'WinConditionChecking :&: 'NonEmptySeq =>:
        'SolitaireVariation
  }
}
