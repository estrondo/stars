package simulator.impl.context

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, PoolRouter, Routers}
import akka.actor.typed.{ActorRef, SupervisorStrategy}
import simulator.impl.context.ContextGenerator.Message

object ContextGeneratorStarter {

  def apply(context: ActorContext[_]): (ActorRef[Message], ActorRef[Message]) = {
    val pool = createPool()
    val poolRef = context.spawn(pool, "context-generator-pool")
    context.system.receptionist ! Receptionist.Register(ContextGenerator.Key, poolRef)
    val group = Routers.group(ContextGenerator.Key)
    val groupRef = context.spawn(group, "context-generator-group")
    (poolRef, groupRef)
  }

  def createPool(size: Int = math.max(Runtime.getRuntime.availableProcessors(), 2)): PoolRouter[Message] = {
    Routers.pool(size) {
      Behaviors.supervise(ContextGenerator())
        .onFailure[Exception](SupervisorStrategy.restart)
    }
  }
}
