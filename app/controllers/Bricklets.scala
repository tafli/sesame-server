package controllers

import play.api.mvc.{Action, Controller}

/**
  * Created by Andreas Boss on 23.08.16.
  */
class Bricklets extends Controller {
  def getBricklets = Action { implicit request =>
    Ok("Response")
  }
}
