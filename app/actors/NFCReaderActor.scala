package actors

import actors.NFCReaderActor.ReadTagId
import akka.actor.{Actor, Props}
import com.tinkerforge.BrickletNFCRFID
import com.tinkerforge.BrickletNFCRFID.StateChangedListener
import controllers.TagReader
import models.TFConnector

import scala.util.{Failure, Success, Try}

/**
  * Created by abos on 10/09/16.
  */
object NFCReaderActor {
  val actor = RootActor.system.actorOf(Props[NFCReaderActor])
  def props: Props = Props(new MasterBrickActor)

  case class ReadTagId(uid: String)
}

class NFCReaderActor extends Actor {

  def receive: Receive = {
    case ReadTagId(uid: String) => {
      val bricklet = new BrickletNFCRFID(uid, TFConnector.ipcon)
      var currentTagType = BrickletNFCRFID.TAG_TYPE_MIFARE_CLASSIC

      bricklet.addStateChangedListener(new StateChangedListener {
        override def stateChanged(state: Short, idle: Boolean): Unit = {
          Try {
            if (idle) {
              currentTagType = ((currentTagType + 1) % 3).toShort
              bricklet.requestTagID(currentTagType)
            }

            if (state == BrickletNFCRFID.STATE_REQUEST_TAG_ID_READY) {
              bricklet.getTagID
            }
          } match {
            case Success(tagId: BrickletNFCRFID#TagID) =>
              TagReader.checkAndOpenDoor(tagId)
            case Success(tag) =>
            case Failure(e) => println(s"Failed with Exception $e")
          }
          Thread.sleep(100)
        }
      })
      bricklet.requestTagID(BrickletNFCRFID.TAG_TYPE_MIFARE_CLASSIC)
    }
    case _ => println("Received invalid message")
  }
}
