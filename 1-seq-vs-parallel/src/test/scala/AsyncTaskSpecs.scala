import java.util.concurrent.{Executors, TimeUnit}

import io.AsyncTask
import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

class AsyncTaskSpecs extends FunSuite {

  val task = new AsyncTask

  val executionContext: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(3))

  test("assert async value") {

    val response = task.timedBlockingOperation("led-zeppelin")(executionContext)

    executionContext.shutdown()
    executionContext.awaitTermination(6000, TimeUnit.MILLISECONDS)

    response.foreach(s => assert(s == "led-zeppelin-written"))(Implicits.global)

    //assert(response.value.get.get == "led-zeppelin-written")
  }

}
