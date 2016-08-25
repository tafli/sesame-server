package controllers

import models.MasterBrick
import play.api.mvc.{Action, Controller}

/**
  * Created by Andreas Boss on 23.08.16.
  */
class Bricklets extends Controller {
  def getBricklets = Action { implicit request =>

    val result = MasterBrick.fetchInformation("123")

    println(s"f=$result")

    Ok
  }
}
