package unbounded

import java.util.concurrent.{ExecutorService, Executors}

import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}

// https://stackoverflow.com/a/949463/432903
// Creates a thread pool that creates new threads as needed,
// but will reuse previously constructed threads when they are available,
// and uses the provided ThreadFactory to create new threads when needed.

object GrowableThreadPool {

  def main(args: Array[String]): Unit = {

    val executionContextPool: ExecutorService = Executors.newCachedThreadPool()
    implicit val exContext: ExecutionContextExecutor = ExecutionContext.fromExecutor(executionContextPool)

    (1 to 10000).foreach { x =>
      timedBlockingTask(x)
    }

    assert(1 != 1, "did not match")

    val newTask = timedBlockingTask(1001)
    import scala.concurrent.duration._
    Await.result(newTask, 1000 seconds)
  }

  def timedBlockingTask(id: Int)(implicit executionContext: ExecutionContext): Future[Int] = {
    Future {
      println(s"[${Thread.currentThread().getName}] - executing $id")
      Thread.sleep((id % 10) * 1000)
      println(s"[${Thread.currentThread().getName}] - $id completed")
      id * id
    }
  }
}
