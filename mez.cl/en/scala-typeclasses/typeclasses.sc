object first {

  // BEGIN ListOfIntegers
  val ns = List(33, 22, 66, 55, 11, 44)
  // END ListOfIntegers

  // BEGIN MaxOfInts
  def maximum(ns: List[Int]): Int =
    ns match {
      case      Nil ⇒ ???
      case n :: Nil ⇒ n
      case n1 :: n2 :: rest ⇒
        if (n1 < n2) maximum(n2 :: rest)
        else         maximum(n1 :: rest)
    }
  // END MaxOfInts

  assert(maximum(ns) == 66)
  assert(maximum(List(999))    == 999)
  assert(maximum(List(11, 22)) == 22)
  assert(maximum(List(22, 11)) == 22)

  // BEGIN DateAndPersonTypes
  case class Date(year: Int, month: Int, day: Int)
  case class Person(name: String, height: Int, birthDate: Date)
  // END DateAndPersonTypes

  // BEGIN ListOfPersons
  val ps: List[Person] = List(
    Person("Aaron", 180, Date(1985, 4, 15)),
    Person("Maria", 155, Date(1998, 3, 31)),
    Person("Zoila", 175, Date(1998, 9,  1))
  )
  // END ListOfPersons

  println("1 :)")
}

object second {

  // BEGIN Ordered
  trait Ordered[T] {
    def ?<?(that: T): Boolean
  }

  case class Person(name: String, height: Int, birthDate: Date) extends Ordered[Person] {
    def ?<?(that: Person) = this.name < that.name
  }
  // END Ordered

  case class Date(year: Int, month: Int, day: Int) extends Ordered[Date] {
    def ?<?(that: Date) =
      if      (this.year  != that.year)  this.year  < that.year
      else if (this.month != that.month) this.month < that.month
      else                               this.day   < that.day
  }

  val ps: List[Person] = List(
    Person("Aaron", 180, Date(1985, 4, 15)),
    Person("Maria", 155, Date(1998, 3, 31)),
    Person("Zoila", 175, Date(1998, 9,  1))
  )

  // BEGIN MaxOfOrdered
  def maximum[T <: Ordered[T]](ts: List[T]): T =
    ts match {
      case      Nil ⇒ ???
      case t :: Nil ⇒ t
      case t1 :: t2 :: rest ⇒
        if (t1 ?<? t2) maximum(t2 :: rest)
        else           maximum(t1 :: rest)
    }
  // END MaxOfOrdered

  assert(maximum(ps).name == "Zoila")
  assert(maximum(ps.map(_.birthDate)) == Date(1998, 9, 1))
  println("2 :)")

}

object third {
  import first.ps

  // BEGIN MaxWithFunction
  def maximum[T](ts: List[T])(lessThan: (T, T) ⇒ Boolean): T =
    ts match {
      case      Nil ⇒ ???
      case t :: Nil ⇒ t
      case t1 :: t2 :: rest ⇒
        if (lessThan(t1, t2)) maximum(t2 :: rest)(lessThan)
        else                  maximum(t1 :: rest)(lessThan)
    }
  // END MaxWithFunction

  assert(maximum(ps){ (p1, p2) ⇒ p1.name < p2.name }.name == "Zoila")
  println("3 :)")
}

object fourth {
  import first.{Person, ps}

  // BEGIN Ordering
  trait Ordering[T] {
    def lessThan   (t1: T, t2: T): Boolean
    def equal      (t1: T, t2: T): Boolean = !lessThan(t1, t2) && !greaterThan(t1, t2)
    def greaterThan(t1: T, t2: T): Boolean = lessThan(t2, t1)
  }

  object PersonHeightOrdering extends Ordering[Person] {
    def lessThan(t1: Person, t2: Person) = t1.height < t2.height
  }
  object PersonNameOrdering extends Ordering[Person] {
    def lessThan(t1: Person, t2: Person) = t1.name < t2.name
  }
  // END Ordering

  // BEGIN MaxWithOrdering
  def maximum[T](ts: List[T])(ordering: Ordering[T]): T =
    ts match {
      case      Nil ⇒ ???
      case t :: Nil ⇒ t
      case t1 :: t2 :: rest ⇒
        if (ordering.lessThan(t1, t2)) maximum(t2 :: rest)(ordering)
        else                           maximum(t1 :: rest)(ordering)
    }
  // END MaxWithOrdering

  assert(maximum(ps)(PersonHeightOrdering).name == "Aaron")
  println("4 :)")

}

object fifth {
  import first.ps
  import fourth.{Ordering, PersonNameOrdering}

  // BEGIN MaxWithImplicitOrdering
  def maximum[T](ts: List[T])(implicit ordering: Ordering[T]): T =
    ts match {
      case      Nil ⇒ ???
      case t :: Nil ⇒ t
      case t1 :: t2 :: rest ⇒
        if (ordering.lessThan(t1, t2)) maximum(t2 :: rest)
        else                           maximum(t1 :: rest)
    }
  // END MaxWithImplicitOrdering

  // BEGIN SetImplicitOrdering
  implicit val ord = PersonNameOrdering
  // END SetImplicitOrdering

  assert(maximum(ps).name == "Zoila")
  println("5 :)")

}

object sixth {
  import first.{Person, ps}
  import fourth.Ordering

  // BEGIN OrderingInstanceForPerson
  object PersonBirthDateDayOfMonthOrdering extends Ordering[Person] {
    def lessThan(t1: Person, t2: Person) = t1.birthDate.day < t2.birthDate.day
  }
  // END OrderingInstanceForPerson

  // BEGIN MaxWithContextBound
  def maximum[T : Ordering](ts: List[T]): T =
    ts match {
      case      Nil ⇒ ???
      case t :: Nil ⇒ t
      case t1 :: t2 :: rest ⇒
        if (implicitly[Ordering[T]].lessThan(t1, t2)) maximum(t2 :: rest)
        else                                          maximum(t1 :: rest)
    }
  // END MaxWithContextBound

  implicit val ord = PersonBirthDateDayOfMonthOrdering
  assert(maximum(ps).name == "Maria")
  println("6 :)")

}

object seventh {
  import first.Date
  import fourth.Ordering

  // BEGIN OrderingInstanceInCompanionObject
  case class Person(name: String, height: Int, birthDate: Date)
  object Person {
    implicit object PersonNameOrdering extends Ordering[Person] {
      def lessThan(p1: Person, p2: Person) = p1.name < p2.name
    }
  }
  // END OrderingInstanceInCompanionObject

  // BEGIN OrderingOps
  implicit class OrderingOps[T : Ordering](t1: T) {
    val ordering = implicitly[Ordering[T]]
    def ?<?(t2: T) = ordering.lessThan   (t1, t2)
    def ?=?(t2: T) = ordering.equal      (t1, t2)
    def ?>?(t2: T) = ordering.greaterThan(t1, t2)
  }
  // END OrderingOps

  // BEGIN MaxWithOrderingOps
  def maximum[T : Ordering](ts: List[T]): T =
    ts match {
      case      Nil ⇒ ???
      case t :: Nil ⇒ t
      case t1 :: t2 :: rest ⇒
        if (t1 ?<? t2) maximum(t2 :: rest)
        else           maximum(t1 :: rest)
    }
  // END MaxWithOrderingOps

  val ps: List[Person] = List(
    Person("Aaron", 180, Date(1985, 4, 15)),
    Person("Maria", 155, Date(1998, 3, 31)),
    Person("Zoila", 175, Date(1998, 9,  1))
  )

  assert(maximum(ps).name == "Zoila")

  // BEGIN UsingMaximumWithIntegers
  object AscendingIntOrdering  extends Ordering[Int] { def lessThan(n1: Int, n2: Int) = n1 < n2 }
  object DescendingIntOrdering extends Ordering[Int] { def lessThan(n1: Int, n2: Int) = n1 > n2 }
  implicit val intOrd = AscendingIntOrdering

  val ns = List(33, 22, 66, 55, 11, 44)
  assert(maximum(ns) == 66)
  assert(maximum(ns)(DescendingIntOrdering) == 11)
  // END UsingMaximumWithIntegers

  println("7 :)")
}

object all {
  first
  second
  third
  fourth
  fifth
  sixth
  seventh
}
all