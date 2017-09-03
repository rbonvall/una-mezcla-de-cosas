The minimum about Scala typeclasses (using the maximum)
=======================================================

*This is a loose transcript of the talk about typeclasses
I gave in a recent `Santiago Scala Meetup`_.
The talk was in Spanish but I decided to write this article in English,
so be aware that many brilliant puns got lost in translation.*

.. _Santiago Scala Meetup: https://www.meetup.com/es/Santiago-Scala-Meetup/events/240507350/


This is a list of integers::

    val ns = List(33, 22, 66, 55, 11, 44)

And this is a function for computing
the maximum value of such a list::

    def maximum(ns: List[Int]): Int =
      ns match {
        case      Nil => ???
        case n :: Nil => n
        case n1 :: n2 :: rest =>
          if (n1 < n2) maximum(n2 :: rest)
          else         maximum(n1 :: rest)
      }

Does it work?

::

    scala> maximum(ns)
    res1: Int = 66

Yes, it does!

Although this function only accepts lists of integers,
the algorithm makes sense for any type that has some ordering.
The only thing that’s ``Int``-specific
is the ``<`` operator.

Let’s make this function generic.
We want it to be polymorphic (so it works on different types)
but also typesafe
(so the compiler rejects uses of the function when the types don’t make sense).

Our goal is to find the maximum of a list of persons,
whatever that may mean::

    case class Date(year: Int, month: Int, day: Int)
    case class Person(name: String, height: Int, birthDate: Date)

    val ps: List[Person] = List(
      Person("Aaron", 180, Date(1985, 4, 15)),
      Person("Maria", 155, Date(1998, 3, 31)),
      Person("Zoila", 175, Date(1998, 9,  1))
    )

One approach is to define an abstract type for things that can be ordered
and make `Person` implement it, let’s say by comparing names lexicographically.
This is OO 101::

    trait Ordered[T] {
      def ?<?(that: T): Boolean
    }

    case class Person(name: String, height: Int, birthDate: Date) extends Ordered[Person] {
      def ?<?(that: Person) = this.name < that.name
    }

Here I decided to call the comparison method ``?<?``
to make it clear that it‘s not the same operator that we used for integers,
but ``<`` would have worked equally fine.

Now ``maximum`` can accept anything that “is-an” ordered::

    def maximum[T](ts: List[Ordered[T]]): T =
      ts match {
        case      Nil => ???
        case t :: Nil => t
        case t1 :: t2 :: rest =>
          if (t1 ?<? t2) maximum(t2 :: rest)
          else           maximum(t1 :: rest)
      }

And we can find the maximum person::

    scala> maximum(ps)
    res1: Person = Person(Zoila,175,Date(1998,9,1))

This is a reasonable approach in many situations,
but there are two potential drawbacks:

* We have forced persons to be ordered by name;
  a basketball recruiter probably would prefer to find
  the maximum person in terms of height.

* Our generic function relies on other library writers
  extending our base type,
  and none of the code out there in the wild does.
  Also, it’s not reasonable to expect other developers
  to implement every possible abstract interface
  that we come up with,
  even if it’s for their own benefit.

For this particular example,
we could also put on our lambda-shaped hat
and require the comparison function
to be passed as a parameter::

    def maximum[T](ts: List[T], lessThan: (T, T) => Boolean): T =
      ts match {
        case      Nil => ???
        case t :: Nil => t
        case t1 :: t2 :: rest =>
          if (lessThan(t1, t2)) maximum(t2 :: rest)
          else                  maximum(t1 :: rest)
      }

which is nice and stuff but gets tedious
if we need more behaviour.
Without going any further,
we 
nobody wants to pass ten functions as arguments.
For example, a generic function that operates on numeric types
would need to accept functions to tell it
how to add, substract, multiply, divide, etc.

So, let’s put our lonely method into an object,
so we have only one thing to pass around
in case we want to add more operations::


    object OrderedPerson extends Ordered[Person] {
      // for you, my basketball recruiter friend
      def lessThan(p1: Person, p2: Person) = p1.height < p2.height
    }

    def maximum[T](ts: List[T], lessThan: (T, T) => Boolean): T =
      ts match {
        case      Nil => ???
        case t :: Nil => t
        case t1 :: t2 :: rest =>
          if (lessThan(t1, t2)) maximum(t2 :: rest)
          else                  maximum(t1 :: rest)
      }

Enter typeclasses
-----------------
Typeclasses are a technique for creating generic code
so it works on any type for which there is evidence
that they satisfy some interface,
in a typesafe fashion.

In Haskell, typeclasses are a language feature.
In Scala, on the other hand,
they are a pattern (also called “the concept pattern”)
that is implemented using implicits.

Let’s create a typeclass for types that can be ordered,
and modify the ``maximum`` function to use it::

    trait Ordering[T] {
      def lessThan(t1: T, t2: T): Boolean
    }
    def maximum[T](ts: List[T], ordering: Ordering): T =
      ts match {
        case      Nil => ???
        case t :: Nil => t
        case t1 :: t2 :: rest =>
          if (lessThan(t1, t2)) maximum(t2 :: rest)
          else                  maximum(t1 :: rest)
      }

Notice that I called it ``Ordering`` rather that ``Ordered`` as above,
since we will declare that a type “has-an ordering,”
not that is “is-an ordered.”
And this is how we could do it::

    object PersonHasOrdering extends Ordering[Person] {
      def lessThan(p1: Person, p2: Person) = p1.height < p2.height
    }







