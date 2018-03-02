package controllers

import javax.inject.Inject

import actors.DualRelayActor
import akka.pattern.ask
import akka.util.Timeout
import models.{Bricklet, DualRelayBricklet}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.JsonUtil

import scala.concurrent.Await
import scala.concurrent.duration._

class Doors @Inject()(cc: ControllerComponents) extends AbstractController(cc) with JsonUtil {
  def getDoors = Action { implicit request =>
    val doorsJson = Json.obj(
      "doors" ->
        Bricklet.getByIdentifier(26).map { bricklet =>
          addSelfLink(Json.toJson(bricklet), routes.Doors.getDoor(bricklet.uid))
        }
    )

    Ok(doorsJson)
  }

  def getDoor(uid: String) = Action { implicit request =>
    val doorsJson = Json.obj(
      "door" ->
        addSelfLink(Json.toJson(Bricklet.getByUid(uid)), routes.Doors.getDoor(uid))
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

  private def open(uid: String, relay: Int) = {
    DualRelayBricklet.setState(uid, relay.toShort)
  }
}
