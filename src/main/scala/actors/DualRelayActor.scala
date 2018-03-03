package actors

import actors.DualRelayActor.SetState
import akka.actor.{Actor, Props}
import com.tinkerforge.BrickletDualRelay
import models.Bricklet
import play.Logger

object DualRelayActor {
  val actor = RootActor.system.actorOf(Props[DualRelayActor])
  def props: Props = Props(new MasterBrickActor)

  case class SetState(uid: String, relay: Short)
}

class DualRelayActor extends Actor {
  import scala.concurrent.ExecutionContext.Implicits.global

  def receive: Receive = {
    case SetState(uid: String, relay: Short) => {
      Logger.debug(s"Setting State of [$uid] to [true]")

      Bricklet.getIpConnectionByUid(uid).foreach { ipCon =>
        new BrickletDualRelay(uid, ipCon).setMonoflop(relay, true, 1000)
      }
    }

    case _ => Logger.warn("Received invalid message")
  }
}
