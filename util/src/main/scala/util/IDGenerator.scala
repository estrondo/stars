package util

import java.util.concurrent.ThreadLocalRandom

object IDGenerator
    extends IDGenerator({
      val builder = IndexedSeq.newBuilder[Char]
      builder.addAll('a' to 'z')
      builder.addAll('0' to '9')
      builder.addAll('A' to 'Z')
      builder.result()
    })

class IDGenerator(charSeq: IndexedSeq[Char]) {

  def generate(length: Int = 8): String = {
    require(length > 0, "Invalid length!")
    val random = ThreadLocalRandom.current()
    val builder = new StringBuilder()
    for (_ <- 0 until length)
      builder.addOne(charSeq(random.nextInt(charSeq.length)))

    builder.result()
  }
}
