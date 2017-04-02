package org.combinators.solitaire.shared

import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.{CompilationUnit, ImportDeclaration}
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared
import scala.collection.JavaConversions._
// domain
import domain._

trait GameTemplate {

  // domain model elements for game defined here...
  lazy val tableauType = Variable("TableauType")

  @combinator object NewEmptySolitaire {
    def apply(): Solitaire = {
       new Solitaire()
    }
    
    val semanticType:Type = 'Solitaire('Tableau('None)) :&:
                            'Solitaire('Foundation('None)) :&:
                            'Solitaire('Reserve('None)) :&:
			    'Solitaire('Layout('None))
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
    
    val semanticType:Type = 'Tableau('Valid :&: 'Eight :&: 'Column)
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
    
    val semanticType:Type = 'Tableau('Valid :&: 'Four :&: 'Column)
  }

  // Standard Layout with Tableau below a Reserve (Left) and Foundation (Right)
  @combinator object FoundationReserveTableauLayout {
    def apply(): Layout = {
       val lay = new Layout()

       // width = 73
       // height = 97

       lay.add (Layout.Foundation, 390, 20, 680, 97);
       lay.add (Layout.Reserve,     15, 20, 680, 97);
       lay.add (Layout.Tableau,     15, 137, 1360, 13*97);
       
       lay    
    }
    
    val semanticType:Type = 'Layout('Valid :&: 'FoundationReserveTableau)
  }


//
//  // generic 4-pile Foundation
//  @combinator object FourPileFoundation {
//    def apply(): Foundation = {
//       val f = new Foundation()
//       f.add (new Pile())    // put into for-loop soon.
//       f.add (new Pile())
//       f.add (new Pile())
//       f.add (new Pile())
//
//       // also use scalaList and work with it
//       val scalaList = f.iterator.toList
//       
//       // Layout for any container which horizontal placement of widgets within. Spacing can 
//       // be computed from the bounds
//       val it = f.iterator()
//       while (it.hasNext()) {
//         val p = it.next()
//         
////          val winStmts =
////          JavaParser.parseStatement("{ int x = 42; int z = -1; z++; foo(x, z); }")
////            .asInstanceOf[com.github.javaparser.ast.stmt.BlockStmt]
////            .stmts
////  
//       }
//       
//       f
//    }
//    
//    val semanticType:Type = 'Foundation('Valid :&: 'Four :&: 'Pile)
//  }


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
