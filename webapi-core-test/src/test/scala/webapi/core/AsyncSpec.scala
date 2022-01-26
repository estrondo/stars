package webapi.core

import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.freespec.AsyncFreeSpecLike
import org.scalatest.matchers.should.Matchers

trait AsyncSpec extends AsyncFreeSpecLike with Matchers with AsyncMockFactory {}
