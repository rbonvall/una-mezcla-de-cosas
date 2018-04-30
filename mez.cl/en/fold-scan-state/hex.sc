// vim: ft=scala

sealed trait Direction
case object N  extends Direction
case object NW extends Direction
case object NE extends Direction
case object S  extends Direction
case object SW extends Direction
case object SE extends Direction

object Direction {
  val fromString: PartialFunction[String, Direction] = {
    case "n"  ⇒ N
    case "nw" ⇒ NW
    case "ne" ⇒ NE
    case "s"  ⇒ S
    case "sw" ⇒ SW
    case "se" ⇒ SE
    case x => throw new Exception(s">$x<")
  }
}

object NotImplemented {
  // BEGIN HexPosNotImplemented
  case class HexPos(i: Int, j: Int) {
    def moveTo(d: Direction): HexPos = ???
    def distanceToOrigin: Int = ???
  }
  object HexPos {
    val origin = HexPos(0, 0)
  }
  // END HexPosNotImplemented
}

object Implemented {

  val directions =
    io.Source.fromFile("directions.txt")
      .mkString
      .split(",")
      .map(_.trim)
      .map(Direction.fromString)
      .toSeq

  case class HexPos(i: Int, j: Int) {
    // BEGIN MoveTo
    def moveTo(d: Direction): HexPos = d match {
      case N  ⇒ HexPos(i + 1, j + 1)
      case NW ⇒ HexPos(i    , j + 1)
      case NE ⇒ HexPos(i + 1, j    )
      case S  ⇒ HexPos(i - 1, j - 1)
      case SW ⇒ HexPos(i - 1, j    )
      case SE ⇒ HexPos(i    , j - 1)
    }
    // END MoveTo

    // BEGIN DistanceToOrigin
    import scala.math.abs
    def distanceToOrigin: Int =
      if (i * j > 0) abs(i) max abs(j)
      else           abs(i)  +  abs(j)
    // END DistanceToOrigin
  }
  object HexPos {
    val origin = HexPos(0, 0)
  }

  val destination = directions.foldLeft(HexPos.origin)(_ moveTo _)
  val path        = directions.scanLeft(HexPos.origin)(_ moveTo _)

  println {
    // BEGIN SolutionPartOne
    directions
      .foldLeft (HexPos.origin) { (pos, dir) ⇒ pos.moveTo(dir) }
      .distanceToOrigin
    // END SolutionPartOne
  }

  println {
    // BEGIN SolutionPartTwo
    directions
      .scanLeft (HexPos.origin) { (pos, dir) ⇒ pos.moveTo(dir) }
      .maxBy { _.distanceToOrigin }
      .distanceToOrigin
    // END SolutionPartTwo
  }
}

NotImplemented
Implemented


