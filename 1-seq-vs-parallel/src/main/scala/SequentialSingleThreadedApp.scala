

object SequentialSingleThreadedApp {

  private val data: Iterable[BlockingApi.Input] = Range(1, 100).map(x => s"data-$x")

  def main(args: Array[String]): Unit = {
    data.foreach(id => println(BlockingApi.blockingOperation(id)))
  }
}

object BlockingApi {

  type Input = String
  type Output = String

  def blockingOperation: (Input => Output) = data => {
    Thread.sleep(1000)
    s"[Thread-${Thread.currentThread().getName}] data $data is processed."
  }

}
