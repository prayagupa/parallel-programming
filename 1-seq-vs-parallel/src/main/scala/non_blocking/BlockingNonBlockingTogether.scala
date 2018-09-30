package non_blocking

import java.util.concurrent.{ForkJoinPool, ForkJoinWorkerThread}
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory

import non_blocking.BlockingOps.blockingIoOperation

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object BlockingNonBlockingTogether {

  val factoryName: String => ForkJoinWorkerThreadFactory = (workerName: String) => (pool: ForkJoinPool) => {
    val worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool)
    worker.setName(s"$workerName-${worker.getPoolIndex}/${pool.getPoolSize}/${pool.getQueuedSubmissionCount}/${pool.getActiveThreadCount}")
    worker
  }

  def nonBlockingIoOperation(id: Int)(implicit executionContext: ExecutionContext): Future[Int] = {

    Future {
      println(s"non-blocking operation:$id, name: ${Thread.currentThread().getName}")
      id * id
    }
  }

  def main(args: Array[String]): Unit = {

    val parallelism = Runtime.getRuntime.availableProcessors() //8

    println("parallelism: " + parallelism)

    //default conntext uses Runtime.getRuntime.availableProcessors()
    // https://docs.scala-lang.org/overviews/core/futures.html
    val blockingFJThreadPool: ExecutionContext = ExecutionContext.fromExecutor(
      new ForkJoinPool(parallelism,factoryName("blocking-thread"), null, false))

    //val nonBlockingFJThreadPool: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
    val nonBlockingFJThreadPool: ExecutionContext = ExecutionContext.fromExecutor(
      new ForkJoinPool(parallelism, factoryName("non-blocking-thread"), null, false))

    val blockingOperation9 = blockingIoOperation(9)(blockingFJThreadPool)
    val blockingOperation8 = blockingIoOperation(8)(blockingFJThreadPool)
    val blockingOperation7 = blockingIoOperation(7)(blockingFJThreadPool)
    val blockingOperation6 = blockingIoOperation(6)(blockingFJThreadPool)
    val blockingOperation5 = blockingIoOperation(5)(blockingFJThreadPool)
    val blockingOperation4 = blockingIoOperation(4)(blockingFJThreadPool)
    val blockingOperation3 = blockingIoOperation(3)(blockingFJThreadPool)
    val blockingOperation2 = blockingIoOperation(2)(blockingFJThreadPool)
    val blockingOperation1 = blockingIoOperation(1)(blockingFJThreadPool)

    val nonBlocking1 = nonBlockingIoOperation(1)(nonBlockingFJThreadPool)
    val nonBlocking2 = nonBlockingIoOperation(2)(nonBlockingFJThreadPool)
    val nonBlocking3 = nonBlockingIoOperation(3)(nonBlockingFJThreadPool)
    val nonBlocking4 = nonBlockingIoOperation(4)(nonBlockingFJThreadPool)
    val nonBlocking5 = nonBlockingIoOperation(5)(nonBlockingFJThreadPool)
    val nonBlocking6 = nonBlockingIoOperation(6)(nonBlockingFJThreadPool)
    val nonBlocking7 = nonBlockingIoOperation(7)(nonBlockingFJThreadPool)
    val nonBlocking8 = nonBlockingIoOperation(8)(nonBlockingFJThreadPool)
    val nonBlocking9 = nonBlockingIoOperation(9)(nonBlockingFJThreadPool)

    implicit val executionContext: ExecutionContext = nonBlockingFJThreadPool

    val res = for {
      r9 <- blockingOperation9
      r8 <- blockingOperation8
      r7 <- blockingOperation7
      r6 <- blockingOperation6
      r5 <- nonBlocking5
      r1 <- nonBlocking1
    } yield r9 + r5

    Await.result(res, Duration.Inf)
  }

}
