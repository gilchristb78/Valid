package org.combinators

import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types._

import scala.reflect.runtime.universe.{Type => _, _}

/**
  * Helper class for debugging combinators.
  *
  * Use this class to generate statistics on the usage of combinators, plus the arity of the individual
  * tuples used in the intersection types.
  *
  * {{{
  *   lazy val statistics = new TypeNameStatistics(Gamma)
  *   println(statistics.overview)
  *   println(statistics.warnings)
  * }}}
  *
  * @param repository   Complete Reflected Repository used for synthesis
  * @tparam A           Type of synthesized elements (i.e., CompilationUnit)
  */
class TypeNameStatistics[A](repository: ReflectedRepository[A]) {
  case class TypeUsage(asParameter: Int = 0,
    asResult: Int = 0,
    arityUsages: Map[Int, Map[String, Seq[Seq[Type]]]] = Map.empty.withDefaultValue(Map.empty.withDefaultValue(Seq.empty))) {
    val overAll: Int = asParameter + asResult
    def increaseParameterUsage: TypeUsage = this.copy(asParameter = asParameter + 1)
    def increaseResultUsage: TypeUsage = this.copy(asResult = asResult + 1)
    def useWithArgumentsIn(combinatorName: String, args: Seq[Type]) = {
      val oldUsage = arityUsages(args.size)
      val newUsage = oldUsage.updated(combinatorName, args +: oldUsage(combinatorName))
      this.copy(arityUsages = arityUsages.updated(args.size, newUsage))
    }
    def isOnlyParameter: Boolean = asParameter > 0 && asResult <= 0
  }

  private lazy val subtypeEnv =
    SubtypeEnvironment(repository.nativeTypeTaxonomy.taxonomy.merge(repository.semanticTaxonomy).underlyingMap)

  private final def increaseUsage(inCombinator: String, isParameter: Boolean, args: Seq[Type]): TypeUsage => TypeUsage =
    typeUsage =>
      (if (isParameter) typeUsage.increaseParameterUsage else typeUsage.increaseResultUsage)
        .useWithArgumentsIn(inCombinator, args)

  private final def analyzeType(
    inCombinator: String,
    usage: Map[String, TypeUsage],
    ty: Type,
    isParameter: Boolean = false): Map[String, TypeUsage] = {
    ty match {
      case Constructor(sym, args@_*) =>
        args.foldLeft(usage.updated(sym, increaseUsage(inCombinator, isParameter, args)(usage(sym)))) {
          case (s, x) => analyzeType(inCombinator, s, x, isParameter)
        }
      case Arrow(src, tgt) =>
        analyzeType(inCombinator, analyzeType(inCombinator, usage, tgt, isParameter), src, !isParameter)
      case Intersection(sigma, tau) =>
        analyzeType(inCombinator, analyzeType(inCombinator, usage, sigma, isParameter), tau, isParameter)
      case _ => usage
    }
  }

  lazy val nativeTypes: Map[String, TypeUsage] =
    repository.combinatorComponents.foldLeft(Map.empty[String, TypeUsage].withDefaultValue(TypeUsage())) {
      case (s, (cn, ci)) => analyzeType(cn, s, ReflectedRepository.nativeTypeOf(ci))
    }
  lazy val semanticTypes: Map[String, TypeUsage] =
    repository.combinatorComponents.foldLeft(Map.empty[String, TypeUsage].withDefaultValue(TypeUsage())) {
      case (s, (cn, ci)) => ci.semanticType.map(analyzeType(cn, s, _)).getOrElse(s)
    }

  def ofNativeType[T](implicit tyTag: WeakTypeTag[T]): TypeUsage =
    nativeTypes(ReflectedRepository.nativeTypeOf[T].name)
  def ofSemanticType(ty: Symbol): TypeUsage =
    semanticTypes(ty.toString)

  def overview: String = {
    s"""
      ==== Statistics (Name, TypeUsage(Parameter Occurrences, Target Occurrences)) ====
      Native Types : { \n ${nativeTypes.toSeq.mkString(";\n\t")} }
      Semantic Types : { \n ${semanticTypes.toSeq.mkString(";\n\t")} }""".stripMargin
  }

  private def possiblyInhabitable(ty: String, seen: Set[String] = Set.empty): Boolean = {
    lazy val subtypes = subtypeEnv.taxonomicSubtypesOf.getOrElse(ty, Set())
    !((nativeTypes(ty).isOnlyParameter || semanticTypes(ty).isOnlyParameter) &&
      subtypes.foldLeft[Option[Set[String]]](Some(seen)) {
        case (None, _) => None
        case (Some(s), subTy) if !seen(ty) && possiblyInhabitable(subTy, s + subTy) => None
        case (Some(s), subTy) => Some(s + subTy)
      }.isDefined)
  }

  private def arityWarningEntry(usageInfo: Map[String, Seq[Seq[Type]]]): Seq[String] = {
    def warnParams(params: Seq[Seq[Type]]): String =
      params.map(_.mkString("(", ", ", ")")).toSet.mkString("{", ", ", "}")
    usageInfo.map { case (combinatorName, params) =>
      s"In $combinatorName with Parameters ${warnParams(params)}"
    }.toSeq
  }

  private def arityWarning(info: (String, TypeUsage)): String = {
    val arityUsages = info._2.arityUsages
    if (arityUsages.size > 1) {
      s"${info._1} ${arityUsages.toSeq.sortBy(_._1).map { case (arity, inf) =>
          s"arity: $arity usage:\n\t\t ${arityWarningEntry(inf).mkString(";\n\t\t")}"
        }.mkString("\n\t", ";\n\t", "")}"
    } else ""
  }

  def warnings: String = {
    s"""
      |==== Warnings ====
      |Types only in parameter but never in target positions:
      |Native Types : { \n ${nativeTypes.keys.filter(ty => !possiblyInhabitable(ty)).mkString(";\n\t")} }
      |Semantic Types : { \n ${semanticTypes.keys.filter(ty => !possiblyInhabitable(ty)).mkString(";\n\t")} }
      |Semantic types used with incompatible arities : { \n\t ${semanticTypes.map(arityWarning).filter(_.nonEmpty).mkString(";\n\t")} }
      """.stripMargin
  }

}

