package utils

import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.JavaConverters._

object Configuration {
  val conf: Config = ConfigFactory.load

  val tfConnections: Seq[Connection] = for {
    connection <- conf.getConfigList("tinkerforge.connections").asScala
  } yield Connection(connection.getString("host"), connection.getInt("port"))

  val nfcUID: String = conf.getString("tinkerforge.bricklets.nfc.uid")
  val doorUID: String = conf.getString("tinkerforge.bricklets.dualRelay.uid")

  def tagIDs = conf.getStringList("tinkerforge.bricklets.nfc.tags").asScala
}

case class Connection(host: String, port: Int)
