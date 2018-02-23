import com.google.inject.AbstractModule
import models.TFConnector

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[TFConnector]).asEagerSingleton()
  }
}