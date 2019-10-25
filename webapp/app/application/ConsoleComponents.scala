package application

import com.softwaremill.macwire.wire
import controllers.{AssetsComponents, HomeController}
import org.flywaydb.core.api.{FlywayException, MigrationState}
import play.api.ApplicationLoader.Context
import play.api.{BuiltInComponentsFromContext, Mode}
import play.api.mvc.EssentialFilter
import play.filters.HttpFiltersComponents
import play.filters.cors.CORSComponents
import play.api.cache.caffeine.CaffeineCacheComponents
import org.flywaydb.play.FlywayPlayComponents
import play.api.routing.Router
import router.Routes
import scalikejdbc.{PlayFixture, PlayInitializer}

class ConsoleComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
    with HttpFiltersComponents
    with CORSComponents
    with AssetsComponents
    with FlywayPlayComponents
    with CaffeineCacheComponents {

  override def httpFilters: Seq[EssentialFilter] = Seq(
    csrfFilter,
    // TODO
    //securityHeadersFilter,
    //allowedHostsFilter,
    //corsFilter
  )

  lazy val router: Router = {
    val prefix: String = httpConfiguration.context
    wire[Routes]
  }

  lazy val homeController: HomeController = wire[HomeController]

  // ここから下は起動順序に依存性があるため、val で順番通りに初期化する
  val flywayPlugin = flywayPlayInitializer
  if (environment.mode == Mode.Dev) {
    flyways.allDatabaseNames.foreach { dbName =>
      try {
        flyways.migrate(dbName)
      } catch {
        case e: FlywayException =>
          flyways.clean(dbName)
          flyways.migrate(dbName)
      }
    }
  }

  val scalikejdbcPlayInitializer: PlayInitializer = wire[PlayInitializer]

  val migrationStatus = for {
    name <- flyways.allDatabaseNames
    info <- flyways.allMigrationInfo(name)
  } yield info.getState

  if (migrationStatus.forall(_ == MigrationState.SUCCESS)) {
    wire[PlayFixture]
  }

}
