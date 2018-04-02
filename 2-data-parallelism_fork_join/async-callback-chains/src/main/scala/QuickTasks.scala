import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

object QuickTasks {

  def main(args: Array[String]): Unit = {

    Thread.sleep(10000)

    println("starting task")
    Tasks.quickTask2()

    println("waiting for 30 secs")
    Thread.sleep(60000)
  }

}

object Tasks {

  def quickTask2(): Future[Int] = {
    for {
      x <- Future {
        Thread.sleep(2000)
        100
      }
    } yield {
      println("finished computation " + x)
      x
    }
  }

  def syncTask: String = {
    Thread.sleep(20000)
    "synchronous hello"
  }
}