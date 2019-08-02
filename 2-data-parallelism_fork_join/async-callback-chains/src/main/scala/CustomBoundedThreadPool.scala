import java.util.concurrent.LinkedBlockingQueue

import scala.concurrent.ExecutionContext

object CustomBoundedThreadPool {

  import java.util.concurrent.{ThreadPoolExecutor, TimeUnit}

  val executor = new ThreadPoolExecutor(5, 5, 120L, TimeUnit.SECONDS, new LinkedBlockingQueue[Runnable])
  implicit val contextShift: ExecutionContext = ExecutionContext.fromExecutor(executor)

  def main(args: Array[String]): Unit = {

  }

}
