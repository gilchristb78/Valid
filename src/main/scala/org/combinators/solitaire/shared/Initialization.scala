package org.combinators.solitaire.shared

import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.expr.Name
import com.github.javaparser.ast.stmt.Statement
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.templating.twirl.Java
import domain._
import org.combinators.solitaire.shared
import org.combinators.cls.types.syntax._

import scala.collection.JavaConverters._

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
    * @param actual      Actual iterator of Element objects from the domain model
    * @param typ         The name of the Element class
    */
  def loopConstructGen(cont: Container, modelName: String, actual:Iterator[Element], typ:String): Seq[Statement] = {
    val nc = cont.size()
    val viewName = modelName + "View"

    // In nearly EVERY case, the constructor is clean, but it really is up to the individual element.
    // We should actually unroll the loop and process the iterator fully. Leave for another time
    val element:Element = actual.next()

    // TOTAL HACK: TODO: FIX UP WITH CLEANER INSTANTIATION
    val constructor:String = element match  {
      case fp:FanPile => {
        val num:Int = fp.numToShow
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

      var fields = Java(s"""
                           |IntegerView scoreView;
                           |IntegerView numLeftView;
             """.stripMargin).fieldDeclarations()

      for (containerType:ContainerType <- sol.structure.keySet.asScala) {
        val container = sol.structure.get(containerType)
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
     // var stmts: Seq[Statement] = Seq.empty

      sol.containers.asScala.flatMap {
        container => {
          val containerType = container.`type`
          val part: Seq[Statement] = container match {
            case d: Stock =>
              if (sol.isVisible(d)) {
                controllerGen(s"${containerType.getName}View", "DeckController")
              } else {
                Seq.empty
              }

            case _ =>
              val tpe: String = container.types().next
              loopControllerGen(container, s"${containerType.getName}View", s"${tpe}Controller")
          }

          part
        }
      }.toSeq

//      for (containerType:ContainerType <- sol.structure.keySet.asScala) {
//        val container = sol.structure.get(containerType)
//        container match {
//
//          case d: Stock =>
//            if (sol.isVisible(d)) {  //   (!d.isInvisible) {
//              stmts = stmts ++ controllerGen(s"${containerType.getName}View", "DeckController")
//            }
//
//          case _ =>
//            val tpe: String = container.types().next
//            stmts = stmts ++ loopControllerGen(container, s"${containerType.getName}View", s"${tpe}Controller")
//        }
//      }
//
//      stmts
    }

    val semanticType: Type = game(game.control)
  }

  /** Process Solitaire domain model to construct game(game.view). */
  class ProcessView (sol:Solitaire) {
    def apply: Seq[Statement] =
      sol.containers.asScala.flatMap {
        container => {
         val name = container.`type`.getName
         container match {
            case d: Stock =>
              if (sol.isVisible(d)) {
                Java(s"""${name}View = new DeckView($name);""").statements() ++
                  layout_place_one(sol, d, Java(s"${name}View").name())
              } else Seq.empty
            case _ =>
              layout_place_it(sol, container, Java(s"${name}View").name())
          }
        }
      }.toSeq

    val semanticType: Type = game(game.view)
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

  /** Process Solitaire domain model to construct game(game.model). */
  class ProcessModel (sol:Solitaire) {
    def apply: Seq[Statement] = {
      var stmts: Seq[Statement] = Seq.empty
      for (containerType:ContainerType <- sol.structure.keySet.asScala) {
        val container = sol.structure.get(containerType)
        val name = containerType.getName

        container match {

          case d: Stock =>
            stmts = stmts ++ deckGen(name, d)

          case _ =>
            val tpe: String = container.types().next
            stmts = stmts ++ loopConstructGen(container, name, container.iterator().asScala, tpe)
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
        Java("public MultiDeck deck;").fieldDeclarations()
      } else {
        Java("public Deck deck;").fieldDeclarations()
      }
    val deckViews = Java("DeckView deckView;").fieldDeclarations()

    decks ++ deckViews
  }


}
