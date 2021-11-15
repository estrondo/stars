package stars.simulator.kafka

import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityTypeKey}
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}

object SourceConnector {

  def toEntity[T, Mat](source: Source[T, Mat], typeKey: EntityTypeKey[T], sharding: ClusterSharding)
    (extractId: T => Option[String])(implicit materializer: Materializer): Mat = {

    source
      .to(Sink.foreach { message =>
        val option = extractId(message)
        if (option.isDefined)
          sharding.entityRefFor(typeKey, option.get).tell(message)
      })
      .run()
  }
}
