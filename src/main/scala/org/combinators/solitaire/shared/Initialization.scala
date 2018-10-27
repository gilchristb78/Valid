package org.combinators.solitaire.shared

import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.expr.Name
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.templating.twirl.Java
import org.combinators.solitaire.shared
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.domain._

trait Initialization extends SemanticTypes {
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
  def loopControllerGen(cont: Seq[Element], viewName : String, contName:String): Seq[Statement] = {
    val nc = cont.size
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
    * @param ct          Actual container holding these widgets
    * @param modelName   The name of the variable name holding the element
    * @param actual      Actual iterator of Element objects from the domain model
    * @param typ         The name of the Element class
    */
  def loopConstructGen(ct: ContainerType, modelName: String, actual:Seq[Element], typ:String): Seq[Statement] = {
    val nc = actual.size
    val viewName = modelName + "View"

    // In nearly EVERY case, the constructor is clean, but it really is up to the individual element.
    // We should actually unroll the loop and process the iterator fully. Leave for another time
    val element:Element = actual.head

    // TOTAL HACK: TODO: FIX UP WITH CLEANER INSTANTIATION
    val constructor:String = element match  {
      case FanPile(num) => {
        s"""new ${typ}View($num, $modelName[j])"""
      }

      case _ => s"""new ${typ}View($modelName[j])"""
    }

    Java(
      s"""
         |for (int j = 0; j < $nc; j++) {
         |  $modelName[j] = new $typ(${modelName}Prefix + (j+1));
         |  addModelElement ($modelName[j]);
         |  $viewName[j] = $constructor;
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

      var defaultFields = Java(s"""|IntegerView scoreView;
                                   |IntegerView numLeftView;
                                """.stripMargin).fieldDeclarations()

      val fields = sol.structure.flatMap { case (ct, elements) =>
          ct match {
            case StockContainer => deckFieldGen(elements)
            case _ =>  processFieldGen(elements.head.name, ct.name, elements.size)
          }
      }


      defaultFields ++ fields
    }

    val semanticType: Type = game(game.fields)
  }

  /** Process Solitaire domain model to construct game(game.control). */
  class ProcessControl (sol:Solitaire) {
    def apply: Seq[Statement] = {
     // var stmts: Seq[Statement] = Seq.empty

      sol.structure.flatMap { case (ct, elements) =>
        ct match {
        case StockContainer =>
          if (sol.layout.isVisible(ct)) {
            controllerGen("deckView", "DeckController")
          } else {
            Seq.empty
          }

        case _ =>
          val tpe: String = elements.head.name
          loopControllerGen(elements, s"${ct.name}View", s"${tpe}Controller")
        }
      }.toSeq
    }

    val semanticType: Type = game(game.control)
  }

  /** Process Solitaire domain model to construct game(game.view). */
  class ProcessView (sol:Solitaire) {
    def apply: Seq[Statement] =
      sol.structure.flatMap { case (ct, elements) =>
        ct match {
          case StockContainer =>
            if (sol.layout.isVisible(ct)) {
              Java(s"""deckView = new DeckView(deck);""").statements() ++
                layout_place_one(sol, ct, Java("deckView").name())
            } else Seq.empty

          case _ =>
            layout_place_it(sol, ct, Java(s"${ct.name}View").name())
        }
      }.toSeq

    val semanticType: Type = game(game.view)
  }

  def layout_place_it (s:Solitaire, ct:ContainerType, view:Name): Seq[Statement] = {
    // this can all be retrieved from the solitaire domain model by
    // checking if a tableau is present, then do the following, etc... for others
    // c.placements().asScala.flatMap {
   s.layout.places(ct).zipWithIndex.flatMap { case (r, idx) =>
          Java(s"""
                   |$view[$idx].setBounds(${r.x}, ${r.y}, ${r.width}, ${r.height});
                   |addViewWidget($view[$idx]);
            """.stripMargin).statements()
    }
  }

  /** Used when you know in advance there is only one widget and you've chosen not to use array. */
  def layout_place_one (s:Solitaire, ct:ContainerType, view:Name): Seq[Statement] = {
    // this can all be retrieved from the solitaire domain model by
    // checking if a tableau is present, then do the following, etc... for others
    //c.placements().asScala.flatMap {
    s.layout.places(ct).zipWithIndex.flatMap { case (r, idx) =>
         Java(s"""|$view.setBounds(${r.x}, ${r.y}, ${r.width}, ${r.height});
                  |addViewWidget($view);
              """.stripMargin).statements()
    }
  }

  /** Process Solitaire domain model to construct game(game.model). */
  class ProcessModel (sol:Solitaire) {
    def apply: Seq[Statement] = {

      sol.structure.flatMap { case (ct, elements) =>
        ct match {
          case StockContainer => deckGen("deck", elements)
          case _ => loopConstructGen(ct, ct.name, elements, elements.head.name)
        }
      }.toSeq
    }

    val semanticType: Type = game(game.model)
  }

  /**
    * Generate statements that constructs a single Deck (but no view), adds it to
    * the model and initializes it to be ready. Handles MultiDeck as needed.
    *
    * @param modelName    name of model element to create
    */
  def deckGen (modelName:String, stock:Seq[Element]):Seq[Statement] = {

    // will be a single deck in the stock
    val decks = stock.head match {
      case Stock(n) =>
        if (n > 1) {
          Java(
            s"""|// Multi-decks are constructed from stock size.
                |$modelName = new MultiDeck ("$modelName", $n);
                |""".stripMargin).statements

        } else {
          Java(
            s"""|// Single deck instantiated as is
                |$modelName = new Deck ("$modelName");
                |""".stripMargin).statements
        }

      case _ => Seq.empty
    }

    decks ++ Java(s"""|// Basic start of pretty much any solitaire game that requires a deck.
                      |int seed = getSeed();
                      |$modelName.create(seed);
                      |addModelElement ($modelName);
                      |""".stripMargin).statements()
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
  def deckFieldGen(stock:Seq[Element]) : Seq[FieldDeclaration] = {

    val decks =
      if (stock.size > 1) {
        Java("public MultiDeck deck;").fieldDeclarations()
      } else {
        Java("public Deck deck;").fieldDeclarations()
      }
    val deckViews = Java("DeckView deckView;").fieldDeclarations()

    decks ++ deckViews
  }


}
