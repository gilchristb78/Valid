
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/ben/IdeaProjects/nextgen-solitaire/src/main/resources/routes
// @DATE:Tue Oct 04 13:18:10 EDT 2022

import play.api.mvc.Call


import _root_.controllers.Assets.Asset

// @LINE:15
package example.temperature {

  // @LINE:15
  class ReverseTemperature(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:17
    def prepare(number:Long): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "temperature/prepare" + play.core.routing.queryString(List(Some(implicitly[play.api.mvc.QueryStringBindable[Long]].unbind("number", number)))))
    }
  
    // @LINE:18
    def serveFile(file:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "temperature/temperature.git/" + implicitly[play.api.mvc.PathBindable[String]].unbind("file", file))
    }
  
    // @LINE:16
    def raw(number:Long): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "temperature/raw_" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[Long]].unbind("number", number)))
    }
  
    // @LINE:15
    def overview(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "temperature")
    }
  
  }


}
