package org.combinators

import scala.reflect.runtime.universe.{ Type => _, _}
import de.tu_dortmund.cs.ls14.cls.types._
import de.tu_dortmund.cs.ls14.cls.interpreter.ReflectedRepository

class TypeNameStatistics[A](repository: ReflectedRepository[A]) {
  case class TypeUsage(asParameter: Int = 0, asResult: Int = 0) {
    val overAll: Int = asParameter + asResult
    def increaseParameterUsage : TypeUsage = this.copy(asParameter = asParameter + 1)
    def increaseResultUsage : TypeUsage = this.copy(asResult = asResult + 1)
    def isOnlyParameter = asParameter > 0 && asResult <= 0
  }

  private lazy val subtypeEnv =
    SubtypeEnvironment(repository.nativeTypeTaxonomy.taxonomy.merge(repository.semanticTaxonomy))

  private final def increaseUsage(isParameter: Boolean): TypeUsage => TypeUsage =
    if (isParameter) _.increaseParameterUsage else _.increaseResultUsage

  private final def analyzeType(usage: Map[String, TypeUsage], ty: Type, isParameter: Boolean = false): Map[String, TypeUsage] = {
    ty match {
      case Constructor(sym, args@_*) =>
        args.foldLeft(usage.updated(sym, increaseUsage(isParameter)(usage(sym)))) {
          case (s, x) => analyzeType(s, x, isParameter)
        }
      case Arrow(src, tgt) =>
        analyzeType(analyzeType(usage, tgt, isParameter), src, !isParameter)
      case Intersection(sigma, tau) =>
        analyzeType(analyzeType(usage, sigma, isParameter), tau, isParameter)
      case _ => usage
    }
  }

  lazy val nativeTypes: Map[String, TypeUsage] =
    repository.combinatorComponents.foldLeft(Map.empty[String, TypeUsage].withDefaultValue(TypeUsage())) {
      case (s, (_, ci)) => analyzeType(s, ReflectedRepository.nativeTypeOf(ci))
    }
  lazy val semanticTypes: Map[String, TypeUsage] =
    repository.combinatorComponents.foldLeft(Map.empty[String, TypeUsage].withDefaultValue(TypeUsage())) {
      case (s, (_, ci)) => ci.semanticType.map(analyzeType(s, _)).getOrElse(s)
    }

  def ofNativeType[A](implicit tyTag: WeakTypeTag[A]): TypeUsage =
    nativeTypes(ReflectedRepository.nativeTypeOf[A].name)
  def ofSemanticType(ty: Symbol): TypeUsage =
    semanticTypes(ty.toString)

  def overview: String = {
    s"""
      ==== Statistics (Name, TypeUsage(Parameter Occurrences, Target Occurrences)) ====
      Native Types : { \n ${nativeTypes.toSeq.mkString(";\n\t")} }
      Semantic Types : { \n ${semanticTypes.toSeq.mkString(";\n\t")} }""".stripMargin
  }

  private def possiblyInhabitable(ty: String, seen: Set[String] = Set.empty): Boolean = {
    lazy val subtypes = subtypeEnv.taxonomicSubtypesOf(ty)
    !((nativeTypes(ty).isOnlyParameter || semanticTypes(ty).isOnlyParameter) &&
      subtypes.foldLeft[Option[Set[String]]](Some(seen)) {
        case (None, _) => None
        case (Some(s), subTy) if !seen(ty) && possiblyInhabitable(subTy, s + subTy) => None
        case (Some(s), subTy) => Some(s + subTy)
      }.isDefined)
  }

  def warnings: String = {
    s"""
      ==== Warnings ====
      Types only in parameter but never in target positions:
      Native Types : { \n ${nativeTypes.keys.filter(ty => !possiblyInhabitable(ty)).mkString(";\n\t")} }
      Semantic Types : { \n ${semanticTypes.keys.filter(ty => !possiblyInhabitable(ty)).mkString(";\n\t")} }""".stripMargin
  }

}

