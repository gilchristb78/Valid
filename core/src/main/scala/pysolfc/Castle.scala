package pysolfc

import _root_.java.nio.file.Paths
import javax.inject.Inject

import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.twirl.Python
import domain.castle.Domain
import org.webjars.play.WebJarsUtil

class Castle @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  val domainModel = new Domain()

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new CastleDomain(domainModel) with GameTemplate {}
  import repository._
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), domainModel)

  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs = Gamma.InhabitationBatchJob[Python](game(complete))

  lazy val results:Results = Results.add(jobs.run(), Paths.get("castle.py"))

}