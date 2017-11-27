package example.expression.cpp

import java.nio.file.{Path, Paths}
import javax.inject.Inject

import de.tu_dortmund.cs.ls14.Persistable
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository
import de.tu_dortmund.cs.ls14.git.InhabitationController
import expression.data.{Add, Eval, Lit}
import expression.extensions.{Collect, Neg, PrettyP, Sub}
import expression.operations.SimplifyAdd
import expression.{DomainModel, Exp}
import org.webjars.play.WebJarsUtil

class Expression_CPP @Inject()(webJars: WebJarsUtil) extends InhabitationController(webJars) {

  // Configure the desired (sub)types and operations
  val model:DomainModel = new DomainModel()

  // no need to add 'Exp' to the model, since assumed always to be there
  model.data.add(new Lit)
  model.data.add(new Add)
  model.data.add(new Neg)
  model.data.add(new Sub)

  // operations to have (including Eval)
  model.ops.add(new Eval)
  model.ops.add(new PrettyP)
  model.ops.add(new SimplifyAdd)
  model.ops.add(new Collect)

  lazy val repository = new ExpressionSynthesis_CPP(model) with CPP_Structure {}
  import repository._

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), model)

  /** This needs to be defined, and it is set from Gamma. */
  lazy val combinatorComponents = Gamma.combinatorComponents

  /**
    * Tell the framework to store stuff of type (Python, Path) at the location specified in Path.
    * The Path is relative to the Git repository.
    */
//  implicit def PersistInt: Persistable.Aux[(CPPClass, Path)] = new Persistable {
//    override def path(elem: (CPPClass, Path)): Path = elem._2
//    override def rawText(elem: (CPPClass, Path)): String = elem._1.toString()
//    override type T = (CPPClass, Path)
//  }

  implicit def PersistInt: Persistable.Aux[CPPFile] = new Persistable {
    override def path(elem: CPPFile): Path = Paths.get(elem.fileName() + ".cpp")
    override def rawText(elem: CPPFile): String = elem.toString()
    override type T = CPPFile
  }

//  var jobs = Gamma.InhabitationBatchJob[CPPFile](generated(generated.visitor))
//          .addJob[CPPFile](exp(exp.base, new Exp))
//          .addJob[CPPFile](ops(ops.visitor, new Eval))    // assumed to always be there...
//
//          .addJob[CPPFile](exp(exp.visitor, new Lit))
//          .addJob[CPPFile](exp(exp.visitor, new Add))
//          .addJob[CPPFile](exp(exp.visitor, new Sub))
//          .addJob[CPPFile](exp(exp.visitor, new Neg))
//
//          .addJob[CPPFile](ops(ops.visitor, new PrettyP))
//          //.addJob[CPPFile](ops(ops.visitor, new Collect))
//
//
//
//          // new one...
//          //.addJob[CPPClass](ops(ops.visitor, new SimplifyAdd))
//
//          .addJob[CPPFile](driver)
//          .addJob[CPPFile](module(module.base))

  // produce concatenation of files in specific order for compilation purpose.
  // may prove challenging to have independent extensions...
  var jobs = Gamma.InhabitationBatchJob[CPPFile](module(module.base))

  lazy val results = Results.addAll(jobs.run())

}
