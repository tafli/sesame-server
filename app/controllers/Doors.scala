package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
  * Created by Andreas Boss on 23.08.16.
  */
class Doors extends Controller {
  def getDoors = Action { implicit request =>
    val doorsJson = Json.obj("doors" ->
      "123"
    )
    Ok(doorsJson)
  }


  def openDoor(id: String) = Action { implicit request =>
    Ok
  }
}