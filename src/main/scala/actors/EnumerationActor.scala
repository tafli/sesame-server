package actors

import actors.EnumerationActor.{Enumerate, GetBricklets, Tick}
import akka.actor.{Actor, Props}
import akka.pattern.ask
import akka.util.Timeout
import models.{Bricklet, TFConnector}
import play.Logger

import scala.concurrent.duration._

object EnumerationActor {
  val actor = RootActor.system.actorOf(Props[EnumerationActor])

  def props: Props = Props(new MasterBrickActor)

  case class Tick()
  case class Enumerate(bricklet: Bricklet)
  case class GetBricklets()
}

class EnumerationActor extends Actor {

  import context.dispatcher

  val tick = context.system.scheduler.schedule(0 millis, 30 seconds, self, Tick)
  override def postStop() = tick.cancel()

  var brickletList: Set[Bricklet] = Set()

  def receive: Receive = {
    case Tick => {
      Logger.debug("Enumerate TF stack...")
      brickletList = Set()
      TFConnector.ipcon.enumerate()
    }
    case Enumerate(bricklet) => {
      brickletList = brickletList + bricklet
    }
    case GetBricklets => {
      implicit val timeout = Timeout(2 seconds)
      self ? Tick
      sender ! brickletList
    }
    case _ => Logger.warn("Received invalid message")
  }
}
