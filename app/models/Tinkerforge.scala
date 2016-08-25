package models

import actors.MasterBrickActor
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by boss on 23.08.16.
  */
object MasterBrick {
  val system = ActorSystem("Master_Brick_System", ConfigFactory.load("application"))
  val masterBrickActor = system.actorOf(Props[MasterBrickActor])

  implicit val timeout = Timeout(5 seconds)

  def fetchInformation(uid: String) = {
    val future = masterBrickActor ? uid
    Await.result(future, timeout.duration).asInstanceOf[String]
  }
}

case class MasterBrick(uid: String)