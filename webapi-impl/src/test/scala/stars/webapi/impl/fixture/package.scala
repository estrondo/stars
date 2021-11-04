package stars.webapi.impl

import scala.util.Random

package object fixture {

  def newRandomId(): String = {
    val bytes = Array.ofDim[Byte](16)
    Random.nextBytes(bytes)
    BigInt(bytes).abs.toString(32)
  }

}
