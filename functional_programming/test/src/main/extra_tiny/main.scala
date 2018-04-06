object main {
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

    machine.run(expression1, environment)
    println()
    machine.run(expression2, environment)
    println()
    machine.run(expression3, environment)
    println()
    machine.run(expression4, environment)
    println()
    machine.run(expression5, environment)
    println()
    machine.run(expression6, environment)
    println()
    machine.run(expression7, environment)
    println()
  }
}
