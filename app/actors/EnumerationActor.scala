package actors

import actors.EnumerationActor.{Enumerate, GetBricklets, Tick}
import akka.actor.{Actor, Props}
import akka.pattern.ask
import akka.util.Timeout
import models.{Bricklet, TFConnector}

import scala.concurrent.duration._

/**
  * Created by Andreas Boss on 29.08.16.
  */
object EnumerationActor {
  val actor = RootActor.system.actorOf(Props[EnumerationActor])

  def props: Props = Props(new MasterBrickActor)

  case class Tick()

  case class Enumerate(bricklet: Bricklet)

  case class GetBricklets()

}

class EnumerationActor extends Actor {

  import context.dispatcher

  val tick = context.system.scheduler.scheduleOnce(0 millis, self, Tick)

  override def postStop() = tick.cancel()

  var brickletList: Set[Bricklet] = Set()

  def receive: Receive = {
    case Tick => {
      import context.dispatcher
      context.system.scheduler.scheduleOnce(10000 millis, self, Tick)
      TFConnector.ipcon.enumerate()
    }
    case Enumerate(bricklet) => {
      brickletList = brickletList + bricklet
    }
    case GetBricklets => {
      implicit val timeout = Timeout(1 seconds)
      self ? Tick
      sender ! brickletList
    }
    case _ => println("Received invalid message")
  }
}
