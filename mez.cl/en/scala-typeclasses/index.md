---
title: The minimum about Scala typeclasses (using the maximum)
author: Roberto Bonvallet
date: 2017-09-02
abstract: |
    This is a loose transcript of the talk about typeclasses
    I gave in a recent [Santiago Scala Meetup](https://www.meetup.com/es/Santiago-Scala-Meetup/events/240507350/).
    The talk was in Spanish but I decided to write this article in English,
    so be aware that many brilliant puns got lost in translation.
---

This is a list of integers:

~~~~ {.scala}
val ns = List(33, 22, 66, 55, 11, 44)
~~~~

And this is a function for computing
the maximum value of such a list:

~~~~ {.scala}
def maximum(ns: List[Int]): Int =
  ns match {
    case      Nil ⇒ ???
    case n :: Nil ⇒ n
    case n1 :: n2 :: rest ⇒
      if (n1 < n2) maximum(n2 :: rest)
      else         maximum(n1 :: rest)
  }
~~~~

Does it work?

~~~~
scala> maximum(ns)
res1: Int = 66
~~~~

Yes, it does!

Although this function only accepts lists of integers,
the algorithm is the same for any type
that has a notion of a maximum.
The only thing that’s `Int`-specific
is the use of the ``<`` operator.

So let’s make this function generic.
We want it to be polymorphic (so it works on different types)
and typesafe
(so it doesn’t work on *any* type,
with the compiler rejecting uses that don’t make sense).

Our goal will be to find the maximum of a list of persons,
whatever that may mean:

~~~~ {.scala}
case class Date(year: Int, month: Int, day: Int)
case class Person(name: String, height: Int, birthDate: Date)

val ps: List[Person] = List(
  Person("Aaron", 180, Date(1985, 4, 15)),
  Person("Maria", 155, Date(1998, 3, 31)),
  Person("Zoila", 175, Date(1998, 9,  1))
)
~~~~

One approach is to define an abstract type
for things that can be ordered
and make `Person` implement it,
let’s say by comparing people by their names.
This is OO 101:

~~~~ {.scala}
trait Ordered[T] {
  def ?<?(that: T): Boolean
}

case class Person(name: String, height: Int, birthDate: Date) extends Ordered[Person] {
  def ?<?(that: Person) = this.name < that.name
}
~~~~

Here I decided to call the comparison method ``?<?``
to make it clear that it‘s not the same operator that we used for integers,
but ``<`` would have worked equally fine.

Now ``maximum`` can accept anything that “is-an” ordered:

~~~~ {.scala}
def maximum[T](ts: List[Ordered[T]]): T =
  ts match {
    case      Nil ⇒ ???
    case t :: Nil ⇒ t
    case t1 :: t2 :: rest ⇒
      if (t1 ?<? t2) maximum(t2 :: rest)
      else           maximum(t1 :: rest)
  }
~~~~

And we can find the maximum person:

~~~~
scala> maximum(ps)
res1: Person = Person(Zoila,175,Date(1998,9,1))
~~~~

This is a reasonable approach in many situations,
but it has some limitations:

* We have forced persons to be ordered by name;
  a basketball recruiter probably would prefer to find
  the maximum person in terms of height,
  while a birthday party planner would rather
  find who has the maximum birthday date in the year
  (I’m just guessing what party planners care about,
  I’m sure I’m right).

* Our generic function relies on other developers
  extending our base type,
  and none of the code out there in the wild does.
  Some coder that wants to use our generic function
  on a type defined by someone else in some other library
  will have to resort to have to write
  some kind of adapter.

For this particular use case,
we also could have put on our lambda-shaped hat,
requiring the comparison function
to be passed as a parameter:

~~~~ {.scala}
def maximum[T](ts: List[T], lessThan: (T, T) ⇒ Boolean): T =
  ts match {
    case      Nil ⇒ ???
    case t :: Nil ⇒ t
    case t1 :: t2 :: rest ⇒
      if (lessThan(t1, t2)) maximum(t2 :: rest)
      else                  maximum(t1 :: rest)
  }
~~~~

which is nice and stuff but gets tedious
if we need more behaviour:
nobody wants to pass ten functions as arguments.

For example,
a generic function that operates on numeric types
would need to accept functions to tell it
how to add, substract, multiply, divide, etc.
Or a function operating on values of more than one type
would force you to pass function arguments
for each of them.

So, let’s put our lonely method into an object,
so we have just one thing to pass around
in case we want to add more operations:

~~~~ {.scala}
object OrderedPerson extends Ordered[Person] {
  // for you, my basketball recruiter friend
  def lessThan(p1: Person, p2: Person) = p1.height < p2.height

  // Another operation we could have here
  def equal(p1: Person, p2: Person) =
    !lessThan(p1, p2) && !lessThan(p2, p1)
}

def maximum[T](ts: List[T], ops: Ordered[T]): T =
  ts match {
    case      Nil ⇒ ???
    case t :: Nil ⇒ t
    case t1 :: t2 :: rest ⇒
      if (ops.lessThan(t1, t2)) maximum(t2 :: rest)
      else                      maximum(t1 :: rest)
  }
~~~~

Are we there yet?
-----------------

At this point we are close to have invented typeclasses,
so let’s switch the terminology appropiately.

First of all, `Person` no longer *is* an ordered type,
but rather it *has* an ordering,
represented by our “bag of functions” `OrderedPerson`.

`OrderedPerson` can be seen as a concrete evidence
that 

Enter typeclasses
-----------------

Typeclasses are a technique for creating generic code
that works on any type for which there is evidence
that it satisfies some interface,
in a typesafe fashion.

In Haskell, typeclasses are a language feature.
In Scala, on the other hand,
they are a pattern (also called “the concept pattern”)
that is implemented using implicits.

Let’s create a typeclass for types that can be ordered,
and modify the ``maximum`` function to use it:

~~~~ {.scala}
trait Ordering[T] {
  def lessThan(t1: T, t2: T): Boolean
}
def maximum[T](ts: List[T], evidence: Ordering[T]): T =
  ts match {
    case      Nil ⇒ ???
    case t :: Nil ⇒ t
    case t1 :: t2 :: rest ⇒
      if (evidence.lessThan(t1, t2)) maximum(t2 :: rest)
      else                           maximum(t1 :: rest)
  }
~~~~

Notice that I called it ``Ordering`` rather that ``Ordered`` as above,
since we will declare that a type “has-an ordering,”
not that it “is-an ordered.”
And this is how we could do it:

~~~~ {.scala}
object PersonHasOrdering extends Ordering[Person] {
  def lessThan(p1: Person, p2: Person) = p1.height < p2.height
}
~~~~

And now:

~~~~
scala> maximum(people, PersonHasOrdering)
res1: Person = Person(...)
~~~~
