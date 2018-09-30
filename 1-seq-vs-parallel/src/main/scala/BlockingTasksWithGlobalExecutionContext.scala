import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object BlockingTasksWithGlobalExecutionContext {

  private val data: Iterable[Input] = Range(1, 24).map(x => s"data-$x")

  implicit val threadPool: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  def main(args: Array[String]): Unit = {

    val f: Future[Unit] = Future.traverse(data) { d =>

      println(s"[${Thread.currentThread().getName}]-Firing $d")
      timedBlockingOps(d)

    } map { processed =>
      processed.foreach(p => println(s"""[${Thread.currentThread().getName}]-$p"""))
    }

    Await.result(f, Duration.Inf)
  }

  type Input = String
  type Output = String

  def timedBlockingOps: (Input => Future[Output]) = data => {
    Future {
      Thread.sleep(1000)
      s"[Thread-${Thread.currentThread().getName}] data $data is processed."
    }(threadPool)
  }
}
