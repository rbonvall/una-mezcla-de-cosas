// BEGIN FunctorTrait
trait Functor[F[_]] {
  def fmap[A, B] (fa: F[A]) (f: A ⇒ B): F[B]
}
// END FunctorTrait

// BEGIN ListAndOptionFunctors
implicit object ListFunctor extends Functor[List] {
  def fmap[A, B] (la: List[A]) (f: A ⇒ B): List[B] =
    la match {
      case Nil     ⇒ Nil
      case a :: as ⇒ f(a) :: fmap(as)(f)
    }
}

implicit object OptionFunctor extends Functor[Option] {
  def fmap[A, B] (oa: Option[A]) (f: A ⇒ B): Option[B] =
    oa match {
      case None    ⇒ None
      case Some(a) ⇒ Some(f(a))
    }
}
// END ListAndOptionFunctors

val ls = List("hello", "beautiful", "world")
val li = ListFunctor.fmap(ls) { s ⇒ s.length }
assert(li == List(5, 9, 5))

val oi = Option(5)
val os = OptionFunctor.fmap(oi) { n ⇒ "x" * n }
assert(os == Some("xxxxx"))

def MAP[F[_]: Functor, A, B] (fa: F[A]) (f: A ⇒ B): F[B] =
  implicitly[Functor[F]].fmap(fa)(f)

val li2 = MAP (List("hello", "beautiful", "world")) { s ⇒ s.length }
val os2 = MAP (Option(5)) { n ⇒ "x" * n }
assert(li == li2)
assert(os == os2)

// Doesn't compile: "could not find implicit value
// for evidence parameter of type Functor[Vector]"
//MAP (Vector(1, 2, 3)) { n ⇒ n * n }

trait ContravariantFunctor[F[_]] {
  def contramap[A, B] (fa: F[A]) (f: B ⇒ A): F[B]
}

case class Date(year: Int, month: Int, day: Int)
case class Person(name: String, bday: Date)

case class Equivalence[T](eq: (T, T) ⇒ Boolean)

implicit object EqContFunctor extends ContravariantFunctor[Equivalence] {
  def contramap[A, B] (ea: Equivalence[A]) (f: B ⇒ A): Equivalence[B] =
    Equivalence { (b1, b2) ⇒ ea.eq(f(b1), f(b2)) }
}

def CONTRAMAP[F[_]: ContravariantFunctor, A, B] (fa: F[A]) (f: B ⇒ A): F[B] =
  implicitly[ContravariantFunctor[F]].contramap(fa)(f)

val byDayOfTheYear: Equivalence[Date] = Equivalence { (d1, d2) ⇒
  d1.day == d2.day && d1.month == d2.month
}
assert( byDayOfTheYear.eq(Date(1990, 5, 13), Date(2011, 5, 13)))
assert(!byDayOfTheYear.eq(Date(1879, 9, 28), Date(1879, 9, 29)))

val byBirthday: Equivalence[Person] = CONTRAMAP(byDayOfTheYear)(_.bday)
val john = Person("John", Date(1955,  5, 13))
val tina = Person("Tina", Date(1966, 10, 28))
val mike = Person("Mike", Date(1988,  5, 13))
assert( byBirthday.eq(john, mike))
assert(!byBirthday.eq(mike, tina))

val monthNames = Seq(
  "Jan", "Feb", "Mar", "Apr", "May", "Jun",
  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)
def niceDate(date: Date): String =
  s"${date.year}/${monthNames(date.month - 1)}/${date.day}"
assert(niceDate(Date(2018, 11, 30)) == "2018/Nov/30")

/*         MAP(niceDate)(p => p.bday)
 *             - - - - - - >
 *  Person => Date         Person => String
 *       ^                      ^
 *       |                      |
 *       |                      |
 *     Date ----------------> String
*               niceDate
 */

// Shadow implicit conversions from Predef
val ArrowAssoc = 0
val Ensuring = 0
val StringFormat = 0
val any2stringadd = 0

type PersonFunc[Y] = Function[Person, Y]

implicit object PersonFuncFunctor extends Functor[PersonFunc] {
  def fmap[A, B] (fp: Person ⇒ A) (f: A ⇒ B): Person ⇒ B =
    person ⇒ f(fp(person))
}

val niceBirthday: PersonFunc[String] = MAP((p: Person) ⇒ p.bday)(niceDate)

assert(niceBirthday(tina) == "1966/Oct/28")

type FuncToPerson[X] = Function[X, Person]
