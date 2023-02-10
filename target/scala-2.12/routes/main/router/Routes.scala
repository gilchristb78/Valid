
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/ben/IdeaProjects/nextgen-solitaire/src/main/resources/routes
// @DATE:Tue Oct 04 13:18:10 EDT 2022

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:1
  org_combinators_cls_git_Routes_0: org.combinators.cls.git.Routes,
  // @LINE:11
  org_combinators_solitaire_minimal_Minimal_2: org.combinators.solitaire.minimal.Minimal,
  // @LINE:15
  Temperature_0: example.temperature.Temperature,
  // @LINE:20
  example_timeGadget_TimeGadget_1: example.timeGadget.TimeGadget,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:1
    org_combinators_cls_git_Routes_0: org.combinators.cls.git.Routes,
    // @LINE:11
    org_combinators_solitaire_minimal_Minimal_2: org.combinators.solitaire.minimal.Minimal,
    // @LINE:15
    Temperature_0: example.temperature.Temperature,
    // @LINE:20
    example_timeGadget_TimeGadget_1: example.timeGadget.TimeGadget
  ) = this(errorHandler, org_combinators_cls_git_Routes_0, org_combinators_solitaire_minimal_Minimal_2, Temperature_0, example_timeGadget_TimeGadget_1, "/")

  def withPrefix(prefix: String): Routes = {
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, org_combinators_cls_git_Routes_0, org_combinators_solitaire_minimal_Minimal_2, Temperature_0, example_timeGadget_TimeGadget_1, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    prefixed_org_combinators_cls_git_Routes_0_0.router.documentation,
    prefixed_org_combinators_solitaire_minimal_Minimal_2_1.router.documentation,
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """temperature""", """example.temperature.Temperature.overview()"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """temperature/raw_""" + "$" + """number<[^/]+>""", """example.temperature.Temperature.raw(number:Long)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """temperature/prepare""", """example.temperature.Temperature.prepare(number:Long)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """temperature/temperature.git/""" + "$" + """file<.+>""", """example.temperature.Temperature.serveFile(file:String)"""),
    prefixed_example_timeGadget_TimeGadget_1_6.router.documentation,
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:1
  private[this] val prefixed_org_combinators_cls_git_Routes_0_0 = Include(org_combinators_cls_git_Routes_0.withPrefix(this.prefix + (if (this.prefix.endsWith("/")) "" else "/") + ""))

  // @LINE:11
  private[this] val prefixed_org_combinators_solitaire_minimal_Minimal_2_1 = Include(org_combinators_solitaire_minimal_Minimal_2.withPrefix(this.prefix + (if (this.prefix.endsWith("/")) "" else "/") + ""))

  // @LINE:15
  private[this] lazy val example_temperature_Temperature_overview2_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("temperature")))
  )
  private[this] lazy val example_temperature_Temperature_overview2_invoker = createInvoker(
    Temperature_0.overview(),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "example.temperature.Temperature",
      "overview",
      Nil,
      "GET",
      this.prefix + """temperature""",
      """""",
      Seq()
    )
  )

  // @LINE:16
  private[this] lazy val example_temperature_Temperature_raw3_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("temperature/raw_"), DynamicPart("number", """[^/]+""",true)))
  )
  private[this] lazy val example_temperature_Temperature_raw3_invoker = createInvoker(
    Temperature_0.raw(fakeValue[Long]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "example.temperature.Temperature",
      "raw",
      Seq(classOf[Long]),
      "GET",
      this.prefix + """temperature/raw_""" + "$" + """number<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:17
  private[this] lazy val example_temperature_Temperature_prepare4_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("temperature/prepare")))
  )
  private[this] lazy val example_temperature_Temperature_prepare4_invoker = createInvoker(
    Temperature_0.prepare(fakeValue[Long]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "example.temperature.Temperature",
      "prepare",
      Seq(classOf[Long]),
      "GET",
      this.prefix + """temperature/prepare""",
      """""",
      Seq()
    )
  )

  // @LINE:18
  private[this] lazy val example_temperature_Temperature_serveFile5_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("temperature/temperature.git/"), DynamicPart("file", """.+""",false)))
  )
  private[this] lazy val example_temperature_Temperature_serveFile5_invoker = createInvoker(
    Temperature_0.serveFile(fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "example.temperature.Temperature",
      "serveFile",
      Seq(classOf[String]),
      "GET",
      this.prefix + """temperature/temperature.git/""" + "$" + """file<.+>""",
      """""",
      Seq()
    )
  )

  // @LINE:20
  private[this] val prefixed_example_timeGadget_TimeGadget_1_6 = Include(example_timeGadget_TimeGadget_1.withPrefix(this.prefix + (if (this.prefix.endsWith("/")) "" else "/") + ""))


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:1
    case prefixed_org_combinators_cls_git_Routes_0_0(handler) => handler
  
    // @LINE:11
    case prefixed_org_combinators_solitaire_minimal_Minimal_2_1(handler) => handler
  
    // @LINE:15
    case example_temperature_Temperature_overview2_route(params@_) =>
      call { 
        example_temperature_Temperature_overview2_invoker.call(Temperature_0.overview())
      }
  
    // @LINE:16
    case example_temperature_Temperature_raw3_route(params@_) =>
      call(params.fromPath[Long]("number", None)) { (number) =>
        example_temperature_Temperature_raw3_invoker.call(Temperature_0.raw(number))
      }
  
    // @LINE:17
    case example_temperature_Temperature_prepare4_route(params@_) =>
      call(params.fromQuery[Long]("number", None)) { (number) =>
        example_temperature_Temperature_prepare4_invoker.call(Temperature_0.prepare(number))
      }
  
    // @LINE:18
    case example_temperature_Temperature_serveFile5_route(params@_) =>
      call(params.fromPath[String]("file", None)) { (file) =>
        example_temperature_Temperature_serveFile5_invoker.call(Temperature_0.serveFile(file))
      }
  
    // @LINE:20
    case prefixed_example_timeGadget_TimeGadget_1_6(handler) => handler
  }
}
