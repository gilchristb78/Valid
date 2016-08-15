package de.tu_dortmund.cs.ls14.git

import java.nio.file._

import controllers.WebJarAssets
import shapeless.feat.Enumeration
import de.tu_dortmund.cs.ls14.cls.inhabitation.Tree
import de.tu_dortmund.cs.ls14.cls.interpreter.InhabitationResult
import de.tu_dortmund.cs.ls14.cls.types.Type
import de.tu_dortmund.cs.ls14.java.Persistable
import de.tu_dortmund.cs.ls14.html
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import play.api.mvc._

abstract class InhabitationController(webJars: WebJarAssets) extends Controller {
  private lazy val root = Files.createTempDirectory("inhabitants")
  private lazy val git = Git.init().setDirectory(root.toFile()).call()
  private lazy val computedVariations = collection.mutable.Set.empty[Long]

  def init() : Unit = {
    root.toFile.deleteOnExit()
  }
  init()

  val combinators: Map[String, Type]

  val sourceDirectory = Paths.get("src", "main", "java")

  sealed trait Results { self =>
    val targets: Seq[Type]
    val raw: Enumeration[Seq[Tree]]
    val persistenceActions: Enumeration[Seq[() => Unit]]
    def storeToDisk(fileSystemRoot: Path, number: Long): Unit = {
      val result = persistenceActions.index(number)
      result.foreach(_.apply())
    }


    def add[R](inhabitationResult: InhabitationResult[R], repositoryPath: Path): Results =
      add[R](inhabitationResult)(new Persistable {
        type T = R
        override def rawText(elem: T) = elem.toString
        override def path(elem: T) = repositoryPath
      })

    def add[T](inhabitationResult: InhabitationResult[T])(implicit persistable: Persistable.Aux[T]): Results =
      new Results {
        val targets = self.targets :+ inhabitationResult.target
        val raw = self.raw.product(inhabitationResult.terms).map {
          case (others, next) => others :+ next
        }
        val persistenceActions = self.persistenceActions.product(inhabitationResult.interpretedTerms).map {
          case (ps, r) => ps :+ (() => persistable.persist(root.resolve(sourceDirectory), r))
        }
      }
  }
  object Results extends Results {
    val targets = Seq.empty
    val raw = Enumeration.singleton(Seq())
    val persistenceActions = Enumeration.singleton(Seq())
  }

  val results: Results

  private def checkoutEmptyBranch(id: Long): Unit = {
    git
      .checkout()
      .setOrphan(true)
      .setName(s"variation_$id")
      .call()
  }

  private def addAllFilesToCurrentBranch(): RevCommit = {
    git
      .add()
      .addFilepattern(".")
      .call()
    git
      .commit()
      .setMessage("Next variation")
      .call()
  }

  private def updateInfo(rev: RevCommit, id: Long): Unit = {
    val info = Paths.get(root.toString, ".git", "info")
    Files.createDirectories(info)
    val refs = Paths.get(root.toString, ".git", "info", "refs")
    val line = s"${rev.getName}\trefs/heads/variation_${id}\n"
    Files.write(refs, line.getBytes, StandardOpenOption.APPEND, StandardOpenOption.CREATE)
  }

  def prepare(number: Long) = Action {
    val branchFile = Paths.get(root.toString, ".git", "refs", "heads", "variation_$id")
    if (!Files.exists(branchFile)) {
      checkoutEmptyBranch(number)
      results.storeToDisk(root, number)
      val rev = addAllFilesToCurrentBranch()
      updateInfo(rev, number)
      computedVariations.add(number)
    }
    Ok(results.raw.index(number).toString)
    //Redirect(".")
  }

  def overview() = Action {
    Ok(html.overview.render(webJars, combinators, results.targets, results.raw, computedVariations.toSet))
  }
  def raw(id: Long) = {
    TODO
  }

  def serveFile(name: String) = Action {
    try {
      Ok(Files.readAllBytes(root.resolve(Paths.get(".git", name))))
    } catch {
      case _: NoSuchFileException => play.api.mvc.Results.NotFound(s"404, File not found: $name")
      case _: AccessDeniedException => play.api.mvc.Results.Forbidden(s"403, Forbidden: $name")
    }
  }

}