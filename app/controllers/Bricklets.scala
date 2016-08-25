package controllers

import models.MasterBrick
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
  * Created by Andreas Boss on 23.08.16.
  */
class Bricklets extends Controller {
  def getBricklets = Action {
    val result = MasterBrick.fetchInformation("123")

    Ok(Json.obj("bricklets" -> result))
  }
  def getBricklet(uid: String) = Action {
    val result = MasterBrick.fetchInformation(uid)

    Ok(Json.obj("bricklets" -> result))
  }
}
