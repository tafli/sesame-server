package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import models.{Bricklet, DualRelayBricklet}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils.JsonUtil

/**
  * Created by Andreas Boss on 23.08.16.
  */
@Singleton
class Doors @Inject() extends Controller with JsonUtil {
  def getDoors = Action { implicit request =>
    val doorsJson = Json.obj(
      "doors" ->
        Bricklet.getBrickletByIdentifier(26).map { b =>
          addSelfLink(Json.toJson(b), routes.Doors.getDoor(b.uid))
        }
    )

    Ok(doorsJson)
  }
  def getDoor(uid: String) = Action { implicit request =>
    val doorsJson = Json.obj(
      "door" ->
        Bricklet.getBrickletByUid(uid).map { b =>
          addSelfLink(Json.toJson(b), routes.Doors.getDoor(uid))
        }
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
