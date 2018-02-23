package actors

import actors.MasterBrickActor.BrickUid
import akka.actor.{Actor, Props}
import com.tinkerforge.BrickMaster
import models.TFConnector
import play.Logger

object MasterBrickActor {
  def props: Props = Props(new MasterBrickActor)

  case class BrickUid(uid: String)

  case class BrickData(api: String, voltage: Int, temp: Double)

}

class MasterBrickActor extends Actor {

  def receive: Receive = {
    case BrickUid(uid: String) => {

      val master = new BrickMaster(uid, TFConnector.ipcon);

      val apiVersion =
        s"${master.getAPIVersion()(0)}.${master.getAPIVersion()(1)}.${master
          .getAPIVersion()(2)}"

      val data = MasterBrickActor
        .BrickData(apiVersion, master.getStackVoltage, master.getChipTemperature / 10)

      Logger.debug(s"Sending Back [$data]")

      sender ! data
    }
    case _ => Logger.warn("Received invalid message!")
  }
}
