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

import scala.concurrent.Future
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

  implicit val timeout = Timeout(1 seconds)

  def getBricklets: Future[Set[Bricklet]] = {
    (StackActor.actor ? StackActor.GetBricklets).mapTo[Set[Bricklet]]
  }

  def getByIdentifier(identifier: Int): Future[Set[Bricklet]] = {
    (StackActor.actor ? StackActor.GetBrickletsByIdentifier(identifier)).mapTo[Set[Bricklet]]
  }

  def getByUid(uid: String): Future[Bricklet] = {
    (StackActor.actor ? StackActor.GetBrickletByUid(uid)).mapTo[Bricklet]
  }

  def getIpConnectionByUid(uid: String): Future[IPConnection] = {
    (StackActor.actor ? StackActor.GetIpConnectionByUid(uid)).mapTo[IPConnection]
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
