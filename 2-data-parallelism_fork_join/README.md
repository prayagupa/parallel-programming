
bulkheading and work sharing
----------------------------

The `actor.dispatcher` will pick an actor and assign it a dormant thread from it’s pool
when there are events in the `Actor`'s mailbox.

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
