
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/ben/IdeaProjects/nextgen-solitaire/src/main/resources/routes
// @DATE:Tue Oct 04 13:18:10 EDT 2022

import play.api.routing.JavaScriptReverseRoute


import _root_.controllers.Assets.Asset

// @LINE:15
package example.temperature.javascript {

  // @LINE:15
  class ReverseTemperature(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:17
    def prepare: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "example.temperature.Temperature.prepare",
      """
        function(number0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "temperature/prepare" + _qS([(""" + implicitly[play.api.mvc.QueryStringBindable[Long]].javascriptUnbind + """)("number", number0)])})
        }
      """
    )
  
    // @LINE:18
    def serveFile: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "example.temperature.Temperature.serveFile",
      """
        function(file0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "temperature/temperature.git/" + (""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("file", file0)})
        }
      """
    )
  
    // @LINE:16
    def raw: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "example.temperature.Temperature.raw",
      """
        function(number0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "temperature/raw_" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[Long]].javascriptUnbind + """)("number", number0))})
        }
      """
    )
  
    // @LINE:15
    def overview: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "example.temperature.Temperature.overview",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "temperature"})
        }
      """
    )
  
  }


}
