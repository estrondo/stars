package stars.simulator

import akka.actor.typed.ActorRef
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.typed.scaladsl.{ActorSink, ActorSource}
import akka.stream.{Materializer, OverflowStrategy}
import akka.{Done, NotUsed}

object StreamHelper {

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

  def toShardRegion[T, Mat](source: Source[T, Mat], region: ActorRef[ShardingEnvelope[T]])
    (extractId: T => Option[String])(implicit materializer: Materializer): Mat = {

    source
      .to(Sink.foreach { message =>
        val option = extractId(message)
        if (option.isDefined)
          region ! ShardingEnvelope(option.get, message)
      })
      .run()
  }
}
