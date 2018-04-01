

object SequentialTasksExecution {

  private val data: Iterable[Input] = Iterable(
    "data1",
    "data2",
    "data3",
    "data4",
    "data5",
    "data6",
    "data7",
    "data8",
    "data9",
    "data10"
  )

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
