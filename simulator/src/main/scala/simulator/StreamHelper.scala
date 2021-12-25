package simulator

import akka.actor.typed.ActorRef
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.typed.scaladsl.{ActorSink, ActorSource}
import akka.stream.{Materializer, OverflowStrategy}
import akka.{Done, NotUsed}

object StreamHelper {

  class SourceHelper[A, Mat](source: Source[A, Mat]) {

    def toShardRegion[T](region: ActorRef[ShardingEnvelope[T]])
      (collector: A => Option[(String, T)])(implicit materializer: Materializer): Mat = {

      source
        .to(Sink.foreach { element =>
          collector(element) match {
            case Some((entityId, message)) =>
              region ! ShardingEnvelope(entityId, message)
            case _ =>
          }
        })
        .run()
    }
  }

  def apply[A, Mat](source: Source[A, Mat]): SourceHelper[A, Mat] = new SourceHelper(source)

  def createSinkWithoutReplyTo[T, M](ref: ActorRef[M], initial: M, onComplete: M)
    (messageAdapter: T => M, onFailureMessage: Throwable => M): Sink[T, NotUsed] = {

    ActorSink.actorRefWithBackpressure[T, M, Done](
      ref = ref,
      messageAdapter = (actor, message) => {
        actor.tell(Done)
        messageAdapter(message)
      },
      onInitMessage = actor => {
        actor.tell(Done)
        initial
      },
      onCompleteMessage = onComplete,
      onFailureMessage = onFailureMessage
    )
  }

  def createSourceActor[T]()(implicit materializer: Materializer): (ActorRef[T], Source[T, NotUsed]) = {
    ActorSource
      .actorRef[T](
        completionMatcher = PartialFunction.empty,
        failureMatcher = PartialFunction.empty,
        bufferSize = 25,
        overflowStrategy = OverflowStrategy.backpressure
      )
      .preMaterialize()
  }

}
