package models

import actors.{DualRelayActor, MasterBrickActor, RootActor}
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import com.tinkerforge.IPConnection

import scala.concurrent.Await
import scala.concurrent.duration._

object TFConnector {
  lazy val ipcon = new IPConnection(); // Create IP connection

  println("Connecting...")
  ipcon.connect("localhost", 4223);
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