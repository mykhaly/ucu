final class Machine (environment: Map[String, Expr]) {
  var env: Map[String, Expr] = environment

  def run(expr: Expr): Option[Expr] = try Some(runInternal(expr))
    catch {
      case exception: TinyException =>
        println("TinyException: " + exception.getMessage + "\n")
        None
    }

  private def runInternal(expr: Expr): Expr = {
    println(s"Expr: $expr\n")

    if (expr.isReducible) runInternal(reductionStep(expr))
    else expr
  }

  private def reductionStep(expr: Expr): Expr = {
    def binary_operator_reduction(operator: (Expr, Expr) => Expr, l: Expr, r: Expr): Expr = {
      if (l.isReducible) operator(reductionStep(l), r)
      else if (r.isReducible) operator(l, reductionStep(r))
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
        if (c.isReducible) IfElse(reductionStep(c), t, f)
        else
          if(c.toBoolean) reductionStep(t)
          else reductionStep(f)
    }
  }

  def run(st: Statement): Option[Statement] = try Some(runInternal(st))
    catch {
      case exception: TinyException =>
        println("TinyException: " + exception.getMessage + "\n")
        None
    }

  private def runInternal(st: Statement): Statement = {
    println(s"Environment: $env")
    println(s"Statement: $st\n")

    if (st.isReducible) runInternal(reductionStep(st))
    else st
  }

  private def reductionStep(st: Statement): Statement = {
    st match {
      case Assignment(n, v) =>
        if (v.isReducible)
          Assignment(n, reductionStep(v))
        else {
          env += n -> v
          println()
          DoNothing()
        }

      case IfElseStatement(c, t, f) =>
        if (c.isReducible) IfElseStatement(reductionStep(c), t, f)
        else if(c.toBoolean) t else f

      case While(c, body) =>
        val reduced_cond = if (c.isReducible) {
          run(c).getOrElse(Bool(false)).toBoolean
        } else c.toBoolean

        if (reduced_cond) {
          run(body)
          While(c, body)
        }
        else DoNothing()

      case Sequence(s) =>
        if (s.isEmpty) DoNothing() else {
          runInternal(s.head)
          runInternal(Sequence(s.tail))
        }

      case _ => DoNothing()
    }
  }
}
