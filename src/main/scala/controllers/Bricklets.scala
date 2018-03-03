package controllers

import javax.inject.{Inject, Singleton}

import models.Bricklet
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class Bricklets @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  def getBricklets = Action.async {

    Bricklet.getBricklets.map { bricklets =>
      Ok(
        Json.obj(
          "bricklets" -> bricklets.map { b =>
            Json.toJson(b)
          }
        )
      )
    }
  }
}
