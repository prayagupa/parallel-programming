package non_blocking

/**
  * jstack 68209 | grep thread
  * Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.151-b12 mixed mode):
  * "pool-4-thread-1" #14 prio=5 os_prio=31 tid=0x00007ff1b812a800 nid=0xa503 waiting on condition [0x000070000c305000]
  * "pool-3-thread-1" #13 prio=5 os_prio=31 tid=0x00007ff1b812a000 nid=0xa703 waiting on condition [0x000070000c202000]
  * "pool-2-thread-1" #12 prio=5 os_prio=31 tid=0x00007ff1b80f9000 nid=0xa903 waiting on condition [0x000070000c0ff000]
  * "GC task thread#0 (ParallelGC)" os_prio=31 tid=0x00007ff1b9802800 nid=0x1c07 runnable
  * "GC task thread#1 (ParallelGC)" os_prio=31 tid=0x00007ff1b9001000 nid=0x1d03 runnable
  * "GC task thread#2 (ParallelGC)" os_prio=31 tid=0x00007ff1ba804800 nid=0x2b03 runnable
  * "GC task thread#3 (ParallelGC)" os_prio=31 tid=0x00007ff1ba805000 nid=0x5303 runnable
  * "GC task thread#4 (ParallelGC)" os_prio=31 tid=0x00007ff1ba805800 nid=0x5103 runnable
  * "GC task thread#5 (ParallelGC)" os_prio=31 tid=0x00007ff1b8810800 nid=0x2c03 runnable
  * "GC task thread#6 (ParallelGC)" os_prio=31 tid=0x00007ff1b981e800 nid=0x4e03 runnable
  * "GC task thread#7 (ParallelGC)" os_prio=31 tid=0x00007ff1b981f000 nid=0x4c03 runnable
  */
object ThreadPools {

  def main(args: Array[String]): Unit = {

    import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

    val genericExecutorService = Executors.newCachedThreadPool()

    val scheduledExecutorService = Executors.newScheduledThreadPool(4)

    val scheduledExecutorService1 = Executors.newScheduledThreadPool(1)

    val scheduledExecutorService2 = Executors.newScheduledThreadPool(1)

    genericExecutorService.execute(() => {
      println("0")
    })

    scheduledExecutorService.execute(() => {
      println("1")
    })

    scheduledExecutorService1.execute(() => {
      println("2")
    })

    scheduledExecutorService2.execute(() => {
      println("3")
    })
  }

}
