
Parallelism
------------

- A parallel system is one which necessarily has the ability to execute multiple programs 
at the same time. 
- Usually, this capability is aided by hardware in the form of multicore processors 
on individual machines or as computing clusters where several machines are hooked up 
to solve independent pieces of a problem simultaneously.

Data Partitioning (based on available cores)
--------------------------------------------

- partitions are units of parallelism

[How does Kafka streaming handle concurrency? Is everything run in a single thread?](https://stackoverflow.com/a/39992430/432903)

[Kafka Streaming data partition](http://docs.confluent.io/current/streams/architecture.html#stream-partitions-and-tasks)

[Actor Model](http://doc.akka.io/docs/akka/snapshot/scala/general/actors.html) / Process
-------

[How does Actors work compared to threads?](https://stackoverflow.com/a/3587250/432903)

```
The actor model operates on message passing. 
Individual processes (actors) are allowed to send messages 
asynchronously to each other. 

What distinguishes this from what we normally think of as the 
threading model, is that there is (in theory at least) 
no shared state.

And if one believes that shared state is the root of all evil, 
then the actor model becomes very attractive.
```

[How, if at all, do Erlang Processes map to Kernel Threads?](https://stackoverflow.com/a/605631/432903)

[akka jvm threads vs os threads when performing io](https://stackoverflow.com/a/7458958/432903)

https://cr.openjdk.java.net/~rpressler/loom/Loom-Proposal.html

Amdahl's law
-------------

- Amdahl's law describes the theoretical speedup a program can achieve at best by 
using additional computing resources.

```
S(n) = 1 / ((1 - P) + P/n)

where, 
P = the fraction of the program that is parallelizable
n = cores or threads.
```

Parallel (parallel with blocking threads or parallel with non-blocking threads)
-----------------------------------------------------------------------------

useful references
- https://wiki.haskell.org/Parallelism
- https://typelevel.org/cats-effect/concurrency/basics.html

- By default, `future`s and `promise`s are non-blocking,
making use of callbacks instead of typical blocking operations.
- Java provides combinators such as `thenApply`, `thenAccept`, and `thenCompose` 
used to compose futures in a non-blocking way.

Where do I get thread from?
------------------------------

- [`ThreadExecutor`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executor.html) or `ExecutionContext` or [`Threadpool`](https://docs.rs/threadpool/1.7.1/threadpool/)
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

By default the `ForkJoinPool.commonPool()` sets the parallelism level of its
underlying `fork-join` pool to the amount of available processors

in scala `ForkJoinPool` can increase the amount of threads beyond its `parallelismLevel`
only in the presence of blocking computation.

Let’s assume that we want to use a order API of some retail company
to obtain a list of orders for a given user.

```scala
import scala.concurrent._
import ExecutionContext.Implicits.global

val session = orderApi.createSession("user-001@duwamish.com", "password")

val f: Future[List[Order]] = Future {
  session.getOrders()
}
```

Thread will be waiting till the response arrives from API,
which means CPU is not doing any work during that time, so can be
hired for uber driving(with another `thread`) in between.

**To better utilize the CPU until the response arrives**, we should not
block the rest of the program– this computation should be scheduled
asynchronously.

The `Future.apply` method does exactly that– it performs
the specified computation block concurrently, in this case sending a
request to the server (maybe HTTP) and waiting for a response.


Callbacks
---------

- from a performance point of view a better way to do it is in a
completely non-blocking way, by registering a callback on the future.

- This callback is called asynchronously once the future is completed.

The `thenAccept` combinator is used purely for side-effecting purposes.

- number of process = [10 to 16]/ 8 CPUs
- each process takes = 1 secs
- total time = ~3secs (8 CPUs can take 8 tasks + rest of 2 tasks will wait till 2 CPUs are free + 1sec)

on a machine with
- 8 cores,
- 4G heap size

```
$ sbt "runMain BlockingTasksWithGlobalExecutionContext"
[info] Running BlockingTasksWithGlobalExecutionContext
[run-main-0]-Firing data1
[run-main-0]-Firing data2
[run-main-0]-Firing data3
[run-main-0]-Firing data4
[run-main-0]-Firing data5
[run-main-0]-Firing data6
[run-main-0]-Firing data7
[run-main-0]-Firing data8
[run-main-0]-Firing data9
[run-main-0]-Firing data10
[scala-execution-context-global-62]-[Thread-scala-execution-context-global-61] data data1 is processed.
[scala-execution-context-global-62]-[Thread-scala-execution-context-global-62] data data2 is processed.
[scala-execution-context-global-62]-[Thread-scala-execution-context-global-63] data data3 is processed.
[scala-execution-context-global-62]-[Thread-scala-execution-context-global-64] data data4 is processed.
[scala-execution-context-global-62]-[Thread-scala-execution-context-global-66] data data5 is processed.
[scala-execution-context-global-62]-[Thread-scala-execution-context-global-67] data data6 is processed.
[scala-execution-context-global-62]-[Thread-scala-execution-context-global-65] data data7 is processed.
[scala-execution-context-global-62]-[Thread-scala-execution-context-global-68] data data8 is processed.
[scala-execution-context-global-62]-[Thread-scala-execution-context-global-61] data data9 is processed.
[scala-execution-context-global-62]-[Thread-scala-execution-context-global-62] data data10 is processed.
[success] Total time: 3 s, completed Apr 1, 2018 2:09:07 AM
```

for 100 tasks
time taken should be = (100/CPUs) + ~1sec = 96 threads in 12 batches + 4 in another batch + 1 secs

```
$ sbt "runMain BlockingTasksWithGlobalExecutionContext"
[info] Loading global plugins from /Users/a1353612/.sbt/0.13/plugins
[info] Loading project definition from /Users/a1353612/buybest/sc212/parallel-programming/1-seq-vs-parallel/project
[info] Set current project to seq-vs-parallel (in build file:/Users/a1353612/buybest/sc212/parallel-programming/1-seq-vs-parallel/)
[info] Running BlockingTasksWithGlobalExecutionContext
[run-main-0]-Firing data-1
[run-main-0]-Firing data-2
[run-main-0]-Firing data-3
[run-main-0]-Firing data-4
[run-main-0]-Firing data-5
[run-main-0]-Firing data-6
[run-main-0]-Firing data-7
[run-main-0]-Firing data-8
[run-main-0]-Firing data-9
[run-main-0]-Firing data-10
[run-main-0]-Firing data-11
[run-main-0]-Firing data-12
[run-main-0]-Firing data-13
[run-main-0]-Firing data-14
[run-main-0]-Firing data-15
[run-main-0]-Firing data-16
[run-main-0]-Firing data-17
[run-main-0]-Firing data-18
[run-main-0]-Firing data-19
[run-main-0]-Firing data-20
[run-main-0]-Firing data-21
[run-main-0]-Firing data-22
[run-main-0]-Firing data-23
[run-main-0]-Firing data-24
[run-main-0]-Firing data-25
[run-main-0]-Firing data-26
[run-main-0]-Firing data-27
[run-main-0]-Firing data-28
[run-main-0]-Firing data-29
[run-main-0]-Firing data-30
[run-main-0]-Firing data-31
[run-main-0]-Firing data-32
[run-main-0]-Firing data-33
[run-main-0]-Firing data-34
[run-main-0]-Firing data-35
[run-main-0]-Firing data-36
[run-main-0]-Firing data-37
[run-main-0]-Firing data-38
[run-main-0]-Firing data-39
[run-main-0]-Firing data-40
[run-main-0]-Firing data-41
[run-main-0]-Firing data-42
[run-main-0]-Firing data-43
[run-main-0]-Firing data-44
[run-main-0]-Firing data-45
[run-main-0]-Firing data-46
[run-main-0]-Firing data-47
[run-main-0]-Firing data-48
[run-main-0]-Firing data-49
[run-main-0]-Firing data-50
[run-main-0]-Firing data-51
[run-main-0]-Firing data-52
[run-main-0]-Firing data-53
[run-main-0]-Firing data-54
[run-main-0]-Firing data-55
[run-main-0]-Firing data-56
[run-main-0]-Firing data-57
[run-main-0]-Firing data-58
[run-main-0]-Firing data-59
[run-main-0]-Firing data-60
[run-main-0]-Firing data-61
[run-main-0]-Firing data-62
[run-main-0]-Firing data-63
[run-main-0]-Firing data-64
[run-main-0]-Firing data-65
[run-main-0]-Firing data-66
[run-main-0]-Firing data-67
[run-main-0]-Firing data-68
[run-main-0]-Firing data-69
[run-main-0]-Firing data-70
[run-main-0]-Firing data-71
[run-main-0]-Firing data-72
[run-main-0]-Firing data-73
[run-main-0]-Firing data-74
[run-main-0]-Firing data-75
[run-main-0]-Firing data-76
[run-main-0]-Firing data-77
[run-main-0]-Firing data-78
[run-main-0]-Firing data-79
[run-main-0]-Firing data-80
[run-main-0]-Firing data-81
[run-main-0]-Firing data-82
[run-main-0]-Firing data-83
[run-main-0]-Firing data-84
[run-main-0]-Firing data-85
[run-main-0]-Firing data-86
[run-main-0]-Firing data-87
[run-main-0]-Firing data-88
[run-main-0]-Firing data-89
[run-main-0]-Firing data-90
[run-main-0]-Firing data-91
[run-main-0]-Firing data-92
[run-main-0]-Firing data-93
[run-main-0]-Firing data-94
[run-main-0]-Firing data-95
[run-main-0]-Firing data-96
[run-main-0]-Firing data-97
[run-main-0]-Firing data-98
[run-main-0]-Firing data-99
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-70] data data-1 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-71] data data-2 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-72] data data-3 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-73] data data-4 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-74] data data-5 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-76] data data-6 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-75] data data-7 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-77] data data-8 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-70] data data-9 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-73] data data-10 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-77] data data-11 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-75] data data-12 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-72] data data-13 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-76] data data-14 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-74] data data-15 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-71] data data-16 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-70] data data-17 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-72] data data-18 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-77] data data-19 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-73] data data-20 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-76] data data-21 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-74] data data-22 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-71] data data-23 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-75] data data-24 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-70] data data-25 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-77] data data-26 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-72] data data-27 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-76] data data-28 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-74] data data-29 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-71] data data-30 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-73] data data-31 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-75] data data-32 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-74] data data-33 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-70] data data-34 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-76] data data-35 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-71] data data-36 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-73] data data-37 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-77] data data-38 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-72] data data-39 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-75] data data-40 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-71] data data-41 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-73] data data-42 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-70] data data-43 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-76] data data-44 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-74] data data-45 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-77] data data-46 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-72] data data-47 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-75] data data-48 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-73] data data-49 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-71] data data-50 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-74] data data-51 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-76] data data-52 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-77] data data-53 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-70] data data-54 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-75] data data-55 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-72] data data-56 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-76] data data-57 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-74] data data-58 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-71] data data-59 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-77] data data-60 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-73] data data-61 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-70] data data-62 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-75] data data-63 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-72] data data-64 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-70] data data-65 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-76] data data-66 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-77] data data-67 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-71] data data-68 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-74] data data-69 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-73] data data-70 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-72] data data-71 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-75] data data-72 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-77] data data-73 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-70] data data-74 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-74] data data-75 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-76] data data-76 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-73] data data-77 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-71] data data-78 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-72] data data-79 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-75] data data-80 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-70] data data-81 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-74] data data-82 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-73] data data-83 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-76] data data-84 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-71] data data-85 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-72] data data-86 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-75] data data-87 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-77] data data-88 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-71] data data-89 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-75] data data-90 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-70] data data-91 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-73] data data-92 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-72] data data-93 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-76] data data-94 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-74] data data-95 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-77] data data-96 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-73] data data-97 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-71] data data-98 is processed.
[scala-execution-context-global-73]-[Thread-scala-execution-context-global-75] data data-99 is processed.
[success] Total time: 14 s, completed Apr 1, 2018 2:27:18 AM
```

![](parallel.png)

bulkheading (of ship) and work sharing
----------------------------

The `actor.dispatcher` will pick an actor and assign it a dormant thread from it’s pool
when there are events in the `Actor`'s mailbox.

- https://docs.microsoft.com/en-us/azure/architecture/patterns/bulkhead

`thread-pool-executor`
------------------------

use `thread-pool-executor` if you want your thread pool to have dynamic nature.

[fork-join-executor](https://github.com/shekhargulati/52-technologies-in-2016/blob/master/41-akka-dispatcher/README.md#the-default-dispatcher)

`fork-join-executor` allows you to have a static thread pool configuration 
where number of threads will be between parallelism-min and parallelism-max bounds

```
default-dispatcher {
      type = "Dispatcher"

      executor = "fork-join-executor"

      fork-join-executor {
        parallelism-min = 8
        parallelism-factor = 3.0
        parallelism-max = 64
      }

      shutdown-timeout = 1s

      throughput = 5
}
```

[Fork/Join framework](https://docs.oracle.com/javase/tutorial/essential/concurrency/forkjoin.html)
--------------------

The `fork/join` framework is an implementation of the `ExecutorService` interface that helps you
take advantage of multiple processors. 

- It is designed for work that can be broken into smaller pieces recursively. 
- The goal is to use all the available processing power to 
enhance the performance of your application.

[How is the fork/join framework better than a thread pool?](https://stackoverflow.com/a/7928815/432903)

https://zeroturnaround.com/rebellabs/fixedthreadpool-cachedthreadpool-or-forkjoinpool-picking-correct-java-executors-for-background-tasks/
