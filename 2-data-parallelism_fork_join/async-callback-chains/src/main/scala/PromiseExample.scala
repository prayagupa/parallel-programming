
import java.util.concurrent.ForkJoinPool

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

// https://docs.scala-lang.org/overviews/core/futures.html
// promises you a something in future
object PromiseExample {

  def main(args: Array[String]): Unit = {

    implicit val ex: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
    val anotherEx: ExecutionContext = scala.concurrent.ExecutionContext.fromExecutor(new ForkJoinPool(
      Runtime.getRuntime.availableProcessors()
    ))

    final case class Mango(size: Int)

    val promisedMango = Promise[Mango]()
    val completedMango = promisedMango.future

    val tree = Future {
      promisedMango success Mango(size = 10)
      println(s"[${Thread.currentThread().getName}] - fights wind")
      println(s"[${Thread.currentThread().getName}] - working on other mangoes")
      "tree done"
    }.map { res =>
      println(s"[${Thread.currentThread().getName}] - $res")
      res
    }(anotherEx) //uses separate thread than computation pool

    completedMango.onComplete {
      case Success(s) => println(s"[${Thread.currentThread().getName}] - $s")
      case Failure(f) => println(s"[${Thread.currentThread().getName}] - $f")
    }

    import scala.concurrent.duration._
    Await.ready(tree, 2 seconds)
  }
}
