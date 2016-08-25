package actors

import actors.MasterBrickActor.BrickUid
import akka.actor.{Actor, Props}

/**
  * Created by Andreas Boss on 24.08.16.
  */
object MasterBrickActor {
  def props: Props = Props(new MasterBrickActor)

  case class BrickUid(uid: String)
}

class MasterBrickActor extends Actor {

  def receive: Receive = {
    case BrickUid(uid: String) => {
      sender ! s"Received: $uid sent 456"
    }
  }
}