---
title: A DSL in Scala for λ-terms
author: Roberto Bonvallet
date: 2017-11-11
panflute-filters: ["../../../bin/include.py"]
---

Ah, the lambda calculus---what a thing of beauty!
It gives us the opportunity to write expressions like

> *(λm.λn.m(λi.λs.λz.is(sz)) n) (λs.λz.sz) (λs.λz.s(sz))*

that some heresiarchs would stubbornly insist
to obfuscate as 1 + 2.

Unfortunately, things start to get unwieldy
when you try to describe the same thing in idiomatic Scala:

~~~~ {.include .scala}
file: lambda.sc
from: BEGIN TermExample
to:   END TermExample
~~~~

What a mess!
It took me a lot to get that right for sure.

The good thing is that Scala’s syntax
is flexible enough to let us design
a nice-looking lambda calculus API
embedded right into *la langue de Odersky*.
Let’s see how.

Implicit variables
------------------
A bare symbol is good enough to visually identify a variable.

We can create an implicit conversion
in `Term`’s companion object
so the compiler does the wrapping for us
whenever context asks for it:

~~~~ {.include .scala}
file: lambda.sc
from: BEGIN ImplicitVar
to:   END ImplicitVar
~~~~

For example,
the identity function can now be written as:

~~~~ {.include .scala}
file: lambda.sc
from: BEGIN Identity
to:   END Identity
~~~~

Haskellian applications
-----------------------
Scala doesn’t allow us to overload juxtaposition,
but the next best thing we can do
is to introduce an operator for function application.
I’ll borrow Haskell’s dollar sign operator
and implement it in the `Term` trait:

~~~~ {.scala}
sealed trait Term {
  def $(that: Term) = Application(this, that)
}
~~~~

We can verify that `$` associates from the left,
as is the convention in the lambda calculus:

~~~~ {.scala}
val t =  'a $  'b  $ 'c
val l = ('a $  'b) $ 'c
val r =  'a $ ('b  $ 'c)
assert(t == l)
assert(t != r)
~~~~

Lambdaish abstractions
----------------------
And the cherry on top of the cake:
lambdas that look like lambdas!
It’s just a matter of creating
a function called `λ` that has two parameter lists:
one for the parameter and one for the body.

We can also sprinkle some extra syntactic sugar on our cake
by accepting more than one parameter
and taking care of the currying.
Yes, cake with curry!

~~~~ {.include .scala}
file: lambda.sc
from: BEGIN LambdaDefinition
to:   END LambdaDefinition
~~~~

Professor Church would be proud of us:

~~~~ {.scala}
val I = λ('x) { 'x }
val T = λ('x, 'y) { 'x }
val F = λ('x, 'y) { 'y }
val S = λ('n, 's, 'z) { 'n $ 's $ ('s $ 'z) }
~~~~

Putting one and two together
----------------------------

~~~~ {.include .scala}
file: lambda.sc
from: BEGIN Lambda 1+2
to:   END Lambda 1+2
~~~~


