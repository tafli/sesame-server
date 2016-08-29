package controllers

import actors.EnumerationActor
import akka.pattern.ask
import akka.util.Timeout
import models.Bricklet
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by Andreas Boss on 23.08.16.
  */
class Bricklets extends Controller {
  def getBricklets = Action {
    implicit val timeout = Timeout(1 seconds)
    val bricklets = Await.result((EnumerationActor.actor ? EnumerationActor.GetBricklets).mapTo[Set[Bricklet]], 2 seconds)

    bricklets.map(b => b.uid).foreach(println(_))

    Ok(Json.arr(bricklets.map(_.toJson)))
  }
}
