import scala.reflect.ClassTag
import org.apache.spark.graphx._
import org.apache.spark.util.collection.OpenHashSet

object TrianglesInGraphCounter {

  def execute[VD: ClassTag, ED: ClassTag](graph: Graph[VD, ED]): Graph[Int, ED] = {
    val canonicalGraph = graph.mapEdges(_ => true).removeSelfEdges().convertToCanonicalEdges()
    val counters = runPreCanonicalized(canonicalGraph).vertices
    graph.outerJoinVertices(counters) { (_, _, optCounter: Option[Int]) =>
      optCounter.getOrElse(0)
    }
  }


  private def runPreCanonicalized[VD: ClassTag, ED: ClassTag](graph: Graph[VD, ED]): Graph[Int, ED] = {
    val nbrSets: VertexRDD[OpenHashSet[VertexId]] =
      graph.collectNeighborIds(EdgeDirection.Either).mapValues { (vid, nbrs) =>
        val set = new OpenHashSet[VertexId](nbrs.length)
        var i = 0
        while (i < nbrs.length) {
          if (nbrs(i) != vid) {
            set.add(nbrs(i))
          }
          i += 1
        }
        set
      }

    val setGraph: Graph[OpenHashSet[VertexId], ED] = graph.outerJoinVertices(nbrSets) {
      (_, _, optSet) => optSet.orNull
    }

    def edgeFunc(ctx: EdgeContext[OpenHashSet[VertexId], ED, Int]) {
      val (smallSet, largeSet) = if (ctx.srcAttr.size < ctx.dstAttr.size) {
        (ctx.srcAttr, ctx.dstAttr)
      } else {
        (ctx.dstAttr, ctx.srcAttr)
      }
      val iter = smallSet.iterator
      var counter: Int = 0
      while (iter.hasNext) {
        val vid = iter.next()
        if (vid != ctx.srcId && vid != ctx.dstId && largeSet.contains(vid)) {
          counter += 1
        }
      }
      ctx.sendToSrc(counter)
      ctx.sendToDst(counter)
    }

    val counters: VertexRDD[Int] = setGraph.aggregateMessages(edgeFunc, _ + _)
    graph.outerJoinVertices(counters) { (_, _, optCounter: Option[Int]) =>
      val dblCount = optCounter.getOrElse(0)
      require(dblCount % 2 == 0, "Triangle count resulted in an invalid number of triangles.")
      dblCount / 2
    }
  }
}