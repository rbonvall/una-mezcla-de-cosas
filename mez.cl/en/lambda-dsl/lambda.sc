// vim: set ft=scala:

@main
def main(): Unit = {

  object Ugly {
    // BEGIN TermExample
    sealed trait Term
    case class Variable(name: Symbol)                   extends Term
    case class Abstraction(param: Variable, body: Term) extends Term
    case class Application(fn: Term, arg: Term)         extends Term

    val `1+2` =
      Application(
        Application(
          Abstraction(Variable('m),
            Abstraction(Variable('n),
              Application(
                Application(
                  Variable('m),
                  Abstraction(Variable('i),
                    Abstraction(Variable('s),
                      Abstraction(Variable('z),
                        Application(
                          Application( Variable('i), Variable('s) ),
                        Application( Variable('s), Variable('z) )))))),
              Variable('n)))),
            Abstraction(Variable('s),
              Abstraction(Variable('z),
                Application( Variable('s), Variable('z) )))),
        Abstraction(Variable('s),
          Abstraction(Variable('z),
            Application(
              Variable('s),
              Application( Variable('s), Variable('z) )))))
    // END TermExample

    // Just to be able to compare this term to one created with the new DSL'd types.
    def toDsl(t: Term): DSL.Term = t match {
      case Variable(v)                 ⇒ DSL.Variable(v)
      case Abstraction(Variable(v), b) ⇒ DSL.Abstraction(DSL.Variable(v), toDsl(b))
      case Application(f, a)           ⇒ DSL.Application(toDsl(f),        toDsl(a))
    }

  }

  object DSL {
    // BEGIN TermWithInfixApplication
    sealed trait Term {
      def $(that: Term) = Application(this, that)
    }
    // END TermWithInfixApplication

    case class Variable   (name: Symbol)                extends Term
    case class Abstraction(param: Variable, body: Term) extends Term
    case class Application(fn: Term, arg: Term)         extends Term

    // BEGIN ImplicitVar
    object Term {
      implicit def symToVar(s: Symbol): Variable = Variable(s)
    }
    // END ImplicitVar

    // BEGIN LambdaDefinition
    def λ(p: Variable, ps: Variable*)(body: Term) =
      Abstraction(p, ps.foldRight(body) { (v, b) ⇒ Abstraction(v, b) })
    // END LambdaDefinition
  }

  object examples {

    import DSL._
    import Term._

    assert(Application('a, 'b) == Application(Variable('a), Variable('b)))

    object PreLambdaExamples {
      // BEGIN Identity
      val I = Abstraction('x, 'x)
      // END Identity

      // BEGIN Omega
      val ω = Abstraction('f, 'f $ 'f)
      // END Omega
    }
    assert(PreLambdaExamples.I == Abstraction(Variable('x), Variable('x)))
    assert(PreLambdaExamples.ω == Abstraction(Variable('f), Application(Variable('f), Variable('f))))

    // BEGIN LeftAssociativeApplication
    val t =  'a $  'b  $ 'c
    val l = ('a $  'b) $ 'c
    val r =  'a $ ('b  $ 'c)
    assert(t == l)
    assert(t != r)
    // END LeftAssociativeApplication

    // BEGIN ClassicCombinators
    val I = λ('x) { 'x }
    val T = λ('x, 'y) { 'x }
    val F = λ('x, 'y) { 'y }
    val ¬ = λ('p) { 'p $ F $ T }
    val ω = λ('f) { 'f $ 'f }
    val Ω = ω $ ω
    val S = λ('x, 'y, 'z) { 'x $ 'z $ ('y $ 'z) }
    // END ClassicCombinators

    // BEGIN Lambda 1+2
    val `1+2` =
      λ('m, 'n) {
        'm $ ( λ('i, 's, 'z) { 'i $ 's $ ('s $ 'z) }) $ 'n
      } $ λ('s, 'z) { 's $ 'z } $ λ('s, 'z) { 's $ ('s $ 'z) }
    // END Lambda 1+2

    // BEGIN Nicer 1+2
    val (s, m, i, n, z) = ('s, 'm, 'i, 'n, 'z)
    val one = λ(s, z) { s $ z }
    val two = λ(s, z) { s $ (s $ z) }
    val scc = λ(i, s, z) { i $ s $ (s $ z) }
    val add = λ(m, n) { m $ scc $ n }
    val onePlusTwo = add $ one $ two
    // END Nicer 1+2

    assert(`1+2` == onePlusTwo)
    assert(`1+2` == Ugly.toDsl(Ugly.`1+2`))

    def freeVariables(t: Term): Set[Symbol] = t match {
      case Variable(v) ⇒ Set(v)
      case Abstraction(Variable(v), b) ⇒ freeVariables(b) - v
      case Application(f, x) ⇒ freeVariables(f) ++ freeVariables(x)
    }

    val withFree = λ('y) { 'x $ 'y $ λ('z) { 'z $ 'y} }
    assert(freeVariables(withFree) == Set('x))

    println(":)")
  }

  // Force instantiation
  examples

}
