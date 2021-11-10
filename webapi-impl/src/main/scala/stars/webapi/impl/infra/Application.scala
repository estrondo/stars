package stars.webapi.impl.infra

import com.lightbend.lagom.scaladsl.cluster.ClusterComponents
import com.lightbend.lagom.scaladsl.persistence.slick.SlickPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext}
import play.api.db.HikariCPComponents
import play.api.libs.ws.ahc.AhcWSComponents

abstract class Application(context: LagomApplicationContext)
  extends LagomApplication(context)
    with WebAPIComponent
    with SimulatorComponent
    with KafkaSimulationDispatcherComponent
    with AhcWSComponents
    with ClusterComponents
    with HikariCPComponents
    with SlickPersistenceComponents {


}
