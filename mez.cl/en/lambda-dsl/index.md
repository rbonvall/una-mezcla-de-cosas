---
title: A DSL for λ-terms in Scala
date: 2017-12-xx
author: Roberto Bonvallet
panflute-filters: ["../../../bin/include.py"]
---

Ah, the lambda calculus—what a thing of beauty!
It gives us the opportunity to write expressions like

> *(λm.λn.m(λi.λs.λz.is(sz)) n) (λs.λz.sz) (λs.λz.s(sz))*

that some heresiarchs would stubbornly insist
in obfuscating with the infidel’s notation as 1 + 2.

Unfortunately, things start to get unwieldy
when you try to describe that thing in idiomatic Scala:

~~~~ {.include .scala}
file: lambda.sc
from: BEGIN TermExample
to:   END TermExample
~~~~

What a mess!
It took me a lot to get that right for sure.

The good thing is that Scala’s syntax
is flexible enough to let us design
a nice-looking lambda calculus DSL
embedded right into *la langue de Odersky*.
Let’s see how.

Implicit variables
------------------
A bare symbol is good enough to visually identify a variable.

We can create an implicit conversion in `Term`’s companion object
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

Infix applications
------------------
Scala doesn’t allow us to overload juxtaposition,
but the next best thing we can do
is to introduce an operator for function application.
I’ll borrow Haskell’s dollar sign operator
and implement it in the `Term` trait:

~~~~ {.include .scala}
file: lambda.sc
from: BEGIN TermWithInfixApplication
to:   END TermWithInfixApplication
~~~~

We can verify that `$` associates from the left,
as is the convention in the lambda calculus:

~~~~ {.include .scala}
file: lambda.sc
from: BEGIN LeftAssociativeApplication
to:   END LeftAssociativeApplication
~~~~

It just takes one dollar to write the omega combinator:

~~~~ {.include .scala}
file: lambda.sc
from: BEGIN Omega
to:   END Omega
~~~~

Lambdas, sweet lambdas
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

Some classic combinators can finally be written in exquisite fashion:

~~~~ {.include .scala}
file: lambda.sc
from: BEGIN ClassicCombinators
to:   END ClassicCombinators
~~~~

Putting one and two together
----------------------------

Let’s take our original expression:

> *(λm.λn.m(λi.λs.λz.is(sz)) n) (λs.λz.sz) (λs.λz.s(sz))*

and see how it would translate to Scala now:

~~~~ {.include .scala}
file: lambda.sc
from: BEGIN Lambda 1+2
to:   END Lambda 1+2
~~~~

Not bad! We can even give names to intermediate terms
to make the meaning of the expression even clearer
(not that we couldn’t have done this from the beginning, though):

~~~~ {.include .scala}
file: lambda.sc
from: BEGIN Nicer 1+2
to:   END Nicer 1+2
~~~~

This code would pass the strictest of code reviews with flying colors.

Doesn’t Scala already have lambdas?
-----------------------------------
Yes, and by using them we could have our expressions evaluated for free by the language.

But what I really want to do
is to implement different evaluation strategies,
and for that I need to manipulate the lambda terms myself.
Hopefully that’ll give me material for future articles,
in which I’ll show how to do impressive stuff
like evaluating 1 + 2 to 3.

At least now I can write some nice-looking unit tests.

_If you liked this article, you should follow me.
I mean literally: wait for me to leave my home and walk behind me until I catch you._
