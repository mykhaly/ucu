import java.util.concurrent._


object Parallel {
  val forkJoinPool = new ForkJoinPool

  def task[T](computation: => T): RecursiveTask[T] = {
    val t = new RecursiveTask[T] {
      def compute: T = computation
    }

    Thread.currentThread match {
      case wt: ForkJoinWorkerThread =>
        t.fork() // schedule for execution
      case _ =>
        forkJoinPool.execute(t)
    }
    t
  }

  def parallel[A, B](taskA: => A, taskB: => B): (A, B) = {
    if (forkJoinPool.getActiveThreadCount < Runtime.getRuntime.availableProcessors()) {
      val right = task {
        taskB
      }
      val left = taskA

      (left, right.join())
    } else (taskA, taskB)
  }
}