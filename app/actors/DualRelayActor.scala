package actors

import actors.DualRelayActor.{GetState, SetState}
import akka.actor.{Actor, Props}
import com.tinkerforge.BrickletDualRelay
import models.TFConnector

/**
  * Created by abos on 27/08/16.
  */
object DualRelayActor {
  val actor = RootActor.system.actorOf(Props[DualRelayActor])
  def props: Props = Props(new MasterBrickActor)

  case class BrickUid(uid: String)

  case class SetState(uid: String, relay: Short)
  case class GetState(uid: String)

}

class DualRelayActor extends Actor {
  def receive: Receive = {
    case SetState(uid: String, relay: Short) => {
      new BrickletDualRelay(uid, TFConnector.ipcon).setMonoflop(relay, true, 500)
    }
    case GetState(uid: String) => {
      val state = new BrickletDualRelay(uid, TFConnector.ipcon).getState
      sender ! (state.relay1, state.relay2)
    }
    case _ => println("Received invalid message")
  }
}
