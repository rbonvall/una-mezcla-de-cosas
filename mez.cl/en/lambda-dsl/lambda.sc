// vim: set ft=scala:

@main
def main(): Unit = {

  object First {
    // BEGIN TermExample
    sealed trait Term
    case class Variable(name: Symbol) extends Term
    case class Abstraction(param: Variable, body: Term) extends Term
    case class Application(fn: Term, arg: Term)

//    val `1+2` =
//      Application(
//        Application(
//
//        ),
//        Abstraction(
//          Variable('s),
//          Abstraction(
//            Variable('z),
//            Application('s, 'z)))
//      )
    // END TermExample
  }

  object Second {
    sealed trait Term
    case class Variable(name: Symbol) extends Term
    case class Abstraction(param: Variable, body: Term) extends Term
    case class Application(fn: Term, arg: Term)

    // BEGIN ImplicitVar
    object Term {
      implicit def symToVar(s: Symbol): Variable = Variable(s)
    }
    // END ImplicitVar

    // BEGIN Identity
    val I = Abstraction('x, 'x)
    // END Identity

    val `5!` = Application('a, 'b)
    assert(Application('a, 'b) == Application(Variable('a), Variable('b)))
  }

  object Third {
    sealed trait Term {
      def $(that: Term) = Application(this, that)
    }
    case class Variable(name: Symbol) extends Term
    case class Abstraction(param: Variable, body: Term) extends Term
    case class Application(fn: Term, arg: Term)

    object Term {
      implicit def symToVar(s: Symbol): Variable = Variable(s)
    }

    val `5!` = 'a $ 'b

  }

  object Fourth {
    sealed trait Term {
      def $(that: Term) = Application(this, that)
    }
    case class Variable(name: Symbol) extends Term
    case class Abstraction(param: Variable, body: Term) extends Term
    case class Application(fn: Term, arg: Term)

    object Term {
      implicit def symToVar(s: Symbol): Variable = Variable(s)
    }

    // BEGIN LambdaDefinition
    def λ(p: Variable, ps: Variable*)(body: Term) =
      Abstraction(p, ps.foldRight(body) { (v, b) ⇒ Abstraction(v, b) })
    // END LambdaDefinition

    val I = λ('x) { 'x }

    // BEGIN Lambda 1+2
    val `1+2` =
      λ('m, 'n) {
        'm $ ( λ('i, 's, 'z) { 'i $ 'z $ ('s $ 'z) }) $ 'n
      } $ λ('s, 'z) { 's $ 'z } $ λ('s, 'z) { 's $ ('s $ 'z) }
    // END Lambda 1+2

    val (s, m, i, n, z) = ('s, 'm, 'i, 'n, 'z)
    val succ = λ(i, s, z) { i $ z $ (s $ z) }
    val one = λ(s, z) { s $ z }
    val two = λ(s, z) { s $ (s $ z) }
    val add = λ(m, n) { m $ succ $ n }
    val onePlusTwo = add $ one $ two

  }


  // Force instantiation of objects.
  First
  Second
  Third
  Fourth

}
