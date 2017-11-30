---
title: Discovering typeclasses in Scala
author: Roberto Bonvallet
date: 2017-11-30
panflute-filters: ["../../../bin/include.py"]
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

~~~~ {.include .scala}
file: typeclasses.sc
from: BEGIN MaxOfInts
to:   END MaxOfInts
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
(so it doesn’t just work on *any* type,
with the compiler rejecting uses that don’t make sense).

Our goal will be to find the maximum of the following list of persons,
whatever that may mean:

~~~~ {.include .scala}
file: typeclasses.sc
from: BEGIN DatesAndPersons
to:   END DatesAndPersons
~~~~

The OO 101 way
==============

We can define an abstract type
for things that can be ordered
and make `Person` implement it,
let’s say by comparing people by their names.

~~~~ {.include .scala}
file: typeclasses.sc
from: BEGIN Ordered
to:   END Ordered
~~~~

Here I decided to name the comparison method ``?<?``
to make it clear that it‘s not the same operator that we used for integers,
but ``<`` would have worked equally fine.

Now ``maximum`` can accept anything that “is-an” ordered,
and we indicate this by adding an upper bound to the type parameter `T`:

~~~~ {.include .scala}
file: typeclasses.sc
from: BEGIN MaxOfOrdered
to:   END MaxOfOrdered
~~~~

And the maximum person is...

~~~~
scala> maximum(ps).name
res2: String = Zoila
~~~~

This is a reasonable approach in many situations,
but it suffers from a couple of limitations.

First, we have forced persons to be ordered by name.
A basketball recruiter probably would prefer to find
the maximum person in terms of height,
while a birthday party planner would rather
find who has the maximum birthday date of the year
(I’m just guessing what party planners care about, I’m sure I’m correct).

Second, our generic function relies on other developers
extending our base type,
and none of the code out there in the wild does.
Some coder that wants to use our generic function
on a type defined by someone else in an external library
will have to resort to write some kind of adapter

We cannot even use this generic function directly
on our original list of integers!


A functional solution
=====================

Let’s put on our lambda-shaped hat
and make the comparison function a parameter of `maximum`:

~~~~ {.include .scala}
file: typeclasses.sc
from: BEGIN MaxWithFunction
to:   END MaxWithFunction
~~~~

Now we can find out who’s the maximum by month of birth,
a very typical sorting criterion in my hometown:

~~~~
scala> maximum(ps){ (p1, p2) ⇒ p1.birthDate.month < p2.birthDate.month }.name
res3: String = Maria
~~~~

We gained in flexibility
but this is not necessarily convenient if we need more behaviour:
nobody wants to pass ten functions as arguments.
For example,
a generic function that operates on numeric types
may need to accept functions to tell it
how to add, substract, multiply, divide, etc.

So, let’s put the comparison function into an object
so we have just one thing to pass around,
with a couple of other inherited comparison operations
so it doesn’t feel so lonely:

~~~~ {.include .scala}
file: typeclasses.sc
from: BEGIN OrderingObject
to:   END OrderingObject
~~~~

Note that I renamed the base trait,
since `Person` no longer “is-an” `Ordered` type
but it rather “has-an” `Ordering`.

The maximum function now becomes:

~~~~ {.include .scala}
file: typeclasses.sc
from: BEGIN MaxWithOrdering
to:   END MaxWithOrdering
~~~~

and we can find out who’s the tall guy by passing the right bag of functions:

~~~~
scala> maximum(ps)(PersonHeightOrdering).name
res4: String = Aaron
~~~~


We almost discovered typeclasses!
=================================

*Typeclasses* are an implementation of polymorphism
in which generic functions know what to do
if there is evidence that the data satisfies some interface.

There is something like that in the latest revision of `maximum`:
`Ordering` is the typeclass
and the object we pass around is the evidence.
We say that the object is an *instance* (or *model*) of the typeclass.

But that’s not the real thing,
because with actual typeclasses you don’t need to pass the evidence along.
You just use the operations and it Just Works™.

In Haskell, typeclasses are a language feature.
In Scala, on the other hand,
they are a pattern (also called “the concept pattern”)
that is implemented using implicits.

We can begin by making the ordering parameter implicit:

~~~~ {.include .scala}
file: typeclasses.sc
from: BEGIN MaxWithImplicitOrdering
to:   END MaxWithImplicitOrdering
~~~~

and then creating an instance:

~~~~ {.include .scala}
file: typeclasses.sc
from: BEGIN PersonNameOrdering
to:   END PersonNameOrdering
~~~~

which we’ll make the implicit ordering in this context:

TODO: ADD SNIPPET

so we can just call `maximum` with no extra arguments:

~~~~
scala> maximum(ps).name
res5: String = Zoila
~~~~

But we have just swept the explicitness under the rug!
The function still knows that it’s getting an `Ordering` instance.
Scala provides a shortcut for not having to list the implicit parameter
(making it doubly implicit), which is the *context bound* syntax:

~~~~ {.include .scala}
file: typeclasses.sc
from: BEGIN MaxWithContextBound
to:   END MaxWithContextBound
~~~~

The type parameter `[T : Ordering]` states that
there needs to be an implicit instance of `Ordering[T]`
for the funcion to be used,
but you may have already noticed that there is still a bit of ugliness:
in order to call the `lessThan` method
the instance needs to be summoned using `implicitly[Ordering[T]]`.


The missing piece: a final touch of implicitness
================================================

To remove the remaining reference to the typeclass instance,
what is usually done is creating an implicit class
that wraps its methods:

~~~~ {.include .scala}
file: typeclasses.sc
from: BEGIN OrderingOps
to:   END OrderingOps
~~~~

And this is the final `maximum` function:

~~~~ {.include .scala}
file: typeclasses.sc
from: BEGIN MaxUsingOrderingOps
to:   END MaxUsingOrderingOps
~~~~

The thing to be delighted with is that
*it is basically the same as the maximum for ints*!
The only differences are the context bound `T : Ordering`
(for type safety)
and the use of the `?<?`
(my own whim, as `<` woulda worked).


Why bother
==========

So we finally know *what* are typeclasses in Scala,
but not *why* they exist.
Their importance lies in the ability to *retroactively* extend types
to satisfy an interface.

