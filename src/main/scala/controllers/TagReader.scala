package controllers

import com.tinkerforge.BrickletNFCRFID
import javax.inject.Inject
import models.DualRelayBricklet
import org.apache.commons.lang3.StringUtils
import play.api.Logging
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.Configuration

object TagReader extends Logging{
  def checkAndOpenDoor(tagId: BrickletNFCRFID#TagID): Unit = {
    logger.debug(s"TagID: [${convertTagId(tagId)}]")

    Configuration.tagIDs.filter(_.equals(convertTagId(tagId))).foreach { _ =>
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
