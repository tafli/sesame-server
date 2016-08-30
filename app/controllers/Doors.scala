package controllers

import com.google.inject.Inject
import models.DualRelayBricklet
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
  * Created by Andreas Boss on 23.08.16.
  */
class Doors @Inject() extends Controller {
  def getDoors = Action { implicit request =>
    val doorsJson = Json.obj(
      "doors" ->
        "123"
    )
    Ok(doorsJson)
  }

  def openFirst(uid: String) = Action {
    open(uid, 1)
    Ok
  }

  def openSelective(uid: String, relay: Int) = Action {
    open(uid, relay)
    Ok
  }

  def open(uid: String, relay: Int) = {
    DualRelayBricklet.setState(uid, relay.toShort)
  }
}
