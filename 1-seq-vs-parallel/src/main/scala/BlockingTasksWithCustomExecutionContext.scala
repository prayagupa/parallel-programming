import java.util.concurrent.Executors

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}
import scala.util.{Failure, Success}

object BlockingTasksWithCustomExecutionContext {

  private val data: Iterable[Input] = Range(1, 100).map(x => s"data-$x")

  implicit val singleThreadContext: ExecutionContextExecutorService =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(3))

  def main(args: Array[String]): Unit = {

    Future.traverse(data) { d =>

      println(s"[${Thread.currentThread().getName}]-Firing $d")
      timedOperation(d)

    } onComplete {
      case Success(processed) =>
        processed.foreach(p => println(s"""[${Thread.currentThread().getName}]-$p"""))
        singleThreadContext.shutdown()

      case Failure(f) =>
        f.printStackTrace()
        singleThreadContext.shutdown()
    }

  }

  type Input = String
  type Output = String

  def timedOperation: (Input => Future[Output]) = data => {
    Future {
      Thread.sleep(5000)
      s"[Thread-${Thread.currentThread().getName}] data $data is processed."
    }
  }
}
