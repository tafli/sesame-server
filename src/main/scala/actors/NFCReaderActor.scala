package actors

import actors.NFCReaderActor.ReadTagId
import akka.actor.{Actor, ActorRef, Props}
import com.tinkerforge.BrickletNFCRFID
import controllers.TagReader
import models.Bricklet
import play.api.Logging

import scala.util.{Failure, Success, Try}

object NFCReaderActor {
  val actor: ActorRef = RootActor.system.actorOf(Props[NFCReaderActor])

  def props: Props = Props(new MasterBrickActor)

  case class ReadTagId(uid: String)
}

class NFCReaderActor extends Actor with Logging {
  import scala.concurrent.ExecutionContext.Implicits.global

  def receive: Receive = {
    case ReadTagId(uid: String) => {
      logger.debug("Start reading tags")

      Bricklet.getIpConnectionByUid(uid).foreach { bricklet =>
        val nfcBricklet = new BrickletNFCRFID(uid, bricklet)
        var currentTagType = BrickletNFCRFID.TAG_TYPE_MIFARE_CLASSIC

        nfcBricklet.addStateChangedListener((state: Short, idle: Boolean) => {
          Try {
            if (idle) {
              currentTagType = ((currentTagType + 1) % 3).toShort
              nfcBricklet.requestTagID(currentTagType)
            }

            if (state == BrickletNFCRFID.STATE_REQUEST_TAG_ID_READY) {
              nfcBricklet.getTagID
            }
          } match {
            case Success(tagId: BrickletNFCRFID#TagID) =>
              TagReader.checkAndOpenDoor(tagId)
            case Success(tag) =>
            case Failure(e) => logger.error(s"Failed with Exception $e")
          }
          Thread.sleep(100)
        })
        nfcBricklet.requestTagID(BrickletNFCRFID.TAG_TYPE_MIFARE_CLASSIC)
      }
    }
    case _ => logger.warn("Received invalid message")
  }
}
