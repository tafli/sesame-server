package actors

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by Andreas Boss on 25/08/16.
  */
object RootActor {
  val system = ActorSystem("Sesame_Server_System", ConfigFactory.load("application"))
}
