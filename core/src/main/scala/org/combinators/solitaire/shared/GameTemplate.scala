package org.combinators.solitaire.shared

import com.github.javaparser.ast.body.{FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Expression, Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.{CompilationUnit, ImportDeclaration}
import de.tu_dortmund.cs.ls14.cls.interpreter.{ReflectedRepository, combinator}
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.types.syntax._
import org.combinators.solitaire.shared
import org.combinators
import de.tu_dortmund.cs.ls14.twirl.Java

import scala.collection.JavaConverters._

// domain
import domain._
import domain.ui._

trait GameTemplate extends Base with Controller with SemanticTypes with WinningLogic with DealLogic  {

  // domain model elements for game defined here...
  lazy val tableauType = Variable("TableauType")

  /**
    * Process the solitaire domain object itself to identify combinators to add.
    */
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    var updated = super.init(gamma, s)

    /** handles generating default (empty) automoves. */
    if (!s.hasAutoMoves) {
      updated = updated
        .addCombinator(NoAutoMovesAvailable)
    } else {
      updated = updated
        .addCombinator(AutoMoveSequence)
    }

    /** Get controllers defined based on solitaire domain. */
    val ui = new UserInterface(s)

    val els_it = ui.controllers
    while (els_it.hasNext) {
      val el = els_it.next()
      val elt:Constructor = Constructor(el)

      // Each of these controllers are expected in the game.
      updated = updated
        .addCombinator (new WidgetController(elt))
        .addCombinator (new ControllerNaming(elt))
    }

    updated
  }


  /**
    * Every solitaire variation belongs in its own package. Take name of game
    * and make it lowercase to conform to Java
    */
  class DefineRootPackage(s:Solitaire) {
    def apply: Name = {
      val name = s.name.toLowerCase
      Java("org.combinators.solitaire." + name).name()
    }
    val semanticType: Type = packageName
  }

  /**
    * Every solitaire variation has its own subclass with given name
    */
  class DefineNameOfTheGame(s:Solitaire) {
    def apply: SimpleName = Java(s.name).simpleName()

    val semanticType: Type = variationName
  }

  /** Do not use @combinator annotation since must wait until domain realized. */
  object NoAutoMovesAvailable {
    def apply(): Seq[Statement] = Seq.empty

    val semanticType: Type = game(game.autoMoves)
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

    val semanticType : Type = packageName =>: typ
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

    val semanticType : Type = packageName =>: typ
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
    val semanticType: Type =
      game(game.model) =>:
        game(game.view) =>:
        game(game.control) =>:
        game(game.deal) =>:
        game(initialized)
  }

  /**
    * Within a solitaire plugin, one needs to construct model elements that are added to the
    * model, and then view widgets to realize these model elements on the screen. This function
    * adds array of fields for both model elements and view widgets.
    *
    * Note: prefix is used when constructing these elements to ensure unique names.
    *
    * @param modelType    Class name of model element
    * @param num          number of elements/widgets to create
    */
  def fieldGen(modelType:String, num:Int):Seq[FieldDeclaration] = {
    Java(s"""
            |public static final String field${modelType}sPrefix = "$modelType";
            |public $modelType[] field${modelType}s = new $modelType[$num];
            |public ${modelType}View[] field${modelType}Views = new ${modelType}View[$num];
            |""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])
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

  def processFieldGen(modelType:String, name:String, num:Int):Seq[FieldDeclaration] = {
    Java(s"""
            |public $modelType[] $name = new $modelType[$num];
            |public static final String ${name}Prefix = "$name";
            |public ${modelType}View[] ${name}View = new ${modelType}View[$num];
            |""".stripMargin).classBodyDeclarations().map(_.asInstanceOf[FieldDeclaration])
  }

  /** Process Solitaire domain model to construct game(game.fields). */
  class ProcessFields (sol:Solitaire) {
    def apply: Seq[FieldDeclaration] = {

      var fields = Java(s"""
             |IntegerView scoreView;
             |IntegerView numLeftView;
             """.stripMargin).fieldDeclarations()

      for (containerType:ContainerType <- sol.containers.keySet.asScala) {
        val container = sol.containers.get(containerType)
        container match {

          case d: Stock =>
            fields = fields ++ deckFieldGen(d)

          case _ =>
            val tpe: String = container.types().next

            fields = fields ++ processFieldGen(tpe, containerType.getName, container.size())
        }
      }

      fields
    }

    val semanticType: Type = game(game.fields)
  }

  /** Process Solitaire domain model to construct game(game.control). */
  class ProcessControl (sol:Solitaire) {
    def apply: Seq[Statement] = {
      var stmts: Seq[Statement] = Seq.empty
      for (containerType:ContainerType <- sol.containers.keySet.asScala) {
        val container = sol.containers.get(containerType)
        container match {

          case d: Stock =>
            if (sol.isVisible(d)) {  //   (!d.isInvisible) {
              stmts = stmts ++ controllerGen(s"${containerType.getName}View", "DeckController")
            }

          case _ =>
            val tpe: String = container.types().next
            stmts = stmts ++ loopControllerGen(container, s"${containerType.getName}View", s"${tpe}Controller")
        }
      }

      stmts
    }

    val semanticType: Type = game(game.control)
  }

  /** Process Solitaire domain model to construct game(game.view). */
  class ProcessView (sol:Solitaire) {
    def apply: Seq[Statement] = {
      var stmts: Seq[Statement] = Seq.empty
      for (containerType:ContainerType <- sol.containers.keySet.asScala) {
        val container = sol.containers.get(containerType)
        val name = containerType.getName
        container match {

          case d: Stock => {
            // Need to keep these "redundant braces" to ensure we don't fall through to next case
            if (sol.isVisible(d)) { ///  (!d.isInvisible) {
              stmts = stmts ++ Java(s"""${name}View = new DeckView($name);""").statements()
              stmts = stmts ++ layout_place_one(sol, d, Java(s"${name}View").name())
            }
          }

          case _ =>
            stmts = stmts ++ layout_place_it(sol, container, Java(s"${name}View").name())
        }
      }

      stmts
    }

    val semanticType: Type = game(game.view)
  }

  /** Process Solitaire domain model to construct game(game.model). */
  class ProcessModel (sol:Solitaire) {
    def apply: Seq[Statement] = {
      var stmts: Seq[Statement] = Seq.empty
      for (containerType:ContainerType <- sol.containers.keySet.asScala) {
        val container = sol.containers.get(containerType)
        val name = containerType.getName

        container match {

          case d: Stock =>
            stmts = stmts ++ deckGen(name, d)

          case _ =>
            val tpe: String = container.types().next

            stmts = stmts ++ loopConstructGen(container, name, name + "View", tpe)
        }
      }

      stmts
    }

    val semanticType: Type = game(game.model)
  }

  /**
    * Generate statements that constructs a single Deck (but no view), adds it to
    * the model and initializes it to be ready. Handles MultiDeck as needed.
    *
    * @param modelName    name of model element to create
    */
  def deckGen (modelName:String, stock:Container):Seq[Statement] = {

    val decks =
      if (stock.size > 1) {
        Java(
          s"""|// Multi-decks are constructed from stock size.
              |$modelName = new MultiDeck ("$modelName", ${stock.size});
              |""".stripMargin).statements()

      } else {
        Java(
          s"""|// Single deck instantiated as is
              |$modelName = new Deck ("$modelName");
              |""".stripMargin).statements()
      }

    decks ++ Java(s"""|// Basic start of pretty much any solitaire game that requires a deck.
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
  def deckGenWithView (modelName:String, viewName:String, stock:Container):Seq[Statement] = {
    deckGen(modelName, stock) ++ Java(s"""$viewName = new DeckView($modelName);""".stripMargin).statements()
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
      packageName =>: variationName =>:
        game(game.imports) =>:
        game(game.fields) =>:
        game(game.methods)=>:
        game(initialized) =>:
        game(game.winCondition) =>:
        game(complete)
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
      packageName =>: variationName =>:
        game(game.imports) =>:
        game(game.fields) =>:
        game(game.methods :&: game.availableMoves) =>:
        game(initialized) =>:
        game(game.winCondition) =>:
        game(complete :&: game.solvable)
  }

  def layout_place_it (s:Solitaire, c:Container, view:Name): Seq[Statement] = {
    // this can all be retrieved from the solitaire domain model by
    // checking if a tableau is present, then do the following, etc... for others
    // c.placements().asScala.flatMap {
    s.placements(c).asScala.flatMap {
      r => Java(s"""
                   |$view[${r.idx}].setBounds(${r.x}, ${r.y}, ${r.width}, ${r.height});
                   |addViewWidget($view[${r.idx}]);
            """.stripMargin).statements()
    }.toSeq
  }

  /** Used when you know in advance there is only one widget and you've chosen not to use array. */
  def layout_place_one (s:Solitaire, c:Container, view:Name): Seq[Statement] = {
    // this can all be retrieved from the solitaire domain model by
    // checking if a tableau is present, then do the following, etc... for others
    //c.placements().asScala.flatMap {
    s.placements(c).asScala.flatMap {
      r => Java(s"""
                   |$view.setBounds(${r.x}, ${r.y}, ${r.width}, ${r.height});
                   |addViewWidget($view);
            """.stripMargin).statements()
    }.toSeq
  }

  /**
    * Place single item, drawn from the given container and of type 'label'.
    *
    * Invoke as, example:  layout_place_one(lay, stock, Layout.Stock, Java("deckView").name(), 97)
    *
    * Feels like too many parameters...
    * @return
    */
  def layout_place_it_expr (s:Solitaire, c: Container, view:Expression): Seq[Statement] = {
   //c.placements().asScala.flatMap {
    s.placements(c).asScala.flatMap {
      r => Java(s"""|$view.setBounds(${r.x}, ${r.y}, ${r.width}, ${r.height});
                    |addViewWidget($view);
            """.stripMargin).statements()
    }.toSeq
  }

  /**
    * Define fields for Deck (and DeckView) if a stock is defined in the Solitaire domain.
    *
    * Properly handles MultiDeck and Deck. Constructs with names 'deck' and 'deckView'
    *
    * TODO: These BURY the names of the fields; should be externally visible.
    * HACK
    * @return
    */
  def deckFieldGen(stock:Container) : Seq[FieldDeclaration] = {

    val decks =
      if (stock.size() > 1) {
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
  object AutoMoveSequence {
    def apply(pkgName: Name, name: SimpleName): Seq[Statement] = {
      Java(s"""(($pkgName.$name)theGame).tryAutoMoves();""").statements()
    }
    val semanticType: Type =
      packageName =>: variationName =>: game(game.autoMoves)
  }

  /**
    * Helper code for various needs are placed in here as static methods.
    */
  @combinator object CreateHelper {
    def apply(pkgName: Name, name: SimpleName, methods:Seq[MethodDeclaration]): CompilationUnit= {

      combinators.java.ConstraintHelper.render(pkgName, name, methods).compilationUnit()
    }
    val semanticType: Type =
      packageName =>: variationName =>: constraints(constraints.methods) =>: constraints(complete)
  }



}
