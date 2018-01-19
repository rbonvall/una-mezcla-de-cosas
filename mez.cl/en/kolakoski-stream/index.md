---
title: The Kolakoski stream
date: 2018-01-19
author: Roberto Bonvallet
panflute-filters: ["../../../bin/include.py"]
---

The Kolakoski sequence is composed of ones and twos.
Try to guess what the pattern is,
and click or tap the pink box to reveal a clue
that will give away the answer:

<svg width="480" height="80">
  <g id="seq">
    <text text-anchor="middle" x="30"  y="20">1</text>
    <text text-anchor="middle" x="60"  y="20">2</text>
    <text text-anchor="middle" x="90"  y="20">2</text>
    <text text-anchor="middle" x="120" y="20">1</text>
    <text text-anchor="middle" x="150" y="20">1</text>
    <text text-anchor="middle" x="180" y="20">2</text>
    <text text-anchor="middle" x="210" y="20">1</text>
    <text text-anchor="middle" x="240" y="20">2</text>
    <text text-anchor="middle" x="270" y="20">2</text>
    <text text-anchor="middle" x="300" y="20">1</text>
    <text text-anchor="middle" x="330" y="20">2</text>
    <text text-anchor="middle" x="360" y="20">2</text>
    <text text-anchor="middle" x="390" y="20">1</text>
    <text text-anchor="middle" x="420" y="20">1</text>
    <text text-anchor="middle" x="450" y="20">…</text>
  </g>
  <g id="runs" visibility="hidden">
    <line x1="15"  x2="15"  y1="40" y2="60" stroke="black"></line>
    <line x1="45"  x2="45"  y1="40" y2="60" stroke="black"></line>
    <line x1="105" x2="105" y1="40" y2="60" stroke="black"></line>
    <line x1="165" x2="165" y1="40" y2="60" stroke="black"></line>
    <line x1="195" x2="195" y1="40" y2="60" stroke="black"></line>
    <line x1="225" x2="225" y1="40" y2="60" stroke="black"></line>
    <line x1="285" x2="285" y1="40" y2="60" stroke="black"></line>
    <line x1="315" x2="315" y1="40" y2="60" stroke="black"></line>
    <line x1="375" x2="375" y1="40" y2="60" stroke="black"></line>
    <line x1="435" x2="435" y1="40" y2="60" stroke="black"></line>
    <text text-anchor="middle" x="30"  y="54" font-size="0.7em">1</text>
    <text text-anchor="middle" x="75"  y="54" font-size="0.7em">2</text>
    <text text-anchor="middle" x="135" y="54" font-size="0.7em">2</text>
    <text text-anchor="middle" x="180" y="54" font-size="0.7em">1</text>
    <text text-anchor="middle" x="210" y="54" font-size="0.7em">1</text>
    <text text-anchor="middle" x="255" y="54" font-size="0.7em">2</text>
    <text text-anchor="middle" x="300" y="54" font-size="0.7em">1</text>
    <text text-anchor="middle" x="345" y="54" font-size="0.7em">2</text>
    <text text-anchor="middle" x="405" y="54" font-size="0.7em">2</text>
  </g>
  <g id="uncover">
    <rect x="110" width="170" y="30" height="35" fill="pink"></rect>
    <text text-anchor="middle" x="195" y="54" font-size="0.7em">Show clue</text>
  </g>
</svg>
<script>
  const uncover = document.getElementById("uncover");
  const runs    = document.getElementById("runs");
  uncover.addEventListener('mouseover', () => {
    uncover.style.cursor = 'pointer';
  })
  uncover.addEventListener('click', () => {
    uncover.style.visibility = 'hidden';
    runs   .style.visibility = 'visible';
  });
</script>

This is a very interesting sequence,
with all sorts of cool properties.
Some of them are very profound
and, to some degree, have a level of spiritual relevance
that trascends their mere existence from a set of numbers
by reflecting the way in which the universe looks into itself.

The previous paragraph was just a filler
so you couldn’t peek so easily at the answer, which is:
the Kolakoski sequence is composed of alternating runs of ones and twos
whose lengths are determined by the sequence itself.

It _does_ look into itself. Like the universe.

Take a look again to see if you got it,
and then try to determine what the next items from the sequence are.
I’ll be letting you know how wrong you are:

<style>
  #kolatry {
    width: 85%;
    font-size: 24px;
    padding: 0.5ex;
  }
  #kolastatus {
    font-size: 16px;
  }
