import org.apache.spark.graphx.{GraphLoader, PartitionStrategy}
import org.apache.spark.{SparkConf, SparkContext}

object Main {
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) println("I need to have a file path to followers.txt as an argument")
    else {
      val followers_path = args(0)
      val conf = new SparkConf().setAppName("Count Triangles")
      val sc = new SparkContext(conf)
      val graph = GraphLoader.edgeListFile(sc, followers_path, true).partitionBy(PartitionStrategy.RandomVertexCut)
      val triangleCount = TrianglesInGraphCounter.execute(graph).vertices
      val triCounts = graph.triangleCount().vertices
      println(triCounts.values.sum())
      println(triCounts.map(_._2).sum())
      println(triCounts.map(_._2).reduce(_ + _))
      println(triangleCount.map(_._2).reduce(_ + _))
    }
  }
}
