object main {
  def main(args: Array[String]): Unit = {
    val machine = new Machine()
    val environment = Map("x" -> 1, "y" -> 3)
    val expression1 = Mult(Mult(Number(3), Number(2)), Mult(Number(7), Number(6)))
    val expression2 = Mult(Number(2), Sum(Number(3), Number(6)))
    val expression3 = Sum(Mult(Var("x"), Number(3)), Var("y"))
    val expression4 = Mult(Sum(Var("z"), Number(2)), Sum(Number(4), Var("y")))
    machine.run(expression1, environment)
    println()
    machine.run(expression2, environment)
    println()
    machine.run(expression3, environment)
    println()
    machine.run(expression4, environment)
    println()
  }
}
