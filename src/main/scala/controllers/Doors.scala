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

class Doors @Inject() (cc:ControllerComponents) extends AbstractController(cc) with JsonUtil {
  def getDoors = Action { implicit request =>
    val doorsJson = Json.obj(
      "doors" ->
        Bricklet.getBrickletByIdentifier(26).map { bricklet =>
          addSelfLink(Json.toJson(bricklet), routes.Doors.getDoor(bricklet.uid))
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

  def openState(uid: String) = Action { implicit request =>
    implicit val timeout = Timeout(1 seconds)
    val state = Await.result(
      (DualRelayActor.actor ? DualRelayActor.GetState(uid)).mapTo[(Boolean, Boolean)],
      2 seconds
    )

    val json = addSelfLink(Json.obj("state" -> Json.obj(
      "relay_1" -> state._1,
      "relay_2" -> state._2
    )), routes.Doors.openState(uid))

    Ok(json)
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
