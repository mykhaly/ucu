final class Machine {
  def run(expr: Expr, env: Map[String, Expr]): Option[Expr] = {
    println(expr)

    if (expr.isReducible) try
      run(reductionStep(expr, env), env)
    catch {
      case exception: TinyException => println(exception.getMessage)
        None
    }
    else Option(expr)
  }

  def reductionStep(expr: Expr, env: Map[String, Expr]): Expr = {
    def binary_operator_reduction(operator: (Expr, Expr) => Expr, l: Expr, r: Expr): Expr = {
      if (l.isReducible) operator(reductionStep(l, env), r)
      else if (r.isReducible) operator(l, reductionStep(r, env))
      else expr.evaluate
    }

    expr match {
      case Number(_) => expr

      case Bool(_) => expr

      case Mult(l, r) => binary_operator_reduction(Mult.apply, l, r)

      case Sum(l, r) => binary_operator_reduction(Sum.apply, l, r)

      case Less(l, r) => binary_operator_reduction(Less.apply, l, r)

      case Var(x) =>
        if (env.contains(x)) env(x)
        else throw TinyException(s"Can't find variable $x in environment")

      case IfElse(c, t, f) =>
        if (c.isReducible) IfElse(reductionStep(c, env), t, f)
        else
          if(c.asInstanceOf[Bool].b) reductionStep(t, env)
          else reductionStep(f, env)
    }
  }
}
