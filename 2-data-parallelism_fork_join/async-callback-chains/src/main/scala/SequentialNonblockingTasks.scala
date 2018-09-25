import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

case class Packed(items: Seq[String])

object SequentialNonblockingTasks {

  def main(args: Array[String]): Unit = {

    implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
    val result = sequentialTasks()

    Await.result(result, Duration.Inf)

  }

  private def sequentialTasks()(implicit executionContext: ExecutionContext): Future[Packed] = {

    for {
      _ <- pickTask("item1")
      _ <- pickTask("item2")

      _ <- packTask("item1")
      _ <- packTask("item2")
    } yield {
      println(s"[${Thread.currentThread().getName}]-packing and packing done")
      Packed(Seq("item1", "item2"))
    }
  }

  def pickTask(item: String)(implicit executionContext: ExecutionContext) = Future {
    println(s"[${Thread.currentThread().getName}]-picking $item\n")
    (1 to 100).foreach(x => "    pick - " + item)
    Thread.sleep(5000)
    s"$item picked-up"
  }

  def packTask(item: String)(implicit executionContext: ExecutionContext) = Future {
    println(s"[${Thread.currentThread().getName}]-packing $item\n")

    (1 to 100).foreach(i => "    pack - " + item)
    Thread.sleep(5000)
    s"$item packed"
  }

  def weight()(implicit executionContext: ExecutionContext): Future[Int] = {

    val data = for {
      x <- Future(100)
      y <- Future(200)
    } yield {

      for {
        a <- Future(x * 1)
        b <- Future(y * 2)
      } yield a * b
    }

    data.flatten

    data.flatten
  }
}
