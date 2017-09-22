package de.tu_dortmund.cs.ls14.git

import play.api.mvc.{Action, InjectedController}

class URLCleanup extends InjectedController {
  def untrail(path: String) = Action {
    MovedPermanently("/" + path)
  }
}
