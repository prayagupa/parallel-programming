import java.util.UUID
import java.util.concurrent.{ForkJoinPool, ForkJoinTask, RecursiveAction, TimeUnit}

import scala.collection.JavaConverters

/**
  * submit 100 tasks to a pool of threads 100
  *
  * distribute the shipping of orders
  */
object WorkStealingOrderShippingTask {

  private val forkJoinPool = new ForkJoinPool(100)

  def main(args: Array[String]): Unit = {

    val orders = List.fill(100)(OrderReceived(UUID.randomUUID().toString))

    val task = new WorkStealingOrderShippingTask(orders)

    forkJoinPool.execute(task)

    forkJoinPool.shutdown()
    println(s"#[${Thread.currentThread().getName}] - shutting down executor pool")
    forkJoinPool.awaitTermination(Long.MaxValue, TimeUnit.NANOSECONDS)
    println(s"#[${Thread.currentThread().getName}] - executor pool is shutdown")
  }

}

case class OrderReceived(id: String)

case class OrderShipping(id: String)

/**
  * creates two sub tasks of work and
  * computes them
  */
class WorkStealingOrderShippingTask(orders: Seq[OrderReceived]) extends RecursiveAction {

  private val NumberOfThreshold = 5

  override def compute(): Unit = {
    if (orders.size > NumberOfThreshold) {

      val mid = orders.length / 2
      println(s"""#[${Thread.currentThread().getName}] - creating sub-tasks for [0-$mid] and [$mid-${orders.length}]""")

      val task1 = new WorkStealingOrderShippingTask(orders.slice(0, mid))
      val task2 = new WorkStealingOrderShippingTask(orders.slice(mid, orders.length))

      ForkJoinTask.invokeAll(JavaConverters.asJavaCollection(List(task1, task2)))

    } else {
      shipOrders(orders)
    }
  }

  private def shipOrders(receiveds: Seq[OrderReceived]): Unit = {
    orders.foreach(o => {
      Thread.sleep(1000)
      println(s"""    [${Thread.currentThread().getName}] - shipping ${o.id}""")
    })
  }
}
