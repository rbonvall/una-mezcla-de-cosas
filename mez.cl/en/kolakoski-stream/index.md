---
title: The Kolakoski stream
date: 2017-12-xx
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


Yes, the previous paragraph was just a filler
so you couldn’t peek 
