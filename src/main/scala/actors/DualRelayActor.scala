package actors

import actors.DualRelayActor.SetState
import akka.actor.{Actor, Props}
import com.tinkerforge.BrickletDualRelay
import models.Bricklet
import play.Logger

object DualRelayActor {
  val actor = RootActor.system.actorOf(Props[DualRelayActor])
  def props: Props = Props(new MasterBrickActor)

  case class BrickUid(uid: String)

  case class SetState(uid: String, relay: Short)
}

class DualRelayActor extends Actor {
  def receive: Receive = {
    case SetState(uid: String, relay: Short) => {
      Logger.debug(s"Setting State of [$uid] to [true]")

      new BrickletDualRelay(uid, Bricklet.getIpConnectionByUid(uid)).setMonoflop(relay, true, 1000)
    }

    case _ => Logger.warn("Received invalid message")
  }
}
