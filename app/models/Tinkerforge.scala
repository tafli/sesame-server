package models

import javax.inject.{Inject, Singleton}

import actors.{DualRelayActor, EnumerationActor, MasterBrickActor, RootActor}
import akka.actor.Props
import com.tinkerforge.IPConnection.EnumerateListener
import com.tinkerforge.{BrickMaster, IPConnection}
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.Json
import utils.Configuration

import scala.concurrent.Future

object TFConnector {
  val ipcon = new IPConnection
  ipcon.connect(Configuration.tfHost, Configuration.tfPort)

  ipcon.addEnumerateListener(new EnumerateListener {
    override def enumerate(uid: String, connectedUid: String, position: Char, hardwareVersion: Array[Short], firmwareVersion: Array[Short], deviceIdentifier: Int, enumerationType: Short): Unit = {
      val bricklet = Bricklet(uid, connectedUid, position, hardwareVersion, firmwareVersion, deviceIdentifier, enumerationType)
      EnumerationActor.actor ! EnumerationActor.Enumerate(bricklet)
    }
  })
}

@Singleton
class TFConnector @Inject()(appLifecycle: ApplicationLifecycle) {
  println("Starting up...")

  EnumerationActor.actor ! EnumerationActor.Tick

  appLifecycle.addStopHook { () =>
    print("Stopping application")
    Future.successful(())
  }
}

case class Bricklet(
                     uid: String,
                     connectedUid: String,
                     position: Char,
                     hardwareVersion: Array[Short],
                     firmwareVersion: Array[Short],
                     deviceIdentifier: Integer,
                     enumerationType: Short
                   ) {
  def toJson = Json.obj(
    "uid" -> uid,
    "connected_uid" -> connectedUid,
    "position" -> position.toString,
    "hardware-version" -> s"${hardwareVersion(0)}.${hardwareVersion(1)}.${hardwareVersion(2)}",
    "firmware-version" -> s"${firmwareVersion(0)}.${firmwareVersion(1)}.${firmwareVersion(2)}",
    "device_identifier" -> deviceIdentifier.toString,
    "enumeration_type" -> enumerationType
  )

  // Overwrite equals to only check UID
  override def equals(o: scala.Any): Boolean = o match {
    case other: Bricklet => uid == other.uid
    case _ => false
  }
}

/**
  * Created by Andreas Boss on 23.08.16.
  */
object MasterBrick {

  import RootActor.system.dispatcher

  def fetchInformation(uid: String): Future[MasterBrickActor.BrickData] = Future {
    val master = new BrickMaster(uid, TFConnector.ipcon);
    // Create device object
    val apiVersion = s"${master.getAPIVersion()(0)}.${master.getAPIVersion()(1)}.${master.getAPIVersion()(2)}"

    MasterBrickActor.BrickData(apiVersion, master.getStackVoltage, master.getChipTemperature / 10)
  }
}

object DualRelayBricklet {
  val dualRelayActor = RootActor.system.actorOf(Props[DualRelayActor])

  def setState(uid: String, relay: Short) = dualRelayActor ! DualRelayActor.SetState(uid, relay)
}