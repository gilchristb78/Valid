package org.combinators.solitaire.archway

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.{Expression, Name}
import com.github.javaparser.ast.ImportDeclaration
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import domain._
import org.combinators.solitaire.shared._
import org.combinators.solitaire.shared.compilation.{CodeGeneratorRegistry, generateHelper}

/**
  * Defines Java package, the game's name, initializes the domain model,
  * the UI, and the controllers (doesn't define them, just generates),
  * and includes extra fields and methods.
  */
class ArchwayDomain(override val solitaire: Solitaire) extends SolitaireDomain(solitaire) with GameTemplate with Controller {

  /**
    * Freecell requires specialized extensions for constraints to work.
    */
  @combinator object DefaultGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.generators
    val semanticType: Type = constraints(constraints.generator)
  }

  @combinator object HelperMethodsArchway {
    def apply(): Seq[MethodDeclaration] = generateHelper.helpers(solitaire)

    val semanticType: Type = constraints(constraints.methods)
  }

  /**
    * Deal may require additional generators.
    */
  @combinator object DefaultDealGenerator {
    def apply: CodeGeneratorRegistry[Expression] = constraintCodeGenerators.mapGenerators
    val semanticType: Type = constraints(constraints.map)
  }



  /*
   * Because Aces and Kings have differing behavior in the Foundation, I have to make them as subclasses.
   */
  @combinator object MakeAcesUpPile        extends ExtendModel("Pile",    "AcesUpPile",    'AcesUpPileClass)
  @combinator object MakeKingsDownPile     extends ExtendModel("Pile",    "KingsDownPile", 'KingsDownPileClass)
  @combinator object MakeAcesUpPileView    extends ExtendView("PileView", "AcesUpPileView",    "AcesUpPile",    'AcesUpPileViewClass)
  @combinator object MakeKingsDownPileView extends ExtendView("PileView", "KingsDownPileView", "KingsDownPile", 'KingsDownPileViewClass)

  /**
    * Generates import statements for the model and controller packages.
    */
  @combinator object ExtraImports {
    def apply(nameExpr: Name): Seq[ImportDeclaration] = {
      Seq(
        Java(s"import $nameExpr.controller.*;").importDeclaration(),
        Java(s"import $nameExpr.model.*;").importDeclaration()
      )
    }
    val semanticType: Type = packageName =>: game(game.imports)
  }

  /**
    * Generate extra methods. Here we only need the preferred window size of the game.
    */
  @combinator object ExtraMethods {
    def apply(): Seq[MethodDeclaration] =
      Java(s"""|public Dimension getPreferredSize() {
               |  return new Dimension (1280, 1280);
               |}""".stripMargin).methodDeclarations() ++
      Java(s"""
         |public java.util.Enumeration<Move> availableMoves() {
         |        java.util.Vector<Move> v = new java.util.Vector<Move>();
         |        // Try all moves from the Reserve to the Aces and Kings Foundation and the Tableau.
         |        for (Pile r : reserve) {
         |            for (AcesUpPile a : foundation) {
         |                ReserveToFoundation rtf = new PotentialReserveToFoundation(r, a);
         |                if (rtf.valid(this)) {
         |                    v.add(rtf);
         |                }
         |            }
         |            for (KingsDownPile k : kings) {
         |                ReserveToKingsFoundation rkf = new PotentialReserveToKingsFoundation(r, k);
         |                if (rkf.valid(this)) {
         |                    v.add(rkf);
         |                }
         |            }
         |            for (Column t : tableau) {
         |                ReserveToTableau rt = new PotentialReserveToTableau(r, t);
         |                if (rt.valid(this)) {
         |                    v.add(rt);
         |                }
         |            }
         |        }
         |        // Try all moves from the Tableau to the Aces and Kings Foundation.
         |        for (Column t : tableau) {
         |            for (AcesUpPile a : foundation) {
         |                TableauToFoundation tf = new PotentialTableauToFoundation(t, a);
         |                if (tf.valid(this)) {
         |                    v.add(tf);
         |                }
         |            }
         |            // TODO: The 3H is duplicated when returned to the Tableau.
         |            for (KingsDownPile k : kings) {
         |                TableauToKingsFoundation tk = new PotentialTableauToKingsFoundation(t, k);
         |                if (tk.valid(this)) {
         |                    v.add(tk);
         |                }
         |            }
         |        }
         |        return v.elements();
         |    }
       """.stripMargin).methodDeclarations()


    val semanticType: Type = game(game.methods :&: game.availableMoves)
  }

}
