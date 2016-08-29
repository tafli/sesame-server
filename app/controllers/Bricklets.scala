package controllers

import java.util.concurrent.TimeoutException

import actors.MasterBrickActor
import models.MasterBrick
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

/**
  * Created by Andreas Boss on 23.08.16.
  */
class Bricklets extends Controller {


  def getBricklets = Action {
    try {
      val result = Await.result[MasterBrickActor.BrickData](MasterBrick.fetchInformation("6rnffo"), 2 seconds)

      Ok(Json.obj("apiVersion" -> result.api,
        "stackVoltage" -> result.voltage,
        "stackTemperatur" -> result.temp)
      )
    } catch {
      case toe: TimeoutException => BadRequest(Json.obj("error" -> "Timout contacting stack", "trace" -> toe.getMessage))
      case _: Throwable => BadRequest
    }
  }

  def getBricklet(uid: String) = Action {
    Try {
      val result = Await.result(MasterBrick.fetchInformation(uid), 2 seconds)

      Ok(Json.obj("apiVersion" -> result.api,
        "stackVoltage" -> result.voltage,
        "stackTemperatur" -> result.temp)
      )
    } match {
      case toe => BadRequest("Bad Request")
    }
  }
}
