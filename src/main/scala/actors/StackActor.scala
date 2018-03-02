package actors

import actors.StackActor._
import akka.actor.{Actor, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.tinkerforge.IPConnection
import models.{Bricklet, TFConnector}
import play.Logger

import scala.concurrent.duration._

object StackActor {
  val actor = RootActor.system.actorOf(Props[StackActor])

  def props: Props = Props(new MasterBrickActor)

  case class Tick()

  case class Enumerate(bricklet: Bricklet, iPConnection: IPConnection)

  case class GetBricklets()

  case class GetBrickletByUid(uid: String)

  case class GetIpConnectionByUid(uid: String)

}

class StackActor extends Actor {

  import context.dispatcher

  val tick = context.system.scheduler.schedule(0 millis, 30 seconds, self, Tick)

  override def postStop() = tick.cancel()

  var brickletMap: Map[String, (Bricklet, IPConnection)] = Map()

  def receive: Receive = {
    case Tick => {
      Logger.debug("Enumerate TF stack...")
      brickletMap = Map()
      TFConnector.enumerate()
    }
    case Enumerate(bricklet, iPConnection) => {
      Logger.debug(s"Adding bricklet with UID [${bricklet.uid}]")
      brickletMap = brickletMap + (bricklet.uid -> (bricklet, iPConnection))
    }
    case GetBricklets => {
      Logger.debug("Someone is asking for all bricklets!")
      implicit val timeout = Timeout(2 seconds)
      self ? Tick
      sender ! brickletMap.values.map(_._1).toSet
    }
    case GetBrickletByUid(uid) =>
      Logger.debug(s"Retrieve UID [$uid]")
      sender ! brickletMap(uid)._1
    case GetIpConnectionByUid(uid) => sender ! brickletMap(uid)._2

    case _ => Logger.warn("Received invalid message")
  }
}
