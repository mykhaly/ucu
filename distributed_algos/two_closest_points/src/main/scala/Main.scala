import org.scalameter._
import scala.util.Random


object Main {
  def median(list: List[Point]): (Double, List[Point], List[Point]) = {
    val (lower, upper) = list.sortWith(_ compareByX  _).splitAt(list.size / 2)
    val middleX = upper.head.x
    (middleX, lower, upper)
  }

  def boundaryMerge(closestPair: List[Point], input: List[Point], minDistance: Double, medianX: Double): (List[Point], Double) = {
    val pointsInsideBoundary = input
      .filter(p => p.x <= medianX + minDistance && p.x >= medianX - minDistance)
      .sortWith(_ compareByY _)
    var newClosestPair = closestPair
    var newMinDistance = minDistance
    for (i <- pointsInsideBoundary.indices) {
      for (j <- 1 until Math.min(8, pointsInsideBoundary.length - i)) {
        if ((pointsInsideBoundary(i) distanceTo pointsInsideBoundary(i + j)) < newMinDistance) {
          newMinDistance = pointsInsideBoundary(i) distanceTo pointsInsideBoundary(i + j)
          newClosestPair = List(pointsInsideBoundary(i), pointsInsideBoundary(i + j))
        }
      }
    }
    (newClosestPair, newMinDistance)
  }

  def closestPairParallel(input: List[Point]): (List[Point], Double) = {
    if (input.size == 2) {
      (input, input.head distanceTo input.last)
    } else {
      val (middleElement, leftPart, rightPart) = median(input)
      val ((leftClosestPoints, leftMinDistance),
           (rightClosestPoints, rightMinDistance)) = Parallel.parallel(closestPairParallel(leftPart), closestPairParallel(rightPart))
      if (leftMinDistance < rightMinDistance) boundaryMerge(leftClosestPoints, input, leftMinDistance, middleElement)
      else boundaryMerge(rightClosestPoints, input, rightMinDistance, middleElement)
    }
  }

  def closestPairSequential(input: List[Point]): (List[Point], Double) = {
    if (input.size < 2) (input, Double.MaxValue)
    else {
      val (middleElement, leftPart, rightPart) = median(input)
      val ((leftClosestPoints, leftMinDistance),
           (rightClosestPoints, rightMinDistance)) = (closestPairSequential(leftPart), closestPairSequential(rightPart))

      if (leftMinDistance < rightMinDistance) boundaryMerge(leftClosestPoints, input, leftMinDistance, middleElement)
      else boundaryMerge(rightClosestPoints, input, rightMinDistance, middleElement)
    }
  }

  def main(args: Array[String]): Unit = {
    val length = Math.pow(2, 10).toInt
    val points = (0 until length).map(x => new Point(x * Random.nextDouble, x * Random.nextDouble)).toList

    val (closestPointsPar, distancePar) = closestPairParallel(points)
    println(s"closest pair par: $closestPointsPar\n" +
            s"min distance par: $distancePar")

    val (closestPointsSeq, distanceSeq) = closestPairSequential(points)
    println(s"closest pair seq: $closestPointsSeq\n" +
            s"min distance seq: $distanceSeq")

    val standardConfig = config(
      Key.exec.minWarmupRuns -> 100,
      Key.exec.maxWarmupRuns -> 1000,
      Key.exec.benchRuns -> 100,
      Key.verbose -> true) withWarmer new Warmer.Default

    val seqtime = standardConfig measure {
      closestPairParallel(points)
    }

    val partime = standardConfig measure {
      closestPairSequential(points)
    }

    println(s"sequential time $seqtime ms")
    println(s"parallel time $partime ms")
    println(s"speedup: ${seqtime.value / partime.value}")

  }
}
