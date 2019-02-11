package controllers

import javax.inject.Inject
import models.{Bricklet, DualRelayBricklet}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import utils.JsonUtil

import scala.concurrent.ExecutionContext.Implicits.global


class Doors @Inject()(cc: ControllerComponents) extends AbstractController(cc) with JsonUtil {
  def getDoors: Action[AnyContent] = Action.async { implicit request =>
    Bricklet.getByIdentifier(26).map { bricklets: Set[Bricklet] =>
      Ok(Json.obj(
        "doors" ->
          bricklets.map { bricklet =>
            addSelfLink(Json.toJson(bricklet), routes.Doors.getDoor(bricklet.uid))
          })
      )
    }.recover {
      case e: scala.concurrent.TimeoutException =>
        NotFound
    }
  }

  def getDoor(uid: String): Action[AnyContent] = Action.async { implicit request =>
    Bricklet.getByUid(uid).map { bricklet: Bricklet =>
      Ok(Json.obj(
        "door" ->
          addSelfLink(Json.toJson(bricklet), routes.Doors.getDoor(uid))
      ))
    }.recover {
      case e: scala.concurrent.TimeoutException =>
        NotFound
    }
  }

  def openFirst(uid: String): Action[AnyContent] = Action {
    open(uid, 1)
    Ok
  }

  def openSelective(uid: String, relay: Int): Action[AnyContent] = Action {
    open(uid, relay)
    Ok
  }

  private def open(uid: String, relay: Int): Unit = {
    DualRelayBricklet.setState(uid, relay.toShort)
  }
}
