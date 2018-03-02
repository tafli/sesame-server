package utils

import com.typesafe.config.{Config, ConfigFactory, ConfigObject}

import scala.collection.JavaConverters._

object Configuration {

  val conf: Config = ConfigFactory.load


  val tfConnections: Seq[Connection] = for {
    connection <- conf.getConfigList("tinkerforge.connections").asScala
  } yield Connection(connection.getString("host"), connection.getInt("port"))

  val nfcUID: String = "uvw"
  val doorUID: String = "kAz"
}

case class Connection(host: String, port: Int)
