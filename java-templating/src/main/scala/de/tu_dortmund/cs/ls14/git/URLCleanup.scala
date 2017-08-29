package de.tu_dortmund.cs.ls14.git

import play.api.mvc.{Action, Controller}

class URLCleanup extends Controller {
  def untrail(path: String) = Action {
    MovedPermanently("/" + path)
  }
}
