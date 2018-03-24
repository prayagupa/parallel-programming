
bulkheading and work sharing
----------------------------

The `actor.dispatcher` will pick an actor and assign it a dormant thread from it’s pool.

[Fork/Join framework](https://docs.oracle.com/javase/tutorial/essential/concurrency/forkjoin.html)
--------------------

The `fork/join` framework is an implementation of the `ExecutorService` interface that helps you
take advantage of multiple processors. 

- It is designed for work that can be broken into smaller pieces recursively. 
- The goal is to use all the available processing power to 
enhance the performance of your application.

[How is the fork/join framework better than a thread pool?](https://stackoverflow.com/a/7928815/432903)

https://zeroturnaround.com/rebellabs/fixedthreadpool-cachedthreadpool-or-forkjoinpool-picking-correct-java-executors-for-background-tasks/
