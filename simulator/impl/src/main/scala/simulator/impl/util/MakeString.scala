package simulator.impl.util

class MakeString[T](iterable: Iterable[T], sep: String = ", ") {

  override def toString: String = iterable.mkString(sep)
}
