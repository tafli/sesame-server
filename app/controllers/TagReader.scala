package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import com.tinkerforge.BrickletNFCRFID
import models.DualRelayBricklet
import org.apache.commons.lang3.StringUtils
import play.api.mvc.Controller
import utils.Configuration

/**
  * Created by abos on 10/09/16.
  */
object TagReader {
  def checkAndOpenDoor(tagId: BrickletNFCRFID#TagID) = {
    println(s"TagID: [${convertTagId(tagId)}]")

    convertTagId(tagId) match {
      case "04D92F9AA54880" => DualRelayBricklet.setState(Configuration.doorUID, 1)
      case _ =>
    }
  }

  def convertTagId(tagId: BrickletNFCRFID#TagID): String = {
    (for {
      i <- 0 until tagId.tidLength
    } yield StringUtils.leftPad(Integer.toHexString(tagId.tid(i)).toUpperCase, 2, "0"))
      .mkString
  }
}

@Singleton
class TagReader @Inject() extends Controller
