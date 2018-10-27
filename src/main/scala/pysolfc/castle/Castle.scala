package pysolfc.castle

import _root_.java.nio.file.Path
import javax.inject.Inject

import org.combinators.templating.persistable.PythonWithPath
import org.combinators.templating.persistable.PythonWithPathPersistable._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git.{EmptyResults, InhabitationController, Results}
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle

class Castle @Inject()(webJars: WebJarsUtil, applicationLifecycle: ApplicationLifecycle) extends InhabitationController(webJars, applicationLifecycle) {

  val domainModel = org.combinators.solitaire.castle.castle  //new Domain()

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new CastleDomain(domainModel) with controllers {}
  import repository._
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), domainModel)

  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs =
    Gamma.InhabitationBatchJob[PythonWithPath](game(complete))

  lazy val results:Results = EmptyResults().addAll(jobs.run())

}