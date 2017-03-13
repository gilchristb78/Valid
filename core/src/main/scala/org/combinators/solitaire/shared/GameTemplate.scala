package org.combinators.solitaire.shared

import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.{CompilationUnit, ImportDeclaration}
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared

// domain
import domain._

trait GameTemplate {

  // domain model elements for game defined here...
  lazy val tableauType = Variable("TableauType")

  @combinator object NewEmptySolitaire {
    def apply(): Solitaire = {
       new Solitaire()
    }
    
    val semanticType:Type = 'P('NoTableau) :&: 'P('NoFoundation)
  }

  // generic 8-column tableau
  @combinator object EightColumnTableau {
    def apply(): Tableau = {
       val t = new Tableau()
       t.add (new Column())    // put into for-loop soon.
       t.add (new Column())
       t.add (new Column())
       t.add (new Column())
       t.add (new Column())
       t.add (new Column())
       t.add (new Column())
       t.add (new Column())

       t
    }
    
    val semanticType:Type = 'ValidTableau :&: 'EightColumnTableau
  }

  // generic 4-column tableau
  @combinator object FourColumnTableau {
    def apply(): Tableau = {
       val t = new Tableau()
       t.add (new Column())    // put into for-loop soon.
       t.add (new Column())
       t.add (new Column())
       t.add (new Column())

       t
    }
    
    val semanticType:Type = 'ValidTableau :&: 'FourColumnTableau
  }

  // generic 4-pile Foundation
  @combinator object FourPileFoundation {
    def apply(): Foundation = {
       val f = new Foundation()
       f.add (new Pile())    // put into for-loop soon.
       f.add (new Pile())
       f.add (new Pile())
       f.add (new Pile())

       f
    }
    
    val semanticType:Type = 'ValidFoundation :&: 'FourPileFoundation
  }


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
  
  @combinator object SolvableGame {

    def apply(rootPackage: NameExpr,
      nameParameter: NameExpr,
      extraImports: Seq[ImportDeclaration],
      extraFields: Seq[FieldDeclaration],
      extraMethods: Seq[MethodDeclaration],
      initializeSteps: Seq[Statement],
      winParameter: Seq[Statement]): CompilationUnit = {

      shared.java.SolvableGameTemplate
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
        'ExtraMethods :&: 'AvailableMoves =>:
        'Initialization :&: 'NonEmptySeq =>:
        'WinConditionChecking :&: 'NonEmptySeq =>:
        'SolitaireVariation :&: 'Solvable
  }
  
}
