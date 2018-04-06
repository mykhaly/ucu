sealed trait Expr {
  def evaluate: Expr = {
    def eval_operand(operand: Expr): Expr = operand match {
      case Number(_) => operand
      case Bool(_) => throw TinyException(s"Operands of type Boolean are not supported")
      case _ => operand.evaluate
    }

    this match {
      case Sum(l, r) => Number(
        eval_operand(l).asInstanceOf[Number].n +
        eval_operand(r).asInstanceOf[Number].n
      )
      case Mult(l, r) => Number(
        eval_operand(l).asInstanceOf[Number].n *
        eval_operand(r).asInstanceOf[Number].n
      )
      case Less(l, r) => Bool(
        eval_operand(l).asInstanceOf[Number].n <
        eval_operand(r).asInstanceOf[Number].n
      )
      case IfElse(c, t, f) => if (c.asInstanceOf[Bool].b) t.evaluate else f.evaluate
      case _ => this
    }
  }

  def show: String = this match {
    case Number(n) => n.toString
    case Sum(left, right) => left.show + " + " + right.show
    case Var(x) => x
    case Bool(b) => b.toString
    case _ => this.show
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


case class Less(l: Expr, r: Expr) extends Expr {
  private def show_operand(operand: Expr): String = {
    operand match {
      case Sum(_, _) => "(" + operand.show + ")"
      case Mult(_, _) => "(" + operand.show + ")"
      case _ => operand.show
    }
  }

  override def show: String = show_operand(l) + " < " + show_operand(r)
}


case class IfElse(condition: Expr, on_true: Expr, on_false: Expr) extends Expr {
  private def show_operand(operand: Expr): String = {
    operand match {
      case Sum(_, _) => "(" + operand.show + ")"
      case Mult(_, _) => "(" + operand.show + ")"
      case Less(_, _) => "(" + operand.show + ")"
      case _ => operand.show
    }
  }

  override def show: String =
    "if " + show_operand(condition) + " then " + show_operand(on_true) + " else " + show_operand(on_false)
}