</style>
<input id="kolatry" value="12211212212211" style=""></input>
<span id="kolastatus"></span>
<script>
  const kolatry    = document.getElementById('kolatry');
  const kolastatus = document.getElementById('kolastatus');
  const solution = "122112122122112112212112122112112122122112122121121122122112122122112112122121122122112122122112112212112122";
  kolatry.value = solution.substr(0, 14);

  function handleGuess () {
    const value = kolatry.value.replace(/\D/g, "");
    if      (value.length > solution.length) setKolawidgets('Enough!', 'silver')
    else if (value.search(/[3-90]/) !== -1)  setKolawidgets('💩', 'indianred');
    else if (solution.startsWith(value))     setKolawidgets(':)', 'palegreen');
    else                                     setKolawidgets(':(', 'salmon');

  }
  function setKolawidgets(text, color) {
    kolastatus.innerText = text;
    kolatry.style.backgroundColor = color;
  }

  handleGuess();
  kolatry.addEventListener('keyup', handleGuess);
</script>

Now that you are a Kolakoski expert
—a _Kolakoskeer_, if you will—,
I’ll teach you how to describe the sequence as a Scala collection.
The entirety of it!

Streams
-------

In Scala, a stream is a linked list whose tail is lazily evaluated.
In other words, it knows what its first element is
but it doesn’t know the rest until someone asks for them:

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN PeopleStream
to:   END PeopleStream
~~~~

The printed representation of a stream
tells you how much of it is already computed.
Initially the stream knows that it starts with Alicia and nothing more.
After you ask for its third element,
Bernardo and Cristóbal become and remain computed.

There are two good reasons why streams will help us with our goal.

First, streams are good for describing infinite sequences.
As long as we don’t do anything stupid,
such as computing the average of its values
or trying to get its last element,
laziness will take care of
making a stream appear as if it had no end.

Second, streams can be manipulated and transformed
just like any other Scala collection.
Also, the standard library provides some convenient functions
to create streams that we can use as a starting point.

In order to accustom ourselves
to the idea of manipulating an infinite sequence,
let’s examine some examples.

A stream of no odd stuff
------------------------

This is the stream of all the integers:

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN IntegerStream
to:   END IntegerStream
~~~~

And this is the stream of all the even integers:

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN EvenIntegersWithFilter
to:   END EvenIntegersWithFilter
~~~~

This is also a stream of all the even integers:

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN EvenIntegersWithMap
to:   END EvenIntegersWithMap
~~~~

This, again, is the stream of all the even integers,
described as its initial value
and an operation for getting a new one
from the previous one:

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN EvenIntegersWithIterate
to:   END EvenIntegersWithIterate
~~~~

Finally, this is the stream of even integers
described in terms of itself by using `#::`,
the stream cons operator:

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN EvenIntegersRecursive
to:   END EvenIntegersRecursive
~~~~

A prime stream
--------------

Let’s create, one more time, the stream of integers.
When we print it, we’ll see that it only knows it starts with zero.
Nobody has any idea what could be there after that:

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN IntegersPrinted
to:   END IntegersPrinted
~~~~

The stream of prime numbers is easy to create:

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN PrimeStream
to:   END PrimeStream
~~~~

The prime stream knows it starts with 2,
and to learn that it needed to consume
a couple of values from the integer stream:

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN PrintedBeforeGettingPrime
to:   END PrintedBeforeGettingPrime
~~~~

If we ask for the fifth prime,
even more values from the integer stream
need to be evaluated:

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN PrintedAfterGettingPrime
to:   END PrintedAfterGettingPrime
~~~~

And now comes the party trick:
one can assign one prime number to each person
without worrying about how many people there are!

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN PrimePeople
to:   END PrimePeople
~~~~

You can tell how fun my parties can get.


Back to Kolakoski
-----------------

The Kolakoski sequence begins with 1, 2, and 2,
and then it uses itself to calculate the rest of the values:

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN RecursiveKolakoski
to:   END RecursiveKolakoski
~~~~

Note that we have to drop the first two members of the sequence
since the first two runs are already provided.

The numbers in the sequence tell how long each run is,
but not what the number in the run is.
For that we need an infinite sequence of alternating ones and twos,
which I’ll define like this:

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN OnesAndTwos
to:   END OnesAndTwos
~~~~

Can you think of other ways to create that sequence?
I know I can:

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN AlternativeOnesAndTwos
to:   END AlternativeOnesAndTwos
~~~~

Creating one run of the same thing
is something Scala already knows how to do:

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN SeqFill
to:   END SeqFill
~~~~

What’s left is just assembling the pieces.
And when working with collections,
“assembling the pieces” is spelled with an `f`:

~~~~ {.include .scala}
file: kolakoski.sc
from: BEGIN KolakoskiRest
to:   END KolakoskiRest
~~~~

Simple, isn’t it?
Streams are an effective way to define this infinite sequence
in a way that corresponds quite directly to the prose description,
once you learn what `#::` is and how `flatMap` is pronounced.

_If you liked this post,
I invite you to continue lazily reading
the infinite stream of articles
I have created for my fine visitors.
Be aware that evaluation could take a while._


