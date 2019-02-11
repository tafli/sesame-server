package actors

import actors.MasterBrickActor.BrickUid
import akka.actor.{Actor, Props}
import com.tinkerforge.BrickMaster
import models.Bricklet
import play.api.Logging

import scala.util.{Failure, Success}

object MasterBrickActor {
  def props: Props = Props(new MasterBrickActor)

  case class BrickUid(uid: String)

  case class BrickData(api: String, voltage: Int, temp: Double)

}

class MasterBrickActor extends Actor with Logging {

  import scala.concurrent.ExecutionContext.Implicits.global

  def receive: Receive = {
    case BrickUid(uid: String) => {

      Bricklet.getIpConnectionByUid(uid).onComplete {
        case Success(ipCon) =>
          val master = new BrickMaster(uid, ipCon)
          val apiVersion = s"${master.getAPIVersion()(0)}.${master.getAPIVersion()(1)}.${master.getAPIVersion()(2)}"

          val data = MasterBrickActor
            .BrickData(apiVersion, master.getStackVoltage, master.getChipTemperature / 10)

          sender ! data
        case Failure(ex) =>
      }
    }
    case _ => logger.warn("Received invalid message!")
  }
}
