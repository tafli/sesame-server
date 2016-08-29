package models

import actors.{DualRelayActor, MasterBrickActor, RootActor}
import akka.actor.Props
import com.tinkerforge.IPConnection.EnumerateListener
import com.tinkerforge.{BrickMaster, IPConnection, IPConnectionBase}
import utils.Configuration

import scala.concurrent.Future

object TFConnector {
  lazy val ipcon = new IPConnection(); // Create IP connection

  ipcon.connect(Configuration.tfHost, Configuration.tfPort)

  ipcon.addEnumerateListener(
    new EnumerateListener {
      override def enumerate(uid: String, connectedUid: String, position: Char, hardwareVersion: Array[Short], firmwareVersion: Array[Short], deviceIdentifier: Int, enumerationType: Short): Unit = {
        println("UID:               " + uid)
        println("Enumeration Type:  " + enumerationType)

        if(enumerationType == IPConnectionBase.ENUMERATION_TYPE_DISCONNECTED) {
          println("")
          return
        }
      }
    }
  )
  ipcon.enumerate()
}

/**
  * Created by Andreas Boss on 23.08.16.
  */
object MasterBrick {
  import RootActor.system.dispatcher

  def fetchInformation(uid: String): Future[MasterBrickActor.BrickData] = Future {
    TFConnector.ipcon.enumerate

    val master = new BrickMaster(uid, TFConnector.ipcon); // Create device object
    val apiVersion = s"${master.getAPIVersion()(0)}.${master.getAPIVersion()(1)}.${master.getAPIVersion()(2)}"

    MasterBrickActor.BrickData(apiVersion, master.getStackVoltage, master.getChipTemperature / 10)
  }
}

object DualRelayBricklet {
  val dualRelayActor = RootActor.system.actorOf(Props[DualRelayActor])

  def setState(uid: String, relay: Short) = dualRelayActor ! DualRelayActor.SetState(uid, relay)
}