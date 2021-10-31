package stars.webapi.impl.infra

import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceLocator}
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader}
import stars.webapi.{SimulationService, SimulatorService}

class ApplicationLoader extends LagomApplicationLoader {

  override def describeService: Option[Descriptor] = Some(readDescriptor[SimulationService])

  override def load(context: LagomApplicationContext): LagomApplication = {
    new Application(context) {
      override def serviceLocator: ServiceLocator = ServiceLocator.NoServiceLocator
    }
  }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication = {
    new Application(context) with LagomDevModeComponents
  }
}
