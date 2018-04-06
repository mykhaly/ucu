sealed trait Expr {
  def evaluate: Expr = {
    def evaluateOperand(operand: Expr): Expr = operand match {
      case Number(_) | Bool(_) => operand

      case _ => operand.evaluate
    }

    this match {
      case Sum(l, r) => Number(evaluateOperand(l) + evaluateOperand(r))

      case Mult(l, r) => Number(evaluateOperand(l) * evaluateOperand(r))

      case Less(l, r) => Bool(evaluateOperand(l) < evaluateOperand(r))

      case IfElse(c, t, f) =>
        if (c.toBoolean) t.evaluate
        else f.evaluate
    }
  }

  def show: String = this match {
    case Number(n) => n.toString

    case Bool(b) => b.toString

    case Var(x) => x

    case Sum(l, r) => l.show + " + " + r.show

    case Mult(l, r) =>
      def showOperand(operand: Expr): String = operand match {
        case Sum(_, _) => "(" + operand.show + ")"
        case _ => operand.show
      }

      showOperand(l) + " * " + showOperand(r)

    case Less(l, r) =>
      def showOperand(operand: Expr): String = operand match {
        case Sum(_, _) | Mult(_, _) | IfElse(_, _, _) => "(" + operand.show + ")"
        case _ => operand.show
      }

      showOperand(l) + " < " + showOperand(r)

    case IfElse(c, t, f) =>
      "if " + c.show + " then " + t.show + " else " + f.show
  }

  def isReducible: Boolean = {
    this match {
      case Number(_) | Bool(_) => false

      case _ => true
    }
  }

  def toInt: Int = this match {
    case Number(n) => n

    case _ =>
      val expr_type = this.getClass
      throw TinyException(s"Can't receive integer value from object of $expr_type")
  }

  def toBoolean: Boolean = this match {
    case Bool(b) => b

    case _ =>
      val expr_type = this.getClass
      throw TinyException(s"Can't receive boolean value from object of $expr_type")
  }

  override def toString: String = this.show

  def +(that: Expr): Int = this.toInt + that.toInt

  def *(that: Expr): Int = this.toInt * that.toInt

  def <(that: Expr): Boolean = this.toInt < that.toInt
}

case class Number(n: Int) extends Expr

case class Var(x: String) extends Expr

case class Sum(l: Expr, r: Expr) extends Expr

case class Mult(l: Expr, r: Expr) extends Expr

case class Bool(b: Boolean) extends Expr

case class Less(l: Expr, r: Expr) extends Expr

case class IfElse(condition: Expr, on_true: Expr, on_false: Expr) extends Expr
