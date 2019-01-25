package org.combinators.solitaire.shared

import com.github.javaparser.ast.body.{BodyDeclaration, FieldDeclaration, MethodDeclaration, TypeDeclaration}
import com.github.javaparser.ast.expr.{Name, SimpleName}
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.ast.{CompilationUnit, ImportDeclaration}
import org.combinators.cls.interpreter.{ReflectedRepository, combinator}
import org.combinators.cls.types._
import org.combinators.cls.types.syntax._
import org.combinators.solitaire.shared
import org.combinators
import org.combinators.solitaire.domain._
import org.combinators.templating.twirl.Java


trait GameTemplate extends Base with Controller with Initialization with SemanticTypes with WinningLogic with DealLogic  {

  /**
    * Process the solitaire domain object itself to identify combinators to add.
    */
  override def init[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    var updated = super.init(gamma, s)

    // handle game
    if (s.solvable) {
      updated = updated
        .addCombinator(new MainSolitaireSolvable(s))
    } else {
      updated = updated
        .addCombinator(new MainGame(s))
    }
    /** handles generating default (empty) automoves. */
    if (!s.autoMoves) {
      updated = updated
        .addCombinator(NoAutoMovesAvailable)
    } else {
      updated = updated
        .addCombinator(AutoMoveSequence)
    }

    /** Get controllers defined based on solitaire domain. */

    /** For all visible containers, returns HEAD of each element set, since types are unique to a set */
//    val visibleElements = s.structure.collect { case (ct,els) if s.layout.isVisible(ct) => els.head }
//
//    visibleElements.foreach(e => {
//        val elt:Constructor = Constructor(e.name)
//        updated = updated
//          .addCombinator (new WidgetController(elt))
//          .addCombinator (new ControllerNaming(elt))
//      })

    s.structure.collect { case (ct,els) if s.layout.isVisible(ct) => {
      val name = ct match {
        case StockContainer => "Deck"
        case _ => els.head.name
      }
      val elt:Constructor = Constructor(name)
      updated = updated
        .addCombinator (new WidgetController(elt))
        .addCombinator (new ControllerNaming(elt))
    }}


    updated
  }


  def modelNameFromElement (e:Element): String = e.name
  def viewNameFromElement (e:Element): String = e.name + "View"

  /**
    * Any variation that seeks to add their own specialized elements must override these methods properly.
    * @param e
    * @return
    */
  // override as needed in your own own specialized trait. I.e. "AcesUpPile" -> "PileView"
  def baseViewNameFromElement (e:Element): String = viewNameFromElement(e)
  // override as needed in your own own specialized trait. I.e. "AcesUpPile" -> "Pile"
  def baseModelNameFromElement (e:Element): String = modelNameFromElement(e)

  // construct specialized classes based on registered domain elements
  def generateExtendedClasses[G <: SolitaireDomain](gamma : ReflectedRepository[G], s:Solitaire) : ReflectedRepository[G] = {
    var updated = gamma
    // Model classes
    for (e <- s.specializedElements) {
      val parent: String = baseModelNameFromElement(e)    // e.getClass.getSuperclass.getSimpleName
      val name: String = e.name
      updated = updated
        .addCombinator(new ExtendModel(parent, name, classes(name), e.modelMethods, e.modelImports))

      updated = updated
        .addCombinator(new ExtendView(baseViewNameFromElement(e), viewNameFromElement(e), name, classes(viewNameFromElement(e)), e.viewMethods, e.viewImports))
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

  class ExtendModel(parent: String, subclass: String, typ:Constructor, modelMethods:Seq[BodyDeclaration[_]] = Seq.empty, modelImports:Seq[ImportDeclaration] =  Seq.empty) {

    def apply(rootPackage: Name): CompilationUnit = {
      val name = rootPackage.toString()
      val comp = Java(s"""package $name;
                import ks.common.model.*;
                public class $subclass extends $parent {
		  public $subclass (String name) {
		    super(name);
		  }
		}
	     """).compilationUnit()

      val clazz  = comp.getTypes.get(0)
      modelMethods.foreach {m => clazz.addMember(m) }
      modelImports.foreach { i => comp.addImport(i) }
      comp
    }

    val semanticType : Type = packageName =>: typ
  }

  class ExtendView(parent: String, subclass: String, model: String, typ:Constructor, viewMethods:Seq[BodyDeclaration[_]] =  Seq.empty, viewImports:Seq[ImportDeclaration] =  Seq.empty) {

    def apply(rootPackage: Name): CompilationUnit = {
      val name = rootPackage.toString()
      val comp  = Java(s"""package $name;
                import ks.common.view.*;
                public class $subclass extends $parent {
                  public $subclass ($model element) {
                    super(element);
                  }
                }
             """)compilationUnit()
      val clazz  = comp.getTypes.get(0)
      viewMethods.foreach {m => clazz.addMember(m) }
      viewImports.foreach {i => comp.addImport(i) }
      comp
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


      val comp = shared.java.GameTemplate
        .render(rootPackage = rootPackage,
          nameParameter = nameParameter,
          winParameter = winParameter,
          initializeSteps = initializeSteps)
        .compilationUnit()

      // add extra imports, fields and methods
      val clazz:TypeDeclaration[_] = comp.getTypes.get(0)

      extraImports.foreach { i => comp.addImport(i) }
      extraMethods.foreach { m => clazz.addMember(m) }
      extraFields.foreach { f => clazz.addMember(f) }

      // Standard size of GUI is Dimension(769, 635). If bigger, add a method
      val (width, height) = sol.layout.minimumSize
      if (width > 769 || height > 635) {
        def max(x:Int, y:Int) = { if (x > y) x else y }
        Java(
          s"""
             |@Override
             |public Dimension getPreferredSize() {
             |	// default starting dimensions...
             |  return new Dimension(${max(769, width)}, ${max(635, height)});
             |}""".stripMargin).methodDeclarations()
          .foreach { m => clazz.addMember(m) }
      }

      comp
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
          nameParameter = nameParameter,
          winParameter = winParameter,
          initializeSteps = initializeSteps)
        .compilationUnit()

      // add extra imports, fields and methods
      val clazz:TypeDeclaration[_] = comp.getTypes.get(0)

      extraImports.foreach { i => comp.addImport(i) }
      extraMethods.foreach { m => clazz.addMember(m) }
      extraFields.foreach { f => clazz.addMember(f) }

      // Standard size of GUI is Dimension(769, 635). If bigger, add a method
      val (width, height) = sol.layout.minimumSize
      if (width > 769 || height > 635) {
        def max(x:Int, y:Int) = { if (x > y) x else y }
        Java(
          s"""
             |@Override
             |public Dimension getPreferredSize() {
             |	// default starting dimensions...
             |  return new Dimension(${max(769, width)}, ${max(635, height)});
             |}""".stripMargin).methodDeclarations()
          .foreach { m => clazz.addMember(m) }
      }

      // introspect from solitaire domain model
      if (sol.solvable) {
//        val main = comp.getClassByName(nameParameter.toString).get
//        val solvable = Java("SolvableSolitaire").tpe.asClassOrInterfaceType()
//        main.getImplementedTypes.add(solvable)
        comp.getClassByName(nameParameter.toString).get
          .getImplementedTypes
          .add(Java("SolvableSolitaire").tpe.asClassOrInterfaceType)
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
