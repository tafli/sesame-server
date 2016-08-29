package models

import actors.{DualRelayActor, MasterBrickActor, RootActor}
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import com.tinkerforge.{IPConnection, IPConnectionBase}
import com.tinkerforge.IPConnection.EnumerateListener
import utils.Configuration

import scala.concurrent.Await
import scala.concurrent.duration._

object TFConnector {
  lazy val ipcon = new IPConnection(); // Create IP connection

  println("Connecting...")
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

        println(s"Connected UID:     $connectedUid")
        println(s"Position:          $position")
        println(s"Hardware Version:  $hardwareVersion(0).$hardwareVersion(1).$hardwareVersion(2)")
        println(s"Firmware Version:  $firmwareVersion(0).$firmwareVersion(1).$firmwareVersion(2)")
        println(s"Device Identifier: $deviceIdentifier")
        println("")
      }
    }
  )
  ipcon.enumerate()
}

/**
  * Created by boss on 23.08.16.
  */
object MasterBrick {
  val masterBrickActor = RootActor.system.actorOf(Props[MasterBrickActor])

  def fetchInformation(uid: String): MasterBrickActor.BrickData = {
    implicit val timeout = Timeout(5 seconds)
    val future = (masterBrickActor ? MasterBrickActor.BrickUid(uid)).mapTo[MasterBrickActor.BrickData]
    val data = Await.result(future, 5 second)
    println(s"Result: $data")

    data
  }
}

object DualRelayBricklet {
  val dualRelayActor = RootActor.system.actorOf(Props[DualRelayActor])

  def setState(uid: String, relay: Short) = dualRelayActor ! DualRelayActor.SetState(uid, relay)
}