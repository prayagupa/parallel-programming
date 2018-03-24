import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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

  def syncTask = {
    Thread.sleep(20000);
    "synchronous hello"
  }
}

object ConcurrentTasks {

  def main(args: Array[String]): Unit = {

    Thread.sleep(10000)
    //sequentialTasks()

    println("starting task")
    Tasks.quickTask2()

    println("waiting for 30 secs")
    Thread.sleep(60000)
  }

  private def sequentialTasks() = {

    def pick(item: String) = Future {
      println(s"picking $item\n")
      (1 to 100).foreach(x => println("pick - " + item))
      Thread.sleep(5000)
      s"$item picked-up"
    }

    def pack(item: String) = Future {
      println(s"packing $item\n")
      (1 to 100).foreach(x => println("pack - " + item))
      Thread.sleep(5000)
      s"$item packed"
    }

    for {
      pick1 <- pick("item1")
      pick2 <- pick("item2")
      pack1 <- pack("###1")
      pack2 <- pack("###2")
    } yield {
      println("packing and packing done")
      pick1 + " - " + pick2
    }
  }

  def weight(): Future[Int] = {

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
