package utils

import com.typesafe.config.ConfigFactory

/**
  * Created by Andreas Boss on 28.08.16.
  */
object Configuration {
  val conf = ConfigFactory.load

  val tfHost = conf.getString("tinkerforge.connection.host")
  val tfPort = conf.getInt("tinkerforge.connection.port")
}
