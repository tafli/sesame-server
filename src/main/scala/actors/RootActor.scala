package actors

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object RootActor {
  val system = ActorSystem("Sesame_Server_System", ConfigFactory.load("application"))
}
