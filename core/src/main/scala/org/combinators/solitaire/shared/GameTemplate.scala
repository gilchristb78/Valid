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

  /**
    * Purpose of this combinator is to declare base state for any solitaire variation, namely
    * that it starts with no core elements (Tableau, Foundation, Reserve, ...) nor Layout,
    * nor Rules.
    */
  @combinator object NewEmptySolitaire {
    def apply(): Solitaire = new Solitaire()
    val semanticType: Type =
        'Solitaire ('Tableau ('None)) :&:
        'Solitaire ('Foundation ('None)) :&:
        'Solitaire ('Reserve ('None)) :&:
        'Solitaire ('Layout ('None)) :&:
        'Solitaire ('Rules ('None))
  }

  /**
    * Class for constructing Tableau from n Columns.
    *
    * param n             number of columns to create
    * @param nAsType      type of Column within the semantic type 'Tableau ('Valid :&: typ)
    */
  class NColumnTableau(n: Int, nAsType: Type) {
    def apply(): Tableau = {
      val t = new Tableau()
      for (_ <- 1 to n)
        t.add(new Column())
      t
    }

    val semanticType: Type = 'Tableau ('Valid :&: nAsType :&: 'Column)
  }

  /**
    * Class for constructing Tableau from n Piles.
    *
    * @param n            number of piles to create
    * @param nAsType      type of Pile within the semantic type 'Tableau ('Valid :&: typ)
    */
 class NPileTableau(n: Int, nAsType: Type) {
    def apply(): Tableau = {
      val t = new Tableau()
      for (_ <- 1 to n)
        t.add(new Pile())
      t
    }

    val semanticType: Type = 'Tableau ('Valid :&: nAsType :&: 'Pile)
  }

  /**
    * Specialized combinators for common scenarios of tableaus from piles and columns.
    */
  @combinator object EightColumnTableau extends NColumnTableau(8, 'Eight)
  @combinator object FourColumnTableau extends NColumnTableau(4,  'Four)
  @combinator object EightPileTableau extends NPileTableau(8, 'Eight)
  @combinator object FourPileTableau extends NPileTableau(4, 'Four)


  /**
    * Common layout for solitaire games with just Stcok on left and tableau on right.
    */
  @combinator object StockTableauLayout {
    def apply(): Layout = {
      val lay = new Layout()
      lay.add(Layout.Stock, 15, 20, 73, 97)
      lay.add(Layout.Tableau, 120, 20, 1360, 13*97)
   
      lay
    }

    val semanticType: Type = 'Layout ('Valid :&: 'StockTableau)
  }

  /**
   * Standard Layout with Tableau below a Reserve (Left) and Foundation (Right)
   *
   * Suggestion: Make a Deck as part of domain model, so it can be used to provide
   * these necessary constants.
   *
   */
  @combinator object FoundationReserveTableauLayout {
    def apply(): Layout = {
      val lay = new Layout()

      // For record, start by assuming a standard deck size of (73x97) which can be
      // changed with some effort, or by adding parameter for decks to use.
      // width = 73
      // height = 97

      lay.add(Layout.Foundation, 390, 20, 680, 97)
      lay.add(Layout.Reserve, 15, 20, 680, 97)
      lay.add(Layout.Tableau, 15, 137, 1360, 13 * 97)

      lay
    }

    val semanticType: Type = 'Layout ('Valid :&: 'FoundationReserveTableau)
  }

  /**
    * Default initialization for a solitaire plugin requires initializing the
    * Model, View and then Control sections. Once done, then provide specific initialization
    * for the game (i.e., initial deal).
    */
  @combinator object Initialization {
    def apply(minit: Seq[Statement],
      vinit: Seq[Statement],
      cinit: Seq[Statement],
      layout: Seq[Statement]): Seq[Statement] = {

      shared.java.DomainInit.render(minit, vinit, cinit, layout).statements()
    }
    val semanticType: Type = 'Init ('Model) =>: 'Init ('View) =>: 'Init ('Control) =>: 'Init ('InitialDeal) =>: 'Initialization :&: 'NonEmptySeq
  }

  // these next three functions help map domain model to Java code
  // not entirely sure this is 'GUI' stuff.

  /**
    * Within a solitaire plugin, one needs to construct model elements that are added to the
    * model, and then view widgets to realize these model elements on the screen. This function
    * adds array of fields for both model elements and view widgets.
    *
    * Note: prefix is used when constructing these elements to ensure unique names.
    *
    * @param name
    * @param modelType
    * @param viewType
    * @param num
    * @return
    */
  def fieldGen(name:String, modelType:String, viewType:String, num:Int):Seq[FieldDeclaration] = {
     Java(s"""
           |public static final String field${name}sPrefix = "$name";
           |public $modelType[] field${name}s = new $modelType[$num];
           |public $viewType[] field${name}Views = new $viewType[$num];
           |""".stripMargin)
           .classBodyDeclarations()
           .map(_.asInstanceOf[FieldDeclaration])
  }

  /**
    * Within a solitaire plugin, one needs to register controllers with the given view widgets
    * constructed earlier.
    *
    * The default adapters (for mouse motion and undo) are registered, and the designated
    * controller is passed in as a parameter to be used and associated with the widgets.
    *
    * @param viewName
    * @param contName
    * @return
    */
  def controllerGen(viewName:String, contName:String) : Seq[Statement] = {
     Java(s"""
           |$viewName.setMouseMotionAdapter (new SolitaireMouseMotionAdapter (this));
           |$viewName.setUndoAdapter (new SolitaireUndoAdapter (this));
           |$viewName.setMouseAdapter (new $contName (this, $viewName));
           |}""".stripMargin).statements()

  }

  /**
    * Convenient function to iterate over views for a given container (i.e., Reserve or Tableau)
    * and assign controllers based on the given type.
    *
    * Iterates over variable 'j' just to be different ;)
    * @param cont
    * @param viewName
    * @param contName
    * @return
    */
  def loopControllerGen(cont: Container, viewName : String, contName:String): Seq[Statement] = {
        val nc = cont.size()
        val inner = controllerGen(viewName + "[j]", contName)

        Java(s"""
           |for (int j = 0; j < $nc; j++) {
           |  ${inner.mkString("\n")}
           |}""".stripMargin).statements()
     }


  /**
    * Convenient function to iterate over the construction of individual entity and view widgets.
    *
    * Note: Not easy to break this out into a helper function, because of the variable 'j' used
    * to iterate over the elements, and the need to compute a unique name (based on the prefix)
    * for each of the j elements.
    *
    * @param cont
    * @param modelName
    * @param viewName
    * @param typ
    * @return
    */
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

  /**
    * Generate statements that constructs a single Deck (and associated Widget view), adds it to
    * the model and initializes it to be ready.
    *
    * @param modelName
    * @return
    */
  def deckGen (modelName:String):Seq[Statement] = {
    Java(
      s"""|// Basic start of pretty much any solitaire game that requires a deck.
          |$modelName = new Deck ("deck");
          |int seed = getSeed();
          |$modelName.create(seed);
          |addModelElement ($modelName);
          |""".stripMargin).statements()
  }


  /**
    * Primary combinator which constructs the subclass of Solitaire to represent the
    * class for a variation.
    */
  @combinator object MainGame {

    def apply(rootPackage: Name, nameParameter: SimpleName,
      extraImports: Seq[ImportDeclaration], extraFields: Seq[FieldDeclaration],
      extraMethods: Seq[MethodDeclaration],
      initializeSteps: Seq[Statement],
      winParameter: Seq[Statement]): CompilationUnit = {

      shared.java.GameTemplate
        .render(rootPackage = rootPackage,
          extraImports = extraImports,
          nameParameter = nameParameter,
          extraFields = extraFields,
          extraMethods = extraMethods,
          winParameter = winParameter,
          initializeSteps = initializeSteps)
        .compilationUnit()
    }

    val semanticType: Type =
      'RootPackage =>: 'NameOfTheGame =>:
        'ExtraImports =>: 'ExtraFields =>:
        'ExtraMethods =>:
        'Initialization :&: 'NonEmptySeq =>:
        'WinConditionChecking :&: 'NonEmptySeq =>:
        'SolitaireVariation
  }

  /**
    * Synthesizes a solvable game if there are 'AvailableMoves associated with
    * the 'ExtraMethods
    */
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
