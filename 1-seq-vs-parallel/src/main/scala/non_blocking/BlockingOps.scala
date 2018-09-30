package non_blocking

import scala.concurrent.{Await, ExecutionContext, Future}

object BlockingOps {

  def blockingIoOperation(id: Int)(implicit executionContext: ExecutionContext): Future[Int] = {
    println("requested: " + id + ". But not sure it will get a thread")

    Future {
      println(s"blocking operation:$id, name: ${Thread.currentThread().getName}")
      scala.concurrent.blocking {
        Thread.sleep(id * 5000)
      }
      id * id
    }
  }

  // https://stackoverflow.com/a/29069021/432903
  // with Thread.sleep(n)
  // taken: 45088 to calculate 86
  // and also the non-blocking does not get time

  //  log
  //  requested: 9. But not sure it will get a thread
  //  requested: 8. But not sure it will get a thread
  //  requested: 7. But not sure it will get a thread
  //  requested: 6. But not sure it will get a thread
  //  requested: 5. But not sure it will get a thread
  //  requested: 4. But not sure it will get a thread
  //  requested: 3. But not sure it will get a thread
  //  blocking operation:5, name: scala-execution-context-global-14
  //  blocking operation:8, name: scala-execution-context-global-12
  //  blocking operation:7, name: scala-execution-context-global-13
  //  blocking operation:6, name: scala-execution-context-global-15
  //  blocking operation:9, name: scala-execution-context-global-11
  //  requested: 2. But not sure it will get a thread
  //  blocking operation:3, name: scala-execution-context-global-17
  //  blocking operation:4, name: scala-execution-context-global-16
  //  blocking operation:2, name: scala-execution-context-global-18
  //  requested: 1. But not sure it will get a thread

  //  blocking operation:1, name: scala-execution-context-global-18
  //  non-blocking ops - [scala-execution-context-global-17]
  //  taken: 45092 to calculate 86

  def main(args: Array[String]): Unit = {

    val parallelism = Runtime.getRuntime.availableProcessors() //8

    println("parallelism: " + parallelism)
    //implicit val executionContext: ExecutionContextExecutor = ExecutionContext.fromExecutor(new ForkJoinPool(parallelism))

    //default conntext uses Runtime.getRuntime.availableProcessors()
    // https://docs.scala-lang.org/overviews/core/futures.html
    implicit val defaultExecutionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

    val start = System.currentTimeMillis()

    val operation9 = blockingIoOperation(9)(defaultExecutionContext)
    val operation8 = blockingIoOperation(8)(defaultExecutionContext)
    val operation7 = blockingIoOperation(7)(defaultExecutionContext)
    val operation6 = blockingIoOperation(6)(defaultExecutionContext)
    val operation5 = blockingIoOperation(5)(defaultExecutionContext)
    val operation4 = blockingIoOperation(4)(defaultExecutionContext)
    val operation3 = blockingIoOperation(3)(defaultExecutionContext)
    val operation2 = blockingIoOperation(2)(defaultExecutionContext)
    val operation1 = blockingIoOperation(1)(defaultExecutionContext)

    val nonBlocking = Future {
      println(s"non-blocking ops - [${Thread.currentThread().getName}]")
    }

    val res = operation9.flatMap { res3 =>
      operation2.flatMap { res2 =>
        operation1.map { res1 =>
          res3 + res2 + res1
        }(defaultExecutionContext)
      }(defaultExecutionContext)
    }(defaultExecutionContext)

    import scala.concurrent.duration._
    val results = Await.result(res, 60000 seconds)

    val taken = System.currentTimeMillis() - start

    println(s"taken: $taken to calculate " + results)
  }
}
