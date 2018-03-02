package models

import javax.inject.{Inject, Singleton}

import actors._
import akka.pattern.ask
import akka.util.Timeout
import com.tinkerforge.IPConnection
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import play.api.libs.json._
import utils.Configuration

import scala.concurrent.Await
import scala.concurrent.duration._

object TFConnector {
  def enumerate(): Unit = ipConnections.foreach(_.enumerate())

  val ipConnections: Seq[IPConnection] = Configuration.tfConnections.map {
    tfConnection =>
      val ipcon = new IPConnection
      ipcon.connect(tfConnection.host, tfConnection.port)

      ipcon.addEnumerateListener(
        (uid: String,
         connectedUid: String,
         position: Char,
         hardwareVersion: Array[Short],
         firmwareVersion: Array[Short],
         deviceIdentifier: Int,
         enumerationType: Short) => {
          val bricklet = Bricklet(
            uid,
            connectedUid,
            position.toString,
            hardwareVersion,
            firmwareVersion,
            deviceIdentifier,
            enumerationType
          )
          StackActor.actor ! StackActor.Enumerate(bricklet, ipcon)
        })

      ipcon
  }
}

/**
  * This class is initialized at application start and send an enumarate command
  * to EnumeraterActor to get all connected bricklets.
  */
@Singleton
class TFConnector @Inject()(lifecycle: ApplicationLifecycle) {
  Logger.debug("Started TFConnector...")
  StackActor.actor ! StackActor.Tick
  NFCReaderActor.actor ! NFCReaderActor.ReadTagId(Configuration.nfcUID)
}

object Bricklet {
  implicit val brickletReads = Json.reads[Bricklet]
  implicit val brickletWrites = Json.writes[Bricklet]

  def getByIdentifier(identifier: Int): Set[Bricklet] = {
    implicit val timeout = Timeout(1 seconds)
    Await
      .result(
        (StackActor.actor ? StackActor.GetBricklets).mapTo[Set[Bricklet]],
        2 seconds
      )
      .filter(_.deviceIdentifier == identifier)
  }

  def getByUid(uid: String): Bricklet = {
    implicit val timeout = Timeout(1 seconds)
    Await
      .result(
        (StackActor.actor ? StackActor.GetBrickletByUid(uid)).mapTo[Bricklet],
        2 seconds
      )
  }

  def getIpConnectionByUid(uid: String): IPConnection = {
    implicit val timeout = Timeout(1 seconds)
    Await
      .result(
        (StackActor.actor ? StackActor.GetIpConnectionByUid(uid)).mapTo[IPConnection],
        2 seconds
      )
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

object DualRelayBricklet {
  def setState(uid: String, relay: Short) =
    DualRelayActor.actor ! DualRelayActor.SetState(uid, relay)
}
