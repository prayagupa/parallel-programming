import java.util.concurrent.{Executors, TimeUnit}

import io.AsyncTask
import org.scalatest.funsuite.AnyFunSuite  // FunSuite moved to funsuite.AnyFunSuite in scalatest 3.1+

import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

class AsyncTaskSpecs extends AnyFunSuite:

  val task = new AsyncTask

  val executionContext: ExecutionContextExecutorService =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(3))

  test("assert async value"):
    val response = task.timedBlockingOperation("nisarga")(executionContext)

    executionContext.shutdown()
    executionContext.awaitTermination(6000, TimeUnit.MILLISECONDS)

    response.foreach(s => assert(s == "nisarga-written"))(Implicits.global)
