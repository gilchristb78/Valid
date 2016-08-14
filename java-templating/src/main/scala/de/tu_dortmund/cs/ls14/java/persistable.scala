package de.tu_dortmund.cs.ls14.java

import scala.collection.JavaConverters._

import java.nio.file.{FileAlreadyExistsException, Files, Path, Paths}

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{NameExpr, QualifiedNameExpr}
import com.github.javaparser.ast.visitor.GenericVisitorAdapter

trait Persistable {
  def rawText: String

  def path: Path

  /**
    * Persists this object to an object dependent path under `basePath`.
    * Overwrites any pre-existing files under `basePath` / `path`.
    */
  def persistOverwriting(basePath: Path): Unit = {
    val fullPath = Paths.get(basePath.toAbsolutePath.toString, path.getParent.toString)
    if (!Files.exists(fullPath.getParent))
      Files.createDirectories(fullPath.getParent)
    Files.write(fullPath, rawText.getBytes)
  }

  /**
    * Persists this object to an object dependent path under `basePath`.
    * Throws an `FileAlreadyExistsException` if the file already exists.
    */
  def persist(basePath: Path): Unit = {
    val fullPath = Paths.get(basePath.toAbsolutePath.toString, path.getParent.toString)
    if (Files.exists(fullPath)) throw new FileAlreadyExistsException(fullPath.toString)
    else persistOverwriting(fullPath)
  }
}

object Persistable {
  /**
    * Persistable instance for a compilation unit, deriving path and file name from the package and first type decl name.
    */
  implicit def compilationUnitInstance(compilationUnit: CompilationUnit): Persistable =
    new Persistable {
      override def rawText = compilationUnit.toString
      override def path = {
        val pkg: Seq[String] =
          compilationUnit.getPackage match {
            case null => Seq.empty
            case somePkg =>
              somePkg.accept(new GenericVisitorAdapter[Seq[String], Unit] {
                  override def visit(name: NameExpr, arg: Unit): Seq[String] = Seq(name.getName)
                  override def visit(name: QualifiedNameExpr, arg: Unit): Seq[String] =
                    name.getQualifier.accept(this, arg) :+ name.getName
                },
                ()
              )
          }
        val clsName = compilationUnit.getTypes.asScala.head.getName
        val fullPath = pkg :+ clsName
        Paths.get(fullPath.head, fullPath.tail : _*)
      }
    }
}