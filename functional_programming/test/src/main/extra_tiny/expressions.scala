sealed trait Expr {
  def evaluate: Expr = this match {
    case Number(n) => Number(n)
    case Sum(l, r) => {
      val left = l match {
        case Number(_) => l
        case _ => l.evaluate
      }
      val right = r match {
        case Number(_) => r
        case _ => r.evaluate
      }
      Number(left.asInstanceOf[Number].n + right.asInstanceOf[Number].n)
//      l.evaluate.asInstanceOf[Number] + r.evaluate.asInstanceOf[Number]
    }
    case Mult(l, r) => {
      val left = l match {
        case Number(_) => l
        case _ => l.evaluate
      }
      val right = r match {
        case Number(_) => r
        case _ => r.evaluate
      }
      Number(left.asInstanceOf[Number].n * right.asInstanceOf[Number].n)
    }
  }

  def show: String = this match {
    case Number(n) => n.toString
    case Sum(left, right) => left.show + " + " + right.show
    case Var(x) => x
    case Mult(_, _) => this.show
  }

  def isReducible: Boolean = {
    this match {
      case Number(_) => false
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
