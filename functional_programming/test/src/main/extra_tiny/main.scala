object main {
  def main(args: Array[String]): Unit = {
//    new Machine().run(Mult(Mult(Number(3), Number(2)), Mult(Number(7), Number(6))))
//    new Machine().run(Mult(Number(2), Sum(Number(3), Number(6))))
    new Machine().run(Sum(Mult(Number(2), Number(3)), Number(4)))
  }

}

