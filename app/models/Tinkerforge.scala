package models

import actors.{MasterBrickActor, RootActor}
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by boss on 23.08.16.
  */
object MasterBrick {
  val masterBrickActor = RootActor.system.actorOf(Props[MasterBrickActor])

  def fetchInformation(uid: String) = {
    implicit val timeout = Timeout(5 seconds)
    val future = (masterBrickActor ? MasterBrickActor.BrickUid(uid)).mapTo[String]
    val result = Await.result(future, 5 second)
    println(s"Result: $result")

    result
  }
}

case class MasterBrick(uid: String)