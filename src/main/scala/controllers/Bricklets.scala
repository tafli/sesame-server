package controllers

import javax.inject.{Inject, Singleton}

import actors.EnumerationActor
import akka.pattern.ask
import akka.util.Timeout
import models.Bricklet
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.Await
import scala.concurrent.duration._

@Singleton
class Bricklets @Inject() (cc:ControllerComponents) extends AbstractController(cc) {
  def getBricklets = Action {
    implicit val timeout = Timeout(1 seconds)
    val bricklets = Await.result(
      (EnumerationActor.actor ? EnumerationActor.GetBricklets).mapTo[Set[Bricklet]],
      2 seconds
    )

    Ok(Json.obj("bricklets" -> bricklets.map { b =>
      Json.toJson(b)
    }))
  }
}
