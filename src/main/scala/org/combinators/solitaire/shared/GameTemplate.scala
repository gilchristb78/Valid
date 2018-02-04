package org.combinators.solitaire.shared

import com.github.javaparser.ast.body.{BodyDeclaration, FieldDeclaration, MethodDeclaration}
import com.github.javaparser.ast.expr.{Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.{CompilationUnit, ImportDeclaration}
import org.combinators.cls.interpreter.{ReflectedRepository, combinator}
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.shared
import org.combinators
import org.combinators.templating.twirl.Java


// domain
import domain._
import domain.ui._

import scala.collection.JavaConverters._

trait GameTemplate extends Base with Controller with Initialization with SemanticTypes with WinningLogic with DealLogic  {

  /**
    * Process the solitaire domain object itself to identify combinators to add.
    */
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    var updated = super.init(gamma, s)

    // handle game
    if (s.isSolvable) {
      updated = updated
        .addCombinator(new MainSolitaireSolvable(s))
    } else {
      updated = updated
        .addCombinator(new MainGame(s))
    }
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


  // construct specialized classes based on registered domain elements
  def generateExtendedClasses[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    var updated = gamma
    // Model classes
    for (e <- s.domainElements().asScala) {
      val parent: String = e.getClass.getSuperclass.getSimpleName
      val name: String = e.getClass.getSimpleName
      updated = updated
        .addCombinator(new ExtendModel(parent, name, classes(name)))
    }

    // View classes
    for (v <- s.domainViews().asScala) {
      val parent: String = v.parent
      val name: String = v.name
      val model:String = v.model
      updated = updated
        .addCombinator(new ExtendView(parent, name, model, classes(name)))
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

  class ExtendModel(parent: String, subclass: String, typ:Constructor) {

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

  class ExtendView(parent: String, subclass: String, model: String, typ:Constructor) {

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


//  /**
//    * Generate statements that constructs a single Deck (and associated Widget view), adds it to
//    * the model and initializes it to be ready. Use this for solitaire variations that need
//    * a DeckView to be present.
//    *
//    *
//    */
//  def deckGenWithView (modelName:String, viewName:String, stock:Container):Seq[Statement] = {
//    deckGen(modelName, stock) ++ Java(s"""$viewName = new DeckView($modelName);""".stripMargin).statements()
//  }

  /**
    * Primary combinator which constructs the subclass of Solitaire to represent the
    * class for a variation.
    */
  class MainGame(sol:Solitaire) {

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
  class MainSolitaireSolvable(sol:Solitaire) {

    def apply(rootPackage: Name,
              nameParameter: SimpleName,
              extraImports: Seq[ImportDeclaration],
              extraFields: Seq[FieldDeclaration],
              extraMethods: Seq[MethodDeclaration],
              initializeSteps: Seq[Statement],
              winParameter: Seq[Statement]): CompilationUnit = {

      val comp = shared.java.GameTemplate
        .render(
          rootPackage = rootPackage,
          extraImports = extraImports,
          nameParameter = nameParameter,
          extraFields = extraFields,
          extraMethods = extraMethods,
          winParameter = winParameter,
          initializeSteps = initializeSteps)
        .compilationUnit()

      // introspect from solitaire domain model
      if (sol.isSolvable) {
        val clazz = comp.getClassByName(nameParameter.toString).get
        val solvable = Java("SolvableSolitaire").tpe.asClassOrInterfaceType()
        clazz.getImplementedTypes.add(solvable)
      }

      comp
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



  /**
    * Place single item, drawn from the given container and of type 'label'.
    *
    * Invoke as, example:  layout_place_one(lay, stock, Layout.Stock, Java("deckView").name(), 97)
    *
    * Feels like too many parameters...
    * @return
    */
//  def layout_place_it_expr (s:Solitaire, c: Container, view:Expression): Seq[Statement] = {
//   //c.placements().asScala.flatMap {
//    s.placements(c).asScala.flatMap {
//      r => Java(s"""|$view.setBounds(${r.x}, ${r.y}, ${r.width}, ${r.height});
//                    |addViewWidget($view);
//            """.stripMargin).statements()
//    }.toSeq
//  }


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
    def apply(pkgName: Name, name: SimpleName, methods:Seq[BodyDeclaration[_]]): CompilationUnit= {

      combinators.java.ConstraintHelper.render(pkgName, name, methods).compilationUnit()
    }
    val semanticType: Type =
      packageName =>: variationName =>: constraints(constraints.methods) =>: constraints(complete)
  }



}
