sealed trait Statement {
  def show: String = this match {
    case DoNothing() => "THE END;"
    case Assignment(name, value) => s"$name = $value;"
    case IfElseStatement(c, t, f) =>
      s"IF (" + c.show + "): \n\t" + t.show + "\nELSE:\n\t" + f.show
    case While(c, b) =>
      s"WHILE(" + c.show + "):\n\t" + b.show
    case Sequence(s) =>
      if (s.nonEmpty) "\n" + s.head.show + "\n" + Sequence(s.tail).show else ""
  }

  def isReducible: Boolean = this match {
    case DoNothing() => false
    case _ => true
  }

  override def toString: String = this.show
}

case class DoNothing() extends Statement

case class Assignment(name: String, value: Expr) extends Statement

case class IfElseStatement(cond: Expr, on_true: Statement, on_false: Statement) extends Statement

case class While(cond: Expr, body: Statement) extends Statement

case class Sequence(statements: List[Statement]) extends Statement
