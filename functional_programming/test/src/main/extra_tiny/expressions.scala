sealed trait Expr {
  def eval_binary_operation(operand: Expr): Expr = operand match {
    case Number(_) => operand
    case Bool(_) => operand
    case _ => operand.evaluate
  }

  def evaluate: Expr = this match {
    case Sum(l, r) => Number(
      eval_binary_operation(l).asInstanceOf[Number].n +
      eval_binary_operation(r).asInstanceOf[Number].n
    )
    case Mult(l, r) => Number(
      eval_binary_operation(l).asInstanceOf[Number].n *
      eval_binary_operation(r).asInstanceOf[Number].n
    )
    case <(l, r) => Bool(
      eval_binary_operation(l).asInstanceOf[Number].n <
      eval_binary_operation(r).asInstanceOf[Number].n
    )
    case _ => this
  }

  def show: String = this match {
    case Number(n) => n.toString
    case Sum(left, right) => left.show + " + " + right.show
    case Var(x) => x
    case Mult(_, _) => this.show
    case Bool(b) => b.toString
    case <(_, _) => this.show
  }

  def isReducible: Boolean = {
    this match {
      case Number(_) => false
      case Bool(_) => false
      case _ => true
    }
  }

  override def toString: String = this.show
}


case class Number(n: Int) extends Expr


case class Var(x: String) extends Expr


case class Sum(l: Expr, r: Expr) extends Expr


case class Mult(l: Expr, r: Expr) extends Expr {
  private def show_operand(operand: Expr): String = {
    operand match {
      case Sum(_, _) => "(" + operand.show + ")"
      case _ => operand.show
    }
  }

  override def show: String = show_operand(l) + " * " + show_operand(r)
}

case class Bool(b: Boolean) extends Expr

case class <(l: Expr, r: Expr) extends Expr {
  private def show_operand(operand: Expr): String = {
    operand match {
      case Sum(_, _) => "(" + operand.show + ")"
      case Mult(_, _) => "(" + operand.show + ")"
      case _ => operand.show
    }
  }

  override def show: String = show_operand(l) + " < " + show_operand(r)
}