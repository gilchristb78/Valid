package org.combinators.solitaire.shared

import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.{CompilationUnit, ImportDeclaration}
import de.tu_dortmund.cs.ls14.cls.interpreter.combinator
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared
import de.tu_dortmund.cs.ls14.twirl.Java

// domain
import domain._
import domain.ui._

trait GameTemplate {

  // domain model elements for game defined here...
  lazy val tableauType = Variable("TableauType")

  @combinator object NewEmptySolitaire {
    def apply(): Solitaire = new Solitaire()
    val semanticType: Type =
        'Solitaire ('Tableau ('None)) :&:
        'Solitaire ('Foundation ('None)) :&:
        'Solitaire ('Reserve ('None)) :&:
        'Solitaire ('Layout ('None)) :&:
        'Solitaire ('Rules ('None))
  }

  class NColumnTableau(n: Int, nAsType: Type) {
    def apply(): Tableau = {
      val t = new Tableau()
      for (_ <- 1 to n)
        t.add(new Column())
      t
    }

    val semanticType: Type = 'Tableau ('Valid :&: nAsType :&: 'Column)
  }

  // generic 8-column tableau
  @combinator object EightColumnTableau extends NColumnTableau(8, 'Eight)

  // generic 4-column tableau
  @combinator object FourColumnTableau extends NColumnTableau(4, 'Four)

  // Deck on the left and four columns to the right
  @combinator object StockTableauLayout {
    def apply(): Layout = {
      val lay = new Layout()
      lay.add(Layout.Stock, 15, 20, 73, 97)
      lay.add(Layout.Tableau, 120, 20, 1360, 13*97)
   
      lay
    }

    val semanticType: Type = 'Layout ('Valid :&: 'StockTableau)
  }

  // Standard Layout with Tableau below a Reserve (Left) and Foundation (Right)
  @combinator object FoundationReserveTableauLayout {
    def apply(): Layout = {
      val lay = new Layout()

      // width = 73
      // height = 97

      lay.add(Layout.Foundation, 390, 20, 680, 97)
      lay.add(Layout.Reserve, 15, 20, 680, 97)
      lay.add(Layout.Tableau, 15, 137, 1360, 13 * 97)

      lay
    }

    val semanticType: Type = 'Layout ('Valid :&: 'FoundationReserveTableau)
  }

  // create three separate blocks based on the domain model.
  @combinator object Initialization {
    def apply(minit: Seq[Statement],
      vinit: Seq[Statement],
      cinit: Seq[Statement],
      layout: Seq[Statement]): Seq[Statement] = {

      shared.java.DomainInit.render(minit, vinit, cinit, layout).statements()
    }
    val semanticType: Type = 'Init ('Model) =>: 'Init ('View) =>: 'Init ('Control) =>: 'Init ('Layout) =>: 'Initialization :&: 'NonEmptySeq
  }

  // these next three functions help map domain model to Java code
  // not entirely sure this is 'GUI' stuff.

  /** Field Declarations: Used to create fields in subclass. */
  def fieldGen(name:String, modelType:String, viewType:String, num:Int):Seq[FieldDeclaration] = {
     Java(s"""
           |public static final String field${name}sPrefix = "$name";
           |public $modelType[] field${name}s = new $modelType[$num];
           |public $viewType[] field${name}Views = new $viewType[$num];
           |""".stripMargin)
           .classBodyDeclarations()
           .map(_.asInstanceOf[FieldDeclaration])
  }

  /** Insert code for basic controllers on a widget. */
  def controllerGen(viewName:String, contName:String) : Seq[Statement] = {
     Java(s"""
           |$viewName.setMouseMotionAdapter (new SolitaireMouseMotionAdapter (this));
           |$viewName.setUndoAdapter (new SolitaireUndoAdapter (this));
           |$viewName.setMouseAdapter (new $contName (this, $viewName));
           |}""".stripMargin).statements()

  }

  /** Useful for constructing controller initializations. */
  def loopControllerGen(cont: Container, viewName : String, contName:String): Seq[Statement] = {
        val nc = cont.size()
        val inner = controllerGen(viewName + "[j]", contName)

        Java(s"""
           |for (int j = 0; j < $nc; j++) {
           |  ${inner.mkString("\n")}
           |}""".stripMargin).statements()
     }

  def constructGen(modelName:String, viewName:String, typ:String):Seq[Statement] = {
    Java(s"""
           |$modelName = new $typ(${modelName}Prefix + (j+1));
           |addModelElement ($modelName);
           |$viewName = new ${typ}View($modelName);
           |}""".stripMargin).statements()

  }

  /** Generate a deck which has special requirements. */
  def deckGen (modelName:String, viewName:String):Seq[Statement] = {
     Java(
        s"""|// Basic start of pretty much any solitaire game that requires a deck.
            |$modelName = new Deck ("deck");
            |$viewName = new DeckView($modelName);
            |int seed = getSeed();
            |$modelName.create(seed);
            |addModelElement ($modelName);
            |""".stripMargin).statements()
  }

  /** Useful for constructing view initializations. */
  def loopConstructGen(cont: Container, modelName: String, viewName : String, typ:String): Seq[Statement] = {
        val nc = cont.size()
        Java(
        s"""
           |for (int j = 0; j < $nc; j++) {
           |  $modelName[j] = new $typ(${modelName}Prefix + (j+1));
           |  addModelElement ($modelName[j]);
           |  $viewName[j] = new ${typ}View($modelName[j]);
           |}""".stripMargin).statements()
     }

  @combinator object MainGame {

    def apply(rootPackage: Name,
      nameParameter: SimpleName,
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

    def apply(rootPackage: Name,
      nameParameter: SimpleName,
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
