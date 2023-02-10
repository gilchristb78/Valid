
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/ben/IdeaProjects/nextgen-solitaire/src/main/resources/routes
// @DATE:Tue Oct 04 13:18:10 EDT 2022


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
