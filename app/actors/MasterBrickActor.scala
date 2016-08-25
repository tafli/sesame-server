package actors

import akka.actor.{Actor, Props}

import scala.concurrent.Future

/**
  * Created by Andreas Boss on 24.08.16.
  */
object MasterBrickActor {
  def props: Props = Props(new MasterBrickActor)
}

class MasterBrickActor extends Actor {
  import context.dispatcher
  val f = Future("hello")
  def receive: Receive = {
    case uid: String => receiveData(uid)
  }

  private def receiveData(uid: String) = {
    println(s"Fetching data from Master Brick with uid: $uid")
    Seq("123","456")
  }
}