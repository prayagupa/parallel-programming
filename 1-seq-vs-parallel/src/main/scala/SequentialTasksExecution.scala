

object SequentialTasksExecution {

  private val data: Iterable[Input] = Range(1, 100).map(x => s"data-$x")

  def main(args: Array[String]): Unit = {

    val processed = data.map(processData)
    processed.foreach(println)
  }

  type Input = String
  type Output = String

  def processData: (Input => Output) = data => {
    Thread.sleep(1000)
    s"[Thread-${Thread.currentThread().getName}] data $data is processed."
  }
}
