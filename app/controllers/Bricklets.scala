package controllers

import javax.inject.Singleton

import actors.EnumerationActor
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import models.Bricklet
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by Andreas Boss on 23.08.16.
  */
@Singleton
class Bricklets @Inject() extends Controller {
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
