class Point(val x: Double, val y: Double) {
  def compareByX(other: Point): Boolean = {
    this.x < other.x
  }

  def compareByY(other: Point): Boolean = {
    this.y < other.y
  }

  def distanceTo(other: Point): Double = {
    Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2))
  }

  override def toString: String = {
    s"x: $x, y: $y"
  }
}