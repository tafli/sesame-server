import com.google.inject.AbstractModule
import models.TFConnector

/**
  * Created by abos on 30/08/16.
  */
class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[TFConnector]).asEagerSingleton()
  }
}