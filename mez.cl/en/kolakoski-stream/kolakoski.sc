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
}
Primes

val onesAndTwos = Stream.continually(List(1, 2)).flatten

def rest(kolakoski: Stream[Int]): Stream[Int] = {
  kolakoski.drop(2) zip onesAndTwos flatMap { case (k, n) ⇒ Seq.fill(k)(n) }
}

// BEGIN RecursiveKolakoski
val kolakoski: Stream[Int] = 1 #:: 2 #:: 2 #:: rest(kolakoski)
// END RecursiveKolakoski

val expectedKolakoski = List(1, 2, 2, 1, 1, 2, 1, 2, 2, 1, 2, 2, 1, 1, 2, 1, 1, 2, 2, 1, 2, 1, 1)

expectedKolakoski.take(23)       .mkString("")
kolakoski        .take(23).toList.mkString("")

assert(kolakoski startsWith expectedKolakoski)

val n = 10000000
val ones = kolakoski.take(n).filter(_ == 1).length
val twos = n - ones
println(s"$ones/$n are ones, $twos/$n are twos.")
