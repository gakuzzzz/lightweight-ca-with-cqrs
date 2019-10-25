package application

import play.api.ApplicationLoader.Context
import play.api.{ApplicationLoader, LoggerConfigurator}

class ConsoleApplicationLoader  extends ApplicationLoader {
  override def load(context: Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach(_.configure(context.environment))

    new ConsoleComponents(context).application
  }
}