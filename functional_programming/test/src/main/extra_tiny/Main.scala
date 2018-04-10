object Main {
  def main(args: Array[String]): Unit = {
    val environment = Map(
      "x" -> Number(1),
      "y" -> Number(3),
      "t" -> Bool(true)
    )
    val machine = new Machine(environment)
    // 3 * 2 * 7 * 6
    val expression1 = Mult(Mult(Number(3), Number(2)), Mult(Number(7), Number(6)))
    // 2 * (3 + 6)
    val expression2 = Mult(Number(2), Sum(Number(3), Number(6)))
    // x * 3 + y
    val expression3 = Sum(Mult(Var("x"), Number(3)), Var("y"))
    // (z + 2) * (4 + y)
    val expression4 = Mult(Sum(Var("z"), Number(2)), Sum(Number(4), Var("y")))
    // (3 + 6) < false
    val expression5 = Less(Sum(Number(3), Number(6)), Bool(false))
    // (3 + 6) < (x * 3 + y)
    val expression6 = Less(
      Sum(Number(3), Number(6)),
      Sum(Mult(Var("x"), Number(3)), Var("y"))
    )
    // 2 + true
    val expression7 = Sum(Number(2), Var("t"))
    // if (3 + 6) < 10 then (x + 3) * y else 5
    val expression8 = IfElse(
      Less(Sum(Number(3), Number(6)), Number(10)),
      Mult(Sum(Var("x"), Number(3)), Var("y")),
      Number(5))

    machine.run(expression1)
    machine.run(expression2)
    machine.run(expression3)
    machine.run(expression4)
    machine.run(expression5)
    machine.run(expression6)
    machine.run(expression7)
    machine.run(expression8)

    val statement1 = DoNothing()
    val statement2 = Assignment("y", Sum(Sum(Number(3), Number(4)), Number(4)))
    val statement3 = IfElseStatement(
      Less(Number(5), Number(4)),
      DoNothing(),
      Assignment("y", Sum(Sum(Number(3), Number(4)), Number(4)))
    )
    val statement4 = IfElseStatement(
      Less(Number(3), Number(4)),
      Assignment("a", Sum(Mult(Number(3), Number(4)), Number(4))),
      Assignment("b", Sum(Mult(Number(3), Number(4)), Number(4)))
    )
    val statement5 = IfElseStatement(
      IfElse(Less(Number(2), Number(5)), Less(Number(3), Number(4)), Bool(false)),
      DoNothing(),
      Assignment("c", Mult(Sum(Number(3), Number(4)), Number(4)))
    )
    val statement6 = While(
      Less(Sum(Var("x"), Number(3)), Number(7)),
      Assignment("x", Sum(Var("x"), Number(1)))
    )
    val statement7 = Sequence(List[Statement](
      Assignment("x", Bool(true)),
      IfElseStatement(Var("x"), Assignment("x", Number(1)), Assignment("x", Number(0))),
      DoNothing()
    ))

    machine.run(statement1)
    machine.run(statement2)
    machine.run(statement3)
    machine.run(statement4)
    machine.run(statement5)
    machine.run(statement6)
    machine.run(statement7)
  }
}
