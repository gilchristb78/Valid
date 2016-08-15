package de.tu_dortmund.cs.ls14.java

import scala.collection.JavaConverters._

import java.nio.file.{FileAlreadyExistsException, Files, Path, Paths}

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.expr.{NameExpr, QualifiedNameExpr}
import com.github.javaparser.ast.visitor.GenericVisitorAdapter

trait Persistable {
  type T
  def rawText(elem: T): String
  def path(elem: T): Path

  /**
    * Computes the full path where to place `elem` relative to `basePath`.
    */
  def fullPath(basePath: Path, elem: T): Path = {
    basePath.resolve(path(elem))
  }


  /**
    * Persists this object to an object dependent path under `basePath`.
    * Overwrites any pre-existing files under `basePath` / `path`.
    */
  def persistOverwriting(basePath: Path, elem: T): Unit = {
    val fp = fullPath(basePath, elem)
    if (!Files.exists(fp.getParent))
      Files.createDirectories(fp.getParent)
    Files.write(fp, rawText(elem).getBytes)
  }

  /**
    * Persists this object to an object dependent path under `basePath`.
    * Throws an `FileAlreadyExistsException` if the file already exists.
    */
  def persist(basePath: Path, elem: T): Unit = {
    val fp = fullPath(basePath, elem)
    if (Files.exists(fp)) throw new FileAlreadyExistsException(fp.toString)
    else persistOverwriting(basePath, elem)
  }
}

object Persistable {
  type Aux[TT] = Persistable { type T = TT }

  /**
    * Persistable instance for a compilation unit, deriving path and file name from the package and first type decl name.
    */
  implicit def compilationUnitInstance: Aux[CompilationUnit] =
    new Persistable {
      type T = CompilationUnit
      override def rawText(compilationUnit: CompilationUnit) = compilationUnit.toString
      override def path(compilationUnit: CompilationUnit) = {
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
        val clsName = s"${compilationUnit.getTypes.asScala.head.getName}.java"
        val fullPath = pkg :+ clsName
        Paths.get(fullPath.head, fullPath.tail : _*)
      }
    }

  def apply[T](implicit persistable: Aux[T]): Aux[T] = persistable
}