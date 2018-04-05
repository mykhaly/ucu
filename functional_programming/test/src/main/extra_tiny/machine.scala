final class Machine {
  def run(expr: Expr): Expr = {
    println(expr)

    if (expr.isReducible) {
      run(reductionStep(expr))
    }
    else {
      expr
    }
  }

  def reductionStep(expr: Expr): Expr = {
    expr.reduce
  }
}
