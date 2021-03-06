package actors

import actors.StackActor._
import akka.actor.{Actor, Props}
import com.tinkerforge.IPConnection
import models.{Bricklet, TFConnector}
import play.api.Logging

import scala.concurrent.duration._

object StackActor {
  val actor = RootActor.system.actorOf(Props[StackActor])

  def props: Props = Props(new MasterBrickActor)

  case class Tick()

  case class Enumerate(bricklet: Bricklet, iPConnection: IPConnection)

  case class GetBricklets()

  case class GetBrickletsByIdentifier(identifier: Int)

  case class GetBrickletByUid(uid: String)

  case class GetIpConnectionByUid(uid: String)

}

class StackActor extends Actor with Logging {

  import context.dispatcher

  val tick = context.system.scheduler.schedule(0 millis, 30 seconds, self, Tick)

  override def postStop() = tick.cancel()

  var brickletMap: Map[String, (Bricklet, IPConnection)] = Map()

  def receive: Receive = {
    case Tick => {
      logger.debug("Enumerate TF stack...")
      brickletMap = Map()
      TFConnector.enumerate()
    }
    case Enumerate(bricklet, iPConnection) => {
      logger.debug(s"Adding bricklet with UID [${bricklet.uid}]")
      brickletMap = brickletMap + (bricklet.uid -> (bricklet, iPConnection))
    }
    case GetBricklets => {
      logger.debug("Someone is asking for all bricklets!")
      sender ! brickletMap.values.map(_._1).toSet
    }
    case GetBrickletsByIdentifier(identifier) =>
      sender ! brickletMap.filter(_._2._1.deviceIdentifier == identifier).map(_._2._1).toSet
    case GetBrickletByUid(uid) =>
      logger.debug(s"Retrieve UID [$uid]")
      sender ! brickletMap(uid)._1
    case GetIpConnectionByUid(uid) => sender ! brickletMap(uid)._2

    case _ => logger.warn("Received invalid message")
  }
}
