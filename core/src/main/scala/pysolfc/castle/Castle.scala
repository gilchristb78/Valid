package pysolfc.castle

import _root_.java.nio.file.Path
import javax.inject.Inject

import de.tu_dortmund.cs.ls14.Persistable
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.git.InhabitationController
import de.tu_dortmund.cs.ls14.twirl.Python
import domain.castle.Domain
import org.webjars.play.WebJarsUtil
import pysolfc.shared.GameTemplate

class Castle @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  val domainModel = new Domain()

  // FreeCellDomain is base class for the solitaire variation. Note that this
  // class is used (essentially) as a placeholder for the solitaire val,
  // which can then be referred to anywhere as needed.
  lazy val repository = new CastleDomain(domainModel) with GameTemplate {}
  import repository._
  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), domainModel)

  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs =
    Gamma.InhabitationBatchJob[(Python, Path)](game(complete))
        .addJob[(Python, Path)](game(pysol.initFile))

  /**
    * Tell the framework to store stuff of type (Python, Path) at the location specified in Path.
    * The Path is relative to the Git repository.
    */
  implicit def PersistInt: Persistable.Aux[(Python, Path)] = new Persistable {
    override def path(elem: (Python, Path)): Path = elem._2
    override def rawText(elem: (Python, Path)): String = elem._1.getCode
    override type T = (Python, Path)
  }

  lazy val results:Results = Results.addAll(jobs.run())

  // Note: I want to also  be able to synthesize another file __init__.py, but I can't figure out how to do
  // it without rewriting this file. Any ideas?
//
//  lazy val init_jobs = Gamma.InhabitationBatchJob[Python](game(pysol.initFile))
//
//  lazy val init_results:Results = Results.add(init_jobs.run(), Paths.get("__init__.py"))



}