object Main {
  def main(args: Array[String]): Unit = {
    val machine = new Machine()
    val environment = Map(
      "x" -> Number(1),
      "y" -> Number(3),
      "t" -> Bool(true)
    )
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

    machine.run(expression1, environment)
    machine.run(expression2, environment)
    machine.run(expression3, environment)
    machine.run(expression4, environment)
    machine.run(expression5, environment)
    machine.run(expression6, environment)
    machine.run(expression7, environment)
    machine.run(expression8, environment)
  }
}
