package webapi

import akka.actor.typed.{ActorSystem, Extension, ExtensionId}
import akka.http.scaladsl.model.{ContentType, HttpCharsets, MediaType}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.softwaremill.macwire.wire

object DefaultRoute extends ExtensionId[DefaultRoute] {

  override def createExtension(system: ActorSystem[_]): DefaultRoute = {
    wire[DefaultRoute]
  }
}

class DefaultRoute(system: ActorSystem[_]) extends Extension {

  def route: Route = path("openapi.yml") {
    getFromResource("openapi.yml")(_ => ContentType.WithCharset(MediaType.text("yaml"), HttpCharsets.`UTF-8`))
  }
}
