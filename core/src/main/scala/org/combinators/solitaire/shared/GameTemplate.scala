package org.combinators.solitaire.shared

import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Name, SimpleName, Expression}
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

  class ExtendModel(parent: String, subclass: String, typ:Symbol) {

    def apply(rootPackage: Name): CompilationUnit = {
      val name = rootPackage.toString()
      Java(s"""package $name;
                import ks.common.model.*;
                public class $subclass extends $parent {
		  public $subclass (String name) {
		    super(name);
		  }
		}
	     """).compilationUnit()
    }

    val semanticType : Type = 'RootPackage =>: typ
  }

  class ExtendView(parent: String, subclass: String, model: String, typ:Symbol) {

    def apply(rootPackage: Name): CompilationUnit = {
      val name = rootPackage.toString()
      Java(s"""package $name;
                import ks.common.view.*;
                public class $subclass extends $parent {
                  public $subclass ($model element) {
                    super(element);
                  }
                }
             """).compilationUnit()
    }

    val semanticType : Type = 'RootPackage =>: typ
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
    * Class for constructing Tableau from n BuildablePile.
    *
    * param n             number of buildable piles to create
    * @param nAsType      type of BuildablePile within the semantic type 'Tableau ('Valid :&: typ)
    */
  class NBuildablePileTableau(n: Int, nAsType: Type) {
    def apply(): Tableau = {
      val t = new Tableau()
      for (_ <- 1 to n)
        t.add(new BuildablePile())
      t
    }

    val semanticType: Type = 'Tableau ('Valid :&: nAsType :&: 'BuildablePile)
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
    * Class for constructing Foundation from n Piles.
    *
    * @param n            number of piles to create
    * @param nAsType      type of Pile within the semantic type 'Foundation ('Valid :&: typ)
    */
  class NPileFoundation(n: Int, nAsType: Type) {
    def apply(): Foundation = {
      val t = new Foundation()
      for (_ <- 1 to n)
        t.add(new Pile())
      t
    }

    val semanticType: Type = 'Foundation ('Valid :&: nAsType :&: 'Pile)
  }

  /**
    * Class for constructing Reserve from n Piles.
    *
    * @param n            number of piles to create
    * @param nAsType      type of Pile within the semantic type 'Reserve ('Valid :&: typ)
    */
  class NPileReserve(n: Int, nAsType: Type) {
    def apply(): Reserve = {
      val t = new Reserve()
      for (_ <- 1 to n)
        t.add(new Pile())
      t
    }

    val semanticType: Type = 'Reserve ('Valid :&: nAsType :&: 'Pile)
  }

  /**
    * Specialized combinators for common scenarios of tableaus from piles and columns.
    */
  @combinator object EightColumnTableau extends NColumnTableau(8, 'Eight)
  @combinator object FourColumnTableau extends NColumnTableau(4,  'Four)
  @combinator object EightPileTableau extends NPileTableau(8, 'Eight)
  @combinator object FourPileTableau extends NPileTableau(4, 'Four)

  /**
    * Canonical Foundation of four Pile objects.
    */
  @combinator object FourPileFoundation extends NPileFoundation(4, 'Four)


  /**
    * Combinator for creating a one-deck stock
    */
  @combinator object SingleDeckStock {
    def apply(): Stock = new Stock()

    val semanticType: Type = 'Stock ('Valid :&: 'One :&: 'Deck)
  }

  /**
    * Combinator for creating a two-deck stock
    */
  @combinator object TwoDeckStock {
    def apply(): Stock = new Stock(2)

    val semanticType: Type = 'Stock ('Valid :&: 'Two :&: 'Deck)
  }

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
    * @param name         Name of model element to create. Will be prefaced by "field..." in final name
    * @param modelType    Class name of model element
    * @param viewType     Class name of view widget
    * @param num          number of elements/widgets to create
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
    * @param viewName    Name of widget variable name storing these view widgets
    * @param contName    Name of container holding these view widgets
    * @return
    */
  def controllerGen(viewName:String, contName:String) : Seq[Statement] = {
     Java(s"""
           |$viewName.setMouseMotionAdapter (new SolitaireMouseMotionAdapter (this));
           |$viewName.setUndoAdapter (new SolitaireUndoAdapter (this));
           |$viewName.setMouseAdapter (new $contName (this, $viewName));
           |""".stripMargin).statements()

  }

  /**
    * Convenient function to iterate over views for a given container (i.e., Reserve or Tableau)
    * and assign controllers based on the given type.
    *
    * Iterates over variable 'j' just to be different ;)
    *
    * @param cont        Actual container holding these widgets
    * @param viewName    The name of the variable name being index over (by j)
    * @param contName    Name of container holding these view widgets
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
    * @param cont        Actual container holding these widgets
    * @param modelName   The name of the variable name holding the element
    * @param viewName    The name of the variable name holding the widget
    * @param typ         The name of the Element class
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
    * Generate statements that constructs a single Deck (but no view), adds it to
    * the model and initializes it to be ready.
    *
    * @param modelName    name of model element to create
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
    * Generate statements that constructs a single Deck (and associated Widget view), adds it to
    * the model and initializes it to be ready. Use this for solitaire variations that need
    * a DeckView to be present.
    *
    * @param modelName    name of model element to create
    */
  def deckGenWithView (modelName:String, viewName:String):Seq[Statement] = {
    deckGen(modelName) ++ Java(s"""$viewName = new DeckView($modelName);""".stripMargin).statements()
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

  /**
    * Place single item, drawn from the given container and of type 'label'.
    *
    * Invoke as, example:  layout_place_one(lay, stock, Layout.Stock, Java("deckView").name(), 97)
    *
    * Feels like too many parameters...
    * @return
    */
  def layout_place_one (lay: Layout, c: Container, label:String, view:Name, height:Int): Seq[Statement] = {
    val itd = lay.placements(label, c, height)
    val r = itd.next()

    Java(s"""|$view.setBounds(${r.x}, ${r.y}, ${r.width}, ${r.height});
             |addViewWidget($view);
       """.stripMargin).statements()
  }

  /**
    * Place single item, drawn from the given container and of type 'label'.
    *
    * Invoke as, example:  layout_place_one(lay, stock, Layout.Stock, Java("deckView").name(), 97)
    *
    * Feels like too many parameters...
    * @return
    */
  def layout_place_one_expr (lay: Layout, c: Container, label:String, view:Expression, height:Int): Seq[Statement] = {
    val itd = lay.placements(label, c, height)
    val r = itd.next()

    Java(s"""|$view.setBounds(${r.x}, ${r.y}, ${r.width}, ${r.height});
             |addViewWidget($view);
       """.stripMargin).statements()
  }

  /**
    * Place multiple items, drawn from the given container and of type 'label'.
    *
    * Invoke as, example:  layout_place_many(lay, tableau, Layout.Tableau, Java("fieldColumnViews").name(), 13*97)
    *
    * Feels like too many parameters...
    * @return
    */
  def layout_place_many (lay: Layout, c:Container, label:String, view:Name, height:Int): Seq[Statement] = {
    // this can all be retrieved from the solitaire domain model by
    // checking if a tableau is present, then do the following, etc... for others
    val itt = lay.placements(label, c, height)
    var stmts = Java("").statements()     // MUST BE SOME BETTER WAY OF GETTING EMPTY STATEMENTS
    while (itt.hasNext) {
      val r = itt.next()

      val s = Java(s"""
                      |$view[${r.idx}].setBounds(${r.x}, ${r.y}, ${r.width}, ${r.height});
                      |addViewWidget($view[${r.idx}]);
               """.stripMargin).statements()

      stmts = stmts ++ s
    }
    stmts
  }

  /**
    * Place multiple elements of a container with custom coordinates.
    * @param layout Holds all containers in the layout.
    * @param container Such as the Reserve or Tableau.
    * @param view The real field name of the View associated.
    * @param x List of x coordinates.
    * @param y List of y coordinates.
    * @param height Usually the height of the card or column.
    * @return Generated statements placing the View in the game.
    *
    * @author jabortell
    */
  def layout_place_custom(layout: Layout, container: Container, view: Name, x: Array[Int], y: Array[Int], height: Int) = {
    val itt = layout.customPlacements(container, x, y, height)
    var statements = Java("").statements()
    while (itt.hasNext) {
      val r = itt.next()

      val s = Java(
        s"""
           |$view[${r.idx}].setBounds(${r.x}, ${r.y}, ${r.width}, ${r.height});
           |addViewWidget($view[${r.idx}]);
           """.stripMargin).statements()

      statements = statements ++ s
    }
    statements
  }

  /**
    * Define fields for Deck (and DeckView) if a stock is defined in the Solitaire domain.
    *
    * Properly handles MultiDeck and Deck. Constructs with names 'deck' and 'deckView'
    *
    * @param s   Solitaire instance
    * @return
    */
  def deckGen(s:Solitaire) : Seq[FieldDeclaration] = {
    val stock = s.getStock
    if (stock == null) {
      return Seq.empty
    }

    val decks =
      if (stock.getNumDecks > 1) {
        Java("public MultiDeck deck;").classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])
      } else {
        Java("public Deck deck;").classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])
      }
    val deckViews = Java("DeckView deckView;").classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])

    decks ++ deckViews
  }


  /**
    * Variations that wish to take advantage of automoves can define this method in their base class.
    */
  @combinator object AutoMoveSequence {
    def apply(pkgName: Name, name: SimpleName): Seq[Statement] = {
      Java(s"""(($pkgName.$name)theGame).tryAutoMoves();""").statements()
    }
    val semanticType: Type =
      'RootPackage =>: 'NameOfTheGame =>: 'AutoMoves
  }
}
