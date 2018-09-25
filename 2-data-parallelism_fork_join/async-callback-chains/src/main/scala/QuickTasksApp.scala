import scala.concurrent.{ExecutionContext, Future}

object QuickTasksApp {

  def main(args: Array[String]): Unit = {

    implicit val ex: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

    Thread.sleep(10000)

    println("[${Thread.currentThread().getName}] - starting task")
    Tasks.quickBlockingTask2()

    println("waiting for 60 secs")
    Thread.sleep(60000)
  }

}

object Tasks {

  //note from https://docs.scala-lang.org/overviews/core/futures.html:
  //  [scala-execution-context-global-12] - executing computation
  //  [scala-execution-context-global-12] - finished computation 100

  // THE CALLBACK THREAD MIGHT NOT BE SAME AS THE COMPUTATION THREAD
  // We say that the callback is executed eventually.
  def quickBlockingTask2()(implicit executionContext: ExecutionContext): Future[Int] = {
    for {
      computedResult <- Future {
        Thread.sleep(2000)
        println(s"[${Thread.currentThread().getName}] - executing computation")
        100
      }
    } yield {
      println(s"[${Thread.currentThread().getName}] - finished computation " + computedResult)
      computedResult * 2
    }
  }

  def syncTask: String = {
    Thread.sleep(20000)
    "synchronous hello"
  }
}