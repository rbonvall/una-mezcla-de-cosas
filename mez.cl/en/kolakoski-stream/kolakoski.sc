// vim: ft=scala

// BEGIN IntegerStream
val integers = Stream.from(0)
// END IntegerStream

object E1 {
  // BEGIN EvenIntegersWithFilter
  val evenIntegers = integers.filter(_ % 2 == 0)
  // END EvenIntegersWithFilter
}
object E2 {
  // BEGIN EvenIntegersWithMap
  val evenIntegers = integers.map(_ * 2)
  // END EvenIntegersWithMap
}
object E3 {
  // BEGIN EvenIntegersWithIterate
  val evenIntegers = Stream.iterate(0)(_ + 2)
  // END EvenIntegersWithIterate
}
object E4 {
  // BEGIN EvenIntegersRecursive
  val evenIntegers: Stream[Int] = 0 #:: evenIntegers.map(_ + 2)
  // END EvenIntegersRecursive
}

val firstEvenIntegers = 0 to 30 by 2
assert(E1.evenIntegers startsWith firstEvenIntegers)
assert(E2.evenIntegers startsWith firstEvenIntegers)
assert(E3.evenIntegers startsWith firstEvenIntegers)
assert(E4.evenIntegers startsWith firstEvenIntegers)

// BEGIN PeopleStream
val people = Stream("Alicia", "Bernardo", "Cristóbal", "Débora", "Ernesto")
println(people)      // Stream(Alicia, ?)
println(people(2))   // Cristóbal
println(people)      // Stream(Alicia, Bernardo, Cristóbal, ?)
// END PeopleStream

object Primes {

  // BEGIN IntegersPrinted
  val integers = Stream.from(0)
  println(integers)  // Stream(0, ?)
  // END IntegersPrinted

  // BEGIN PrimeStream
  def isPrime(n: Int) = n > 1 && (2 to n/2).forall(n % _ != 0)
  val primes = integers.filter(isPrime)
  // END PrimeStream

  // BEGIN PrintedBeforeGettingPrime
  println(integers)  // Stream(0, 1, 2, ?)
  println(primes)    // Stream(2, ?)
  // END PrintedBeforeGettingPrime

  // BEGIN PrintedAfterGettingPrime
  println(primes(4)) // 11
  println(integers)  // Stream(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, ?)
  println(primes)    // Stream(2, 3, 5, 7, 11, ?)
  // END PrintedAfterGettingPrime

  // BEGIN PrimePeople
  val people = List("Alicia", "Bernardo", "Cristóbal", "Débora", "Ernesto")
  println(people zip primes)
  // List((Alicia,2), (Bernardo,3), (Cristóbal,5), (Débora,7), (Ernesto,11))
  // END PrimePeople
}
Primes

// BEGIN OnesAndTwos
val onesAndTwos = Stream.continually(List(1, 2)).flatten
// END OnesAndTwos

// BEGIN AlternativeOnesAndTwos
val onesAndTwos2 = Stream.from(0).map(_ % 2 + 1)
val onesAndTwos3 = Stream.iterate(1) { case 1 ⇒ 2 case 2 ⇒ 1 }
val onesAndTwos4 = Stream.iterate(1)(3 - _)
val onesAndTwos5: Stream[Int] = 1 #:: 2 #:: onesAndTwos5
// END AlternativeOnesAndTwos

val manyOnesAndTwos = onesAndTwos.take(30)
assert(onesAndTwos2 startsWith manyOnesAndTwos)
assert(onesAndTwos3 startsWith manyOnesAndTwos)
assert(onesAndTwos4 startsWith manyOnesAndTwos)
assert(onesAndTwos5 startsWith manyOnesAndTwos)

// BEGIN SeqFill
val fourHorsemen = Seq.fill(4)("horseman")
assert(fourHorsemen == Seq("horseman", "horseman", "horseman", "horseman"))
// END SeqFill

// BEGIN KolakoskiRest
def rest(runLengths: Stream[Int]): Stream[Int] = {
  runLengths zip onesAndTwos flatMap { case (k, n) ⇒ Seq.fill(k)(n) }
}
// END KolakoskiRest

// BEGIN RecursiveKolakoski
val kolakoski: Stream[Int] = 1 #:: 2 #:: 2 #:: rest(kolakoski.drop(2))
// END RecursiveKolakoski

val expectedKolakoski = List(1, 2, 2, 1, 1, 2, 1, 2, 2, 1, 2, 2, 1, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1)

expectedKolakoski.take(23)       .mkString("")
kolakoski        .take(23).toList.mkString("")

assert(kolakoski startsWith expectedKolakoski)

val n = 10000000
val ones = kolakoski.take(n).filter(_ == 1).length
val twos = n - ones
println(s"$ones/$n are ones, $twos/$n are twos.")
