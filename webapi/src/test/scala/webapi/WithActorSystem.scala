package webapi

import akka.actor.typed.ActorSystem

trait WithActorSystem {

  implicit def typedActorSystem: ActorSystem[_]
}
