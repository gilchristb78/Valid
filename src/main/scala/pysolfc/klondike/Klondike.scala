package pysolfc.klondike

import javax.inject.Inject

import org.combinators.solitaire.domain._
import org.combinators.templating.persistable.PythonWithPath
import org.combinators.templating.persistable.PythonWithPathPersistable._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git.{EmptyResults, InhabitationController, Results}
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle

class Klondike @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) {

  //val domainModel = new klondike.KlondikeDomain()
  val domainModel = org.combinators.solitaire.klondike.klondike  // TODO: FIX ME   new klondike.EastCliff()

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new KlondikeDomain(domainModel) with controllers {}
  import repository._
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), domainModel)

  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs =
    Gamma.InhabitationBatchJob[PythonWithPath](game(complete))

  lazy val results:Results = EmptyResults().addAll(jobs.run())
}