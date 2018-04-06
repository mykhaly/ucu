
final class Machine {
  def run(expr: Expr, env: Map[String, Expr]): Option[Expr] = {
    println(expr)

    if (expr.isReducible) {
      try
        run(reductionStep(expr, env), env)
      catch {
        case exception: TinyException => println(exception.getMessage)
          None
      }
    }
    else {
      Option(expr)
    }
  }

  def reductionStep(expr: Expr, env: Map[String, Expr]): Expr = {
    expr match {
      case Number(_) => expr

      case Bool(_) => expr

      case Mult(l, r) =>
        if (l.isReducible) Mult(reductionStep(l, env), r)
        else if (r.isReducible) Mult(l, reductionStep(r, env))
        else expr.evaluate

      case Sum(l, r) =>
        if (l.isReducible) Sum(reductionStep(l, env), r)
        else if (r.isReducible) Sum(l, reductionStep(r, env))
        else expr.evaluate

      case Var(x) =>
        if (env.contains(x)) env(x)
        else throw TinyException(s"Can't find variable $x in environment")

      case Less(l, r) =>
        if (l.isReducible) Less(reductionStep(l, env), r)
        else if (r.isReducible) Less(l, reductionStep(r, env))
        else expr.evaluate
    }
  }
}
