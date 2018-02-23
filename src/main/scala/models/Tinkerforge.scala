package models

import javax.inject.{Inject, Singleton}

import actors._
import akka.pattern.ask
import akka.util.Timeout
import com.tinkerforge.IPConnection.EnumerateListener
import com.tinkerforge.{BrickMaster, IPConnection}
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.Json
import utils.Configuration

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object TFConnector {
  val ipcon = new IPConnection
  ipcon.connect(Configuration.tfHost, Configuration.tfPort)

  ipcon.addEnumerateListener((uid: String, connectedUid: String, position: Char, hardwareVersion: Array[Short], firmwareVersion: Array[Short], deviceIdentifier: Int, enumerationType: Short) => {
    val bricklet = Bricklet(
      uid,
      connectedUid,
      position.toString,
      hardwareVersion,
      firmwareVersion,
      deviceIdentifier,
      enumerationType
    )
    EnumerationActor.actor ! EnumerationActor.Enumerate(bricklet)
  })
}

/**
  * This class is initialized at application start and send an enumarate command
  * to EnumeraterActor to get all connected bricklets.
  */
@Singleton
class TFConnector @Inject()(lifecycle: ApplicationLifecycle) {
  Logger.debug("Started TFConnector...")
  EnumerationActor.actor ! EnumerationActor.Tick
  NFCReaderActor.actor ! NFCReaderActor.ReadTagId(Configuration.nfcUID)
}

object Bricklet {
  implicit val brickletReads = Json.reads[Bricklet]
  implicit val brickletWrites = Json.writes[Bricklet]

  def getBrickletByIdentifier(identifier: Int): Set[Bricklet] = {
    implicit val timeout = Timeout(1 seconds)
    Await
      .result(
        (EnumerationActor.actor ? EnumerationActor.GetBricklets).mapTo[Set[Bricklet]],
        2 seconds
      )
      .filter(_.deviceIdentifier == identifier)
  }

  def getBrickletByUid(uid: String): Option[Bricklet] = {
    implicit val timeout = Timeout(1 seconds)
    Await
      .result(
        (EnumerationActor.actor ? EnumerationActor.GetBricklets).mapTo[Set[Bricklet]],
        2 seconds
      )
      .find(_.uid == uid)
  }
}

case class Bricklet(uid: String,
                    connectedUid: String,
                    position: String,
                    hardwareVersion: Array[Short],
                    firmwareVersion: Array[Short],
                    deviceIdentifier: Int,
                    enumerationType: Short) {

  // Overwrite equals to only check UID
  override def equals(o: scala.Any): Boolean = o match {
    case other: Bricklet => uid == other.uid
    case _ => false
  }
}


object MasterBrick {
  import RootActor.system.dispatcher

  def fetchInformation(uid: String): Future[MasterBrickActor.BrickData] = Future {
    val master = new BrickMaster(uid, TFConnector.ipcon);
    // Create device object
    val apiVersion =
      s"${master.getAPIVersion()(0)}.${master.getAPIVersion()(1)}.${master.getAPIVersion()(2)}"

    MasterBrickActor
      .BrickData(apiVersion, master.getStackVoltage, master.getChipTemperature / 10)
  }
}

object DualRelayBricklet {
  def setState(uid: String, relay: Short) =
    DualRelayActor.actor ! DualRelayActor.SetState(uid, relay)
}

object NFCReaderBricklet {

}
