package actors

import actors.DualRelayActor.SetState
import akka.actor.{Actor, Props}
import com.tinkerforge.BrickletDualRelay
import models.TFConnector

/**
  * Created by abos on 27/08/16.
  */
object DualRelayActor {
  def props: Props = Props(new MasterBrickActor)

  case class BrickUid(uid: String)

  case class SetState(uid: String, relay: Short)

}

class DualRelayActor extends Actor {
  def receive: Receive = {
    case SetState(uid: String, relay: Short) => {
      val bricklet = new BrickletDualRelay(uid, TFConnector.ipcon);

      bricklet.setMonoflop(relay, true, 500)
    }
  }
}
