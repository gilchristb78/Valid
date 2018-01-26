package org.combinators.solitaire.klondike

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import domain.klondike
import domain.klondike.{DealByThreeKlondikeDomain, ThumbAndPouchKlondikeDomain}
import org.combinators.cls.interpreter.{DynamicCombinatorInfo, ReflectedRepository, StaticCombinatorInfo}
import org.combinators.cls.types.syntax._
import org.combinators.cls.git.{EmptyResults, InhabitationController, Results, html}
import org.combinators.templating.persistable.JavaPersistable._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle


// domain
import domain._

class Klondike @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) {

  // request a specific variation via "http://localhost:9000/klondike?variation=ThumbAndPouch
  val Variation = "variation"

  // register each individual domain object here. Also be sure to add target definition at end of this file
  val Variations = Map(
    ""               -> new klondike.KlondikeDomain(),
    "DealByThree"    -> new DealByThreeKlondikeDomain(),
    "ThumbAndPouch"  -> new ThumbAndPouchKlondikeDomain())

  // Selected variation
  var variation:Solitaire = _

  /** Access parameters to specify variation (if needed) */
  override def overview() = Action { request =>
    variation = Variations(request.getQueryString(Variation).getOrElse(""))

    val combinators = combinatorComponents.mapValues {
      case staticInfo: StaticCombinatorInfo =>
        (ReflectedRepository.fullTypeOf(staticInfo),
          s"${scala.reflect.runtime.universe.show(staticInfo.fullSignature)}")
      case dynamicInfo: DynamicCombinatorInfo[_] =>
        (ReflectedRepository.fullTypeOf(dynamicInfo),
          dynamicInfo.position.mkString("\n"))
    }
    Ok(html.overview.render(
      request.path,
      webJars,
      combinators,
      results.targets,
      results.raw,
      collection.mutable.Set.empty[Long].toSet,  // ignore past ones
      results.infinite,
      results.incomplete))
  }

  /** KlondikeDomain for Klondike defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new KlondikeDomain(variation) with controllers {}
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), variation)
  lazy val combinatorComponents = Gamma.combinatorComponents

  // invoke the proper one
  lazy val results:Results = processVariation(variation)

  // each variation needs to specify its own targets.  A bit of a hack. Still need to find way to programmatically compose these
  def processVariation(s:Solitaire): Results = {
    // Only here for use by BuildSynthesis, which requests each one by number.
    if (s == null) {
      variation = new klondike.KlondikeDomain()
      return processDefaultVariation(variation)
    }

    s match {

      case _:DealByThreeKlondikeDomain   => processByThrees(s)

        // these only change because of the domain model
      case _:ThumbAndPouchKlondikeDomain => processDefaultVariation(s)
      case _                              => processDefaultVariation(s)
    }
  }

  // Specialized target files for each variation...
  def processDefaultVariation(s:Solitaire): Results = {
    import repository._

    lazy val jobs = Gamma.InhabitationBatchJob[CompilationUnit](game(complete))
      .addJob[CompilationUnit](constraints(complete))
      .addJob[CompilationUnit](controller(buildablePile, complete))
      .addJob[CompilationUnit](controller(pile, complete))
      .addJob[CompilationUnit](controller(deck, complete))
      .addJob[CompilationUnit](controller('WastePile, complete))

      .addJob[CompilationUnit]('WastePileClass)
      .addJob[CompilationUnit]('WastePileViewClass)

      .addJob[CompilationUnit](move('MoveColumn :&: move.generic, complete))
      .addJob[CompilationUnit](move('DealDeck :&: move.generic, complete))
      .addJob[CompilationUnit](move('ResetDeck :&: move.generic, complete))
      .addJob[CompilationUnit](move('FlipCard :&: move.generic, complete))
      .addJob[CompilationUnit](move('MoveCard :&: move.generic, complete))
      .addJob[CompilationUnit](move('BuildFoundation :&: move.generic, complete))
      .addJob[CompilationUnit](move('BuildFoundationFromWaste :&: move.generic, complete))

      .addJob[CompilationUnit](move('MoveColumn :&: move.potentialMultipleMove, complete))

    EmptyResults().addAll(jobs.run())
  }

  def processByThrees(s:Solitaire): Results = {
    import repository._

    lazy val jobs = Gamma.InhabitationBatchJob[CompilationUnit](game(complete))
      .addJob[CompilationUnit](constraints(complete))
      .addJob[CompilationUnit](controller(buildablePile, complete))
      .addJob[CompilationUnit](controller(pile, complete))
      .addJob[CompilationUnit](controller(deck, complete))
      .addJob[CompilationUnit]('FanPileClass)

      .addJob[CompilationUnit](controller(fanPile, complete))

      .addJob[CompilationUnit](move('MoveColumn :&: move.generic, complete))
      .addJob[CompilationUnit](move('DealDeck :&: move.generic, complete))
      .addJob[CompilationUnit](move('ResetDeck :&: move.generic, complete))
      .addJob[CompilationUnit](move('FlipCard :&: move.generic, complete))
      .addJob[CompilationUnit](move('MoveCard :&: move.generic, complete))
      .addJob[CompilationUnit](move('BuildFoundation :&: move.generic, complete))
      .addJob[CompilationUnit](move('BuildFoundationFromWaste :&: move.generic, complete))

      .addJob[CompilationUnit](move('MoveColumn :&: move.potentialMultipleMove, complete))

    EmptyResults().addAll(jobs.run())
  }

}
