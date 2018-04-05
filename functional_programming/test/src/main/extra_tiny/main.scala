object main {
  def main(args: Array[String]): Unit = {
//    new Machine().run(Mult(Mult(Number(3), Number(2)), Mult(Number(7), Number(6))))
//    new Machine().run(Mult(Number(2), Sum(Number(3), Number(6))))
//    val expression = Sum(Mult(Var("x"), Number(3)), Var("y"))
    val expression = Mult(Sum(Var("x"), Number(2)), Sum(Number(4), Var("y")))
    val environment = Map("x" -> 1, "y" -> 3)
    new Machine().run(expression, environment)
  }
}

