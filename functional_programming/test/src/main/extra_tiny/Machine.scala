final class Machine {
  def reduce(expr: Expr, env: Map[String, Expr]): Expr = try runInternal(expr, env)
    catch {
      case exception: TinyException =>
        println("TinyException: " + exception.getMessage + "\n")
        expr
    }

  private def runInternal(expr: Expr, env: Map[String, Expr]): Expr = {
    println(s"Environment:\n$env")
    println(s"Expr:\n$expr\n")

    if (expr.isReducible) runInternal(reductionStep(expr, env), env)
    else expr
  }

  def reductionStep(expr: Expr, env: Map[String, Expr]): Expr = {
    def binary_operator_reduction(operator: (Expr, Expr) => Expr, l: Expr, r: Expr): Expr = {
      if (l.isReducible) operator(reductionStep(l, env), r)
      else if (r.isReducible) operator(l, reductionStep(r, env))
      else expr.evaluate
    }

    expr match {
      case Number(_) | Bool(_) => expr

      case Mult(l, r) => binary_operator_reduction(Mult.apply, l, r)

      case Sum(l, r) => binary_operator_reduction(Sum.apply, l, r)

      case Less(l, r) => binary_operator_reduction(Less.apply, l, r)

      case Var(x) =>
        if (env.contains(x)) env(x)
        else throw TinyException(s"Can't find variable $x in the environment")

      case IfElse(c, t, f) =>
        if (c.isReducible) IfElse(reductionStep(c, env), t, f)
        else
          if(c.toBoolean) runInternal(t, env)
          else runInternal(f, env)
    }
  }

  def run(st: Statement, env: Map[String, Expr]): Map[String, Expr] =
    try runInternal(st, env)
    catch {
      case exception: TinyException =>
        println("TinyException: " + exception.getMessage + "\n")
        env + ("___error" -> Except(exception.getMessage))
    }

  private def runInternal(st: Statement, env: Map[String, Expr]): Map[String, Expr] = {
    println(s"Environment:\n$env")
    println(s"Statement:\n$st\n")

    if (st.isReducible) reductionStep(st, env)
    else env
  }

  private def reductionStep(st: Statement, env: Map[String, Expr]): Map[String, Expr] = {
    st match {
      case Assignment(n, v) =>
        if (v.isReducible)
          runInternal(Assignment(n, reductionStep(v, env)), env)
        else {
          env + (n -> v)
        }

      case IfElseStatement(c, t, f) =>
        if (c.isReducible) runInternal(IfElseStatement(reductionStep(c, env), t, f), env)
        else if(c.toBoolean) runInternal(t, env) else runInternal(f, env)

      case While(c, body) =>
        val reduced_cond = if (c.isReducible) reduce(c, env).toBoolean else c.toBoolean

        if (reduced_cond) {
          run(While(c, body), run(body, env))
        }
        else env

      case Sequence(s) =>
        if (s.isEmpty) env else {
          runInternal(Sequence(s.tail), runInternal(s.head, env))
        }

      case _ => env
    }
  }
}
