package org.combinators.solitaire.klondike

import javax.inject.Inject

import com.github.javaparser.ast.CompilationUnit
import domain.klondike
import domain.klondike._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.syntax._
import org.combinators.cls.git._
import org.combinators.cls.types.Constructor
import org.combinators.templating.persistable.JavaPersistable._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle
import play.api.routing.SimpleRouter


// domain
import domain._


abstract class KlondikeVariationController(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends InhabitationController(webJars, applicationLifecycle) with RoutingEntries {

  // request a specific variation via "http://localhost:9000/klondike?variation=ThumbAndPouch
  val variation: klondike.KlondikeDomain

  /** KlondikeDomain for Klondike defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new KlondikeDomain(variation) with controllers {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), variation)
  lazy val combinatorComponents = Gamma.combinatorComponents

  import repository._
  lazy val commonTargets: Seq[Constructor] =
    Seq(
      game(complete),
      constraints(complete),
      controller(buildablePile, complete),
      controller(pile, complete),
      controller(deck, complete),
      move('MoveColumn :&: move.generic, complete),
      move('DealDeck :&: move.generic, complete),
      move('FlipCard :&: move.generic, complete),
      move('MoveCard :&: move.generic, complete),
      move('BuildFoundation :&: move.generic, complete),
      move('BuildFoundationFromWaste :&: move.generic, complete),
      move('MoveColumn :&: move.potentialMultipleMove, complete)
    )
  lazy val wastePileTargets: Seq[Constructor] =
    Seq(
      controller('WastePile, complete),
      'WastePileClass,
      'WastePileViewClass
    )
  lazy val resetDeckTargets: Seq[Constructor] =
    Seq(
      move('ResetDeck :&: move.generic, complete)
    )
  lazy val byThreesTargets: Seq[Constructor] =
    Seq(
      'FanPileClass,
      controller(fanPile, complete)
    )

  lazy val targets =
    commonTargets ++
      (variation match {
        case _: klondike.Whitehead => wastePileTargets
        case _: klondike.EastCliff => wastePileTargets
        case _: klondike.DealByThreeKlondikeDomain => byThreesTargets ++ resetDeckTargets
        case _ => wastePileTargets ++ resetDeckTargets
      })

  lazy val results:Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targets).compute()

  override val routingPrefix: Option[String] = Some("klondike")
  lazy val controllerAddress: String = variation.name.toLowerCase
}

class KlondikeController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = new klondike.KlondikeDomain()
}

class WhiteheadController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = new klondike.Whitehead()
}

class EastcliffController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = new klondike.EastCliff()
}

class SmallHarpController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = new klondike.SmallHarp()
}

class ThumbAndPuchController @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = new klondike.ThumbAndPouchKlondikeDomain
}

class DealByThreeKlondikeDomain @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle)
  extends KlondikeVariationController(webJars, applicationLifecycle) {
  lazy val variation = new klondike.DealByThreeKlondikeDomain()
}

