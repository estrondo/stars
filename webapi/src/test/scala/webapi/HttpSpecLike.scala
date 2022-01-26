package webapi

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import org.scalatest.freespec.AsyncFreeSpecLike
import org.scalatest.matchers.should.Matchers
import stars.webapi.client.ApiClient

import scala.concurrent.Future

trait HttpSpecLike extends AsyncFreeSpecLike with Matchers with WithActorSystem {

  protected def newRoute[T](route: Route)(block: ApiClient => Future[T]): Future[T] = {
    val binding = Http(typedActorSystem)
      .newServerAt("0.0.0.0", 0)
      .bind(route)

    val api = for (binding <- binding) yield {
      new ApiClient()
        .setHost("0.0.0.0")
        .setPort(binding.localAddress.getPort)
        .setBasePath("/")
        .setScheme("http")
    }

    api.flatMap(block)
  }
}
