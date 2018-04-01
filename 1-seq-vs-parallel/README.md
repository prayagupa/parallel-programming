
[2. Shared memory and distributed memory multiprocessor systems](https://edux.pjwstk.edu.pl/mat/264/lec/main119.html)

![](https://edux.pjwstk.edu.pl/mat/264/lec/ark16/Image8182.gif)


https://docs.scala-lang.org/overviews/core/futures.html#the-global-execution-context

Sequential
----------

- number of process = 10
- each process takes = 1 secs
- total time = 11 secs

```
$ sbt "runMain SequentialTasksExecution"
[info] Running SequentialTasksExecution
[Thread-run-main-0] data data1 is processed.
[Thread-run-main-0] data data2 is processed.
[Thread-run-main-0] data data3 is processed.
[Thread-run-main-0] data data4 is processed.
[Thread-run-main-0] data data5 is processed.
[Thread-run-main-0] data data6 is processed.
[Thread-run-main-0] data data7 is processed.
[Thread-run-main-0] data data8 is processed.
[Thread-run-main-0] data data9 is processed.
[Thread-run-main-0] data data10 is processed.
[success] Total time: 11 s, completed Apr 1, 2018 2:07:22 AM
```

![](sequential.png)

Parallel
--------

- number of process = 10
- each process takes = 1 secs
- total time = 3secs

```
$ sbt "runMain ParallelTasksWithGlobalExecutionContext"
[info] Running ParallelTasksWithGlobalExecutionContext
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

![](parallel.png)