package webapi.impl.component

import com.lightbend.lagom.scaladsl.cluster.ClusterComponents
import com.lightbend.lagom.scaladsl.persistence.slick.SlickPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext}
import play.api.db.HikariCPComponents
import play.api.libs.ws.ahc.AhcWSComponents

abstract class Application(context: LagomApplicationContext)
  extends LagomApplication(context)
    with WebAPIComponent
    with KafkaSimulationCommandDispatcher
    with AhcWSComponents
    with ClusterComponents
    with HikariCPComponents
    with SlickPersistenceComponents
    with CommandRegionComponent {


}
