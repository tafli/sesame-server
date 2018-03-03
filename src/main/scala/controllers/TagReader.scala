package controllers

import javax.inject.Inject

import com.tinkerforge.BrickletNFCRFID
import models.DualRelayBricklet
import org.apache.commons.lang3.StringUtils
import play.Logger
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.Configuration

object TagReader {
  def checkAndOpenDoor(tagId: BrickletNFCRFID#TagID) = {
    Logger.debug(s"TagID: [${convertTagId(tagId)}]")

    Configuration.tagIDs.filter(_.equals(convertTagId(tagId))).foreach { tagId =>
      DualRelayBricklet.setState(Configuration.doorUID, 1)
    }
  }

  def convertTagId(tagId: BrickletNFCRFID#TagID): String = {
    (for {
      i <- 0 until tagId.tidLength
    } yield StringUtils.leftPad(Integer.toHexString(tagId.tid(i)).toUpperCase, 2, "0")).mkString
  }
}

class TagReader @Inject() (cc:ControllerComponents) extends AbstractController(cc) {}
