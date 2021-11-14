package stars

import scala.util.Random

package object fixture {

  def newRandomId(): String = {
    val bytes = Array.ofDim[Byte](32)
    Random.nextBytes(bytes)
    BigInt(bytes).abs.toString(32)
  }
}
