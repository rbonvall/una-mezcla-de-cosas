---
title: Folding and scanning state
date: 2018-03-24
author: Roberto Bonvallet
panflute-filters: ["../../../bin/include.py"]
abstract: |
    This article is nothing but a summary
    of a talk I gave in my local [Scala Meetup]().
    You can enjoy all the content, but none of the pizza.
---

If you have dabbled in functional programming,
for sure you have learned about `map` and `filter`,
the most well-known combinators in the hood.

Possibly you have already replaced some of your _for_ loops
with a cute little lambda.
Once you even chained a `map` and a `filter`
and stared at the code for two minutes with a smile in your face.
In another occasion you went as far as nesting two `map`s,
and chuckled audibly when the unit test passed.

And then at some point you wondered:
is there life after `map` and `filter`?

In pursue of my agenda of luring more unsuspecting programmers
into the dark delights of this noble paradigm,
I will introduce you to other two powerful combinators,
`foldLeft` and `scanLeft`,
and demonstrate how to leverage them
to elegantly handle some state in Scala without using variables.


Fold and scan
=============

Folding a sequence:


    def add(total: Int, next: Int): Int = total + next

    val ns = List(22, 11, 44, 33, 77)
    ns.foldLeft(0) { (total, next) => subtotal + next }

A list can also be folded into a value
of a type that’s different than its members.
In that case, the “zero” value has to be of the desired result type,
and the combining function has to accept
one parameter of either type.

    ns.foldLeft("") { (text, num) => test ++ ";" ++ num.toString }
    // "22;11;44;33;77"


**Exercise:** write `foldLeft` in terms of `scanLeft`.

**Exercise:** write `scanLeft` in terms of `foldLeft`.


The hexagonal grid
==================

After leaving out all the fluff,
[the problem from day XX](http://caca)
is basically this:
start in the center of an hexagonal grid,
follow a given list of directions (N, NE, NW, S, SE, or SW),
and find out how far you end up from the initial place.

<style>
  #hexgrid path.hexcell {
    stroke: gray;
    stroke-width: 0.2;
  }
  #hexgrid path.hexcell.even {
    fill: #fcc;
  }
  #hexgrid path.hexcell.odd {
    fill: #ccc;
  }
  #thePath {
    fill: none;
    stroke: #333;
    stroke-width: 0.3;
    stroke-linecap: round;
  }
  #tryPath {
    width: 90%;
    font-size: 24px;
    padding: 0.5ex;
  }
</style>
<script>
</script>
<input id="tryPath" value="n n n ne ne s s s"></input>

<span id="pathstatus"></span>
<svg width="100%" height="400" viewBox="-25 -25 50 50">
  <g id="hexgrid">
    <path class="hexcell even" d="M  0  0 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <!-- -->
    <path class="hexcell odd"  d="M  3  2 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd"  d="M -3  2 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd"  d="M  3 -2 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd"  d="M -3 -2 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd"  d="M  0  4 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd"  d="M  0 -4 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <!-- -->
    <path class="hexcell even" d="M  0  8 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  3  6 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  6  4 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  6  0 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  6 -4 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  3 -6 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  0 -8 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M -3 -6 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M -6 -4 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M -6  0 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M -6  4 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M -3  6 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <!-- -->
    <path class="hexcell odd" d="M   0  12 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   3  10 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   6   8 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   9   6 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   9   2 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   9  -2 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   9  -6 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   6  -8 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   3 -10 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   0 -12 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  -3 -10 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  -6  -8 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  -9  -6 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  -9  -2 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  -9   2 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  -9   6 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  -6   8 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  -3  10 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <!-- -->
    <path class="hexcell even" d="M   0  16 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M   3  14 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M   6  12 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M   9  10 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  12   8 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  12   4 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  12   0 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  12  -4 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  12  -8 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M   9 -10 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M   6 -12 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M   3 -14 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M   0 -16 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  -3 -14 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  -6 -12 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  -9 -10 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M -12  -8 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M -12  -4 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M -12   0 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M -12   4 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M -12   8 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  -9  10 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  -6  12 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell even" d="M  -3  14 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <!-- -->
    <path class="hexcell odd" d="M   0  20 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   3  18 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   6  16 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   9  14 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  12  12 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  15  10 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  15   6 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  15   2 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  15  -2 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  15  -6 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  15 -10 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  12 -12 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   9 -14 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   6 -16 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   3 -18 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M   0 -20 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  -3 -18 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  -6 -16 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  -9 -14 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M -12 -12 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M -15 -10 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M -15  -6 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M -15  -2 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M -15   2 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M -15   6 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M -15  10 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M -12  12 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  -9  14 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  -6  16 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
    <path class="hexcell odd" d="M  -3  18 m 2 0 l -1 2 l -2 0 l -1 -2 l 1 -2 l 2 0 z"></path>
  </g>
  <path id="thePath" d="M 0 0 l 2 0"></path>
</svg>
<script>
  const thePath = document.getElementById('thePath');
  const tryPath = document.getElementById('tryPath');

  const movePattern = /[ns][ew]?/gi;
  function parseMoves(movesAsText) {
    const moves = [];
    movesAsText.replace(movePattern, m => moves.push(m.toLowerCase()));
    return moves;
  }

  const deltas = {
    n:  [ 0,  4],
    s:  [ 0, -4],
    ne: [ 3,  2],
    se: [ 3, -2],
    nw: [-3,  2],
    sw: [-3, -2]
  }
  function drawPath() {
    const moves = parseMoves(tryPath.value);
    const pathDescription = moves
      .filter(m => deltas.hasOwnProperty(m))
      .map(m => {
        const [dx, dy] = deltas[m];
        return `l ${dx} ${-dy}`
      })
      .join(' ');
    thePath.setAttribute('d', `M 0 0 ${pathDescription}`);
  }
  drawPath();
  tryPath.addEventListener('keyup', drawPath);

</script>






