final class StatementMachine(environment: Map[String, Expr]) {
  var env: Map[String, Expr] = environment
  val exprMachine = new Machine()

  def run(st: Statement): Option[Statement] = try {
    Some(runInternal(st))
  }
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
          Assignment(n, exprMachine.reductionStep(v, env))
        else {
          env += n -> v
          println()
          DoNothing()
        }

      case IfElseStatement(c, t, f) =>
        if (c.isReducible) IfElseStatement(exprMachine.reductionStep(c, env), t, f)
        else if(c.toBoolean) t else f

      case While(c, body) =>
        val reduced_cond = if (c.isReducible) {
          exprMachine.run(c, env).getOrElse(Bool(false)).toBoolean
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
