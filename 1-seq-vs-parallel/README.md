Process
--------

- A process is a program in execution. 
- A process is an execution environment that consists of instructions, user-data, 
and system-data segments, as well as lots of other resources such as CPU, memory, 
address-space, disk and network I/O acquired at runtime. 
- A program can have several copies of it running at the same time but 
a process necessarily belongs to only one program.

```bash
$ ps
  PID TTY           TIME CMD
 1674 ttys000    0:01.70 -bash
11378 ttys000    1:55.53 docker run -p 27017:27017 -it mongodb --net=host
34745 ttys001    8:41.88 /Library/Java/JavaVirtualMachines/jdk-12.0.1.jdk/Contents/Home/bin/java -agentlib:jdwp=transport=dt_sock
50600 ttys001    3:32.81 /usr/bin/jshell
50638 ttys002    1:05.57 /usr/bin/jconsole
34772 ttys003    0:00.06 /System/Library/Frameworks/Python.framework/Versions/2.7/Resources/Python.app/Contents/MacOS/Python
```

Thread
------

- Thread is the smallest unit of execution in a process. 
- A thread simply executes instructions serially. 
- A process can have multiple threads running as part of it. 
- Usually, there would be some state associated with the process that is shared among all the threads and 
in turn each thread would have some state private to itself. The globally shared state(heap) amongst the threads 
of a process is visible and accessible to all the threads, and special attention needs to be paid when any thread 
tries to read or write to this global shared state. 
- There are several constructs offered by various programming languages to guard and 
discipline the access to this global state.
- All thread in java are native Linux threads, a.k.a `pthreads`(POSIX)
- use of multithreading increases [throughput along with the increased responsiveness (latency).](https://docs.microsoft.com/en-us/dotnet/standard/threading/threads-and-threading)

Concurrency
-----------

- A concurrent program is one that can be decomposed into constituent parts and each part 
can be executed out of order or in partial order without affecting the final outcome. 
-  concurrency is a property of a program

[Thread scheduling](https://en.wikipedia.org/wiki/Thread_(computing)#Scheduling)
------------

OS schedules threads either `pre-emptively` or `co-operatively`. 

| Pre-emptive multithreading                                                      | Co-operative multithreading  |
|---------------------------------------------------------------------------------|----------------------------|
| it allows the OS to determine when a context switch should occur.|               relies on the threads themselves to relinquish control once they are at a stopping point.   |
| disadvantage of preemptive multithreading is that the system may make a context switch at an inappropriate time, causing lock convoy, priority inversion or other negative effects, which may be avoided by cooperative multithreading.| This can create problems if a thread is waiting for a resource to become available.  |
|                                                                  | is used with `await` in languages with a single-threaded event-loop in their runtime, like js or Python |

Normally use pre-emptive. 
[If you find your design has a lot of thread-switching overhead, cooperative threads would be a possible optimization.](https://stackoverflow.com/a/4147474/432903)

[Thread Execution Model](https://www.3dgep.com/cuda-thread-execution-model/)
---------------

- a set of C-function library call/ [posix_thread](https://en.wikipedia.org/wiki/POSIX_Threads)

    % [Static vs. dynamic scheduling](https://courses.cs.washington.edu/courses/cse471/02au/lectures/dyn1.pdf)
    
    % [Dynamic scheduling, scoreboarding](http://ece-research.unm.edu/jimp/611/slides/chap4_3.html)

    % [OpenMP Scheduling Loops](http://cs.umw.edu/~finlayson/class/fall14/cpsc425/notes/12-scheduling.html) - [CPSC 425: Parallel Computing, University of Mary Washington](http://cs.umw.edu/~finlayson/class/fall14/cpsc425/)
    
    % [Difference between static and dynamic schedule in openMP in C](http://stackoverflow.com/a/5864834/432903)
    
- scalac/javac concurrency library `java.util.concurrent`/ `scala.concurrent`

//Static vs Dynamic Loop scheduling in OpenMp

Static Loop Scheduling           | Dynamic Loop Scheduling | Guided Loop Scheduling 
----------------- | ------------------------- | --------------------------------------
|All iterations are allocated to threads -> before they execute any iterations | Some of the iterations allocated to threads -> at start of execution. Threads that complete their iterations are eligible to get additional work. | Large chunks initially assigned to Threads, Additional chunks of progressively smaller size assigned dynamically to Threads as needed
| has low overhead, but may have high load imbalance. | has higher overhead, but can reduce load imbalance. | 
| # pragma omp parallel for private(tid) schedule(static, ChunkSize) | # pragma omp parallel for private(tid) schedule(dynamic, ChunkSize) | schedule(guided)

[Thread states](https://docs.oracle.com/javase/7/docs/api/java/lang/Thread.State.html) JWN, 07-2016, SPLK 2019
--------------

| state        | description  |
|--------------| ------------ |
|NEW           | A thread has **not yet started**. |
|RUNNABLE      | A thread **executing in the Java virtual Machine**.     |
|              |                                                                      |
|BLOCKED       | A thread **blocked waiting** for a [monitor lock - syncd](https://stackoverflow.com/a/15680550/432903) |
|WAITING       | A thread **waiting indefinitely** for another thread to perform a particular action. |
|              | thread can go to `WAITING` state for three reasons `Object.wait`, `Thread.join`, `LockSupport.park` (causes `Parking`) |
|TIMED_WAITING | A thread **waiting for another thread** to perform an action for up to a specified waiting time. (`Thread.sleep(n)`)|
| TERMINATED   | A thread that has exited.|

![](RUNNABLE.png)
![](TIMED_WAITING.png)

```bash
                   RUNNABLE 
NEW ~~~~~~~~~~~~~> BLOCKED  ~~~~~~~~~~~~~~~~> TERMINATED
                   WAITING
                   TIMED_WAITING
```

[Thread Blocking](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/locks/LockSupport.html)
----

See https://stackoverflow.com/a/51788005/432903

| Action       | description  |
|--------------| ------------ |
| Park         | returns immediately if the permit is available, consuming it in the process; otherwise it may block | 
|              | park serves as an optimization of a "busy wait" that does not waste as much time spinning |
|              | `Parking` means suspending execution until permit is available. |
| Unpark       | unpark makes the permit available, if it was not already available | 


Java Memory Model
----------------
- set of rules according to which the compiler, the processor or 
the runtime is permitted to reorder memory operations. 

[Where is Thread Object created? Stack or JVM Heap Memory?](http://stackoverflow.com/a/19433994/432903) - I BBY, 2018

```java
jshell> var processor = new Thread() //new means allocated always in heap memory
processor ==> Thread[Thread-1,5,main]
```

starting a thread will create a [new stack for that thread](https://stackoverflow.com/a/19433542/432903)

![](https://i.stack.imgur.com/kKDL2.gif)

[Why do threads share the heap space?](http://stackoverflow.com/a/3321554/432903), [Quantcast](https://www.glassdoor.com/Interview/Quantcast-Interview-RVW2072600.htm)

```
Because otherwise they would be processes. 
That is the whole idea of threads, to share memory.
```

[How many threads can OS vs Java VM support?](https://stackoverflow.com/a/764096/432903)

**For OS,**

```bash
$ ulimit -a
core file size          (blocks, -c) 0
data seg size           (kbytes, -d) unlimited
file size               (blocks, -f) unlimited
max locked memory       (kbytes, -l) unlimited
max memory size         (kbytes, -m) unlimited
open files                      (-n) 256
pipe size            (512 bytes, -p) 1
stack size              (kbytes, -s) 8192
cpu time               (seconds, -t) unlimited
max user processes              (-u) 709
virtual memory          (kbytes, -v) unlimited`

# or
launchctl limit
	cpu         unlimited      unlimited      
	filesize    unlimited      unlimited      
	data        unlimited      unlimited      
	stack       8388608        67104768       
	core        0              unlimited      
	rss         unlimited      unlimited      
	memlock     unlimited      unlimited      
	maxproc     709            1064           
	maxfiles    256            unlimited  
```

```bash
$ ulimit -s
8192
```

ie. each of threads will get [8192K amount of memory (8MB)](https://stackoverflow.com/a/9211891/432903) assigned for it's stack.

**For JVM,**

Default Oracle 64 bit JVM has 1M stack size per thread which means,
```bash
1 GB RAM = 1024MB/ 1MB
         = 1024 threads
```

To raise the number of concurrent threads you should 
lower the default [StackSize(ss)](https://dzone.com/articles/java-what-limit-number-threads) `java -Xss 64k`

[How to catch an Exception from a thread (in JVM)](http://stackoverflow.com/questions/6546193/how-to-catch-an-exception-from-a-thread), JWN 2016

http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.UncaughtExceptionHandler.html

```java
var shipOrders = new Thread() {
    public void run() {
        
        System.out.println("Shipping ...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Interrupted.");
        }

        System.out.println("Shipping exception ...");
        throw new RuntimeException("Items missing in the package.");
    }
};

shipOrders.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                                           void uncaughtException(th: Thread, th: Throwable) {
                                                System.out.println("Error shipping orders : " + th)
                                           }
                                       });
shipOrders.start();
```

[sleep vs wait](http://stackoverflow.com/q/1036754/432903) JWN 07-2016

`.sleep` | `.notify`(.publish)/ `.wait`(.subscribe)/ Observer pattern in OO
-------|----------------
`sleep()` sends the Thread to sleep as it was before, it just packs the context and stops executing for a predefined time. | `wait()`, on the contrary, is a thread (or message) synchronization mechanism that allows you to notify a Thread of which you have no stored reference (nor care). 
So in order to wake it up before the due time, you need to know the Thread reference. This is NOT a common situation in a multi-threaded environment. It's mostly used for time-synchronization (e.g. wake in exactly 3.5 seconds) and/or hard-coded fairness (just sleep for a while and let others threads work). | You can think of it as a publish-subscribe pattern (wait == subscribe and notify() == publish). Basically using notify() you are sending a message (that might even not be received at all and normally you don't care).
To sum up, you normally use `sleep()` for time-syncronization | and `wait()` for multi-thread-synchronization.
In `sleep()` the thread stops working for the specified duration. | In `wait()` the thread stops working until the object being waited-on is notified, generally by other threads.

[`Runnable.run`(method call) vs `Thread.start`(start a thread)](http://stackoverflow.com/a/8579702/432903)
--------------

```
Runnable.run() is executed on the calling thread, 
just like any other method call. 

Thread.start() is required to actually create a new thread 
so that the runnable's run method is executed in parallel
```

User vs Daemon threads
----------------

- User/Non-Daemon threads are like front performers. 
- [Daemon threads are like assistants.](https://stackoverflow.com/a/2213348/432903)

Assistants help performers to complete a job. When the job is completed, no help is needed 
by performers to perform anymore. 

As no help is needed the assistants leave the place. 
So when the jobs of User/Non-Daemon threads is over, Daemon threads march away.

- An example for user/non-daemon thread is the thread running the `main`.
- Threads created by a user thread are user thread. 
- When all of the user/non-daemon threads complete, daemon threads terminates automatically.

[Shutting down threads cleanly](http://www.javaspecialists.eu/archive/Issue056.html)

[Why Are `Thread.stop`, `Thread.suspend`, `Thread.resume` and `Runtime.runFinalizersOnExit` Deprecated?](http://docs.oracle.com/javase/1.5.0/docs/guide/misc/threadPrimitiveDeprecation.html)

[src/main/java/blocking/GracefulInterruptionExample.java](src/main/java/blocking/GracefulInterruptionExample.java)

[Hyper-threading technology](https://en.wikipedia.org/wiki/Hyper-threading)
----------------

```
Architecturally, a processor with Hyper-Threading Technology consists of two logical processors per core, 
each of which has its own processor architectural state. 

Each logical processor can be individually halted, interrupted or directed to execute a specified thread, 
independently from the other logical processor sharing the same physical core.

$ sysctl -a | grep hw.
hw.ncpu: 8
hw.byteorder: 1234
hw.memsize: 17179869184
hw.activecpu: 8
hw.targettype: 
hw.physicalcpu: 4
hw.physicalcpu_max: 4
hw.logicalcpu: 8
hw.logicalcpu_max: 8
```

[Clock rate](https://en.wikipedia.org/wiki/Clock_rate)

```
The clock rate typically refers to the frequency at which a chip 
like a central processing unit (CPU),one core of a multi-core processor, 
is running and is used as an indicator of the processor's speed.

It is measured in clock cycles per second or its equivalent, 
the SI unit hertz (Hz)

sysctl -n machdep.cpu.brand_string
Intel(R) Core(TM) i7-4770HQ CPU @ 2.20GHz
```

[Introduction to Parallel Computing, Blaise Barney, Lawrence Livermore National Laboratory](https://computing.llnl.gov/tutorials/parallel_comp/)

```

Load Balancing

Load balancing refers to the practice of distributing approximately 
equal amounts of work among tasks so that all tasks are kept busy 
all of the time.

It can be considered a minimization of task idle time.

Load balancing is important to parallel programs for performance 
reasons. 
For example, if all tasks are subject to a barrier synchronization 
point, the slowest task will determine the overall performance.
```

[MultiThreading](https://goo.gl/CCb2wa), HUM 07-2016, [SPLK 2018](https://www.glassdoor.com/Interview/Splunk-Interview-RVW23849616.htm), [MS](https://www.glassdoor.com/Interview/Microsoft-Interview-RVW21394032.htm)
--------------------------------------------------------


[What is a multithreaded application, stackoverflow](http://stackoverflow.com/a/1313122/432903)

```
a single process can have many different "functions" executing concurrently, 
allowing the app to better use the available hardware 
(multiple cores/processors). 

Threads can communicate between them (they have shared 
memory), but its a hard problem to have every thread behave well 
with others when accesing shared objects/memory.
```

[1.3. Multithreading and Thread Synchronization](http://www.nakov.com/inetjava/lectures/part-1-sockets/InetJava-1.3-Multithreading.html)

```
multithreading is the ability of a CPU or a single core in a 
multi-core processor to execute multiple processes 
or threads concurrently, appropriately supported by the 
operating system.
```

[Thread Dead lock](https://en.wikipedia.org/wiki/Deadlock)
---------------------------------------------------

```
In concurrent computing, a deadlock occurs when two 
competing actions wait for the other to finish, 
and thus neither ever does.
```


[Deadlock example](http://stackoverflow.com/a/34520/432903), [SPLK, 2014](https://www.glassdoor.com/Interview/Splunk-Senior-Software-Engineer-Interview-Questions-EI_IE117313.0,6_KO7,31.htm#InterviewReview_3919244)

```
Process1     ---> locks table1
Process1 & 2 ---> want to process table2
Process2     ---> wins lock on table2     <--- Process1 is waiting
             ---> wants to process table1 <---- locked by Process1
             ---> waits in table2
```

- when a `Thread` never gets CPU time or access to shared resources

[CPU intensive vs IO intensive?](https://stackoverflow.com/a/868577/432903), AMZN, 2018
--------------------------------

CPU bound

```
A program is CPU bound if it would go faster if the CPU were faster, i.e. 
it spends the majority of its time simply using the CPU (doing calculations). 

eg. A program that computes new digits of π will typically be CPU-bound, it's just crunching numbers, 
image processing, matrix multiplication

* CPU bound processes spend more time doing computations, few very long CPU bursts.

* CPU burst is when the process is being executed in the CPU

https://www2.cs.uic.edu/~jbell/CourseNotes/OperatingSystems/6_CPU_Scheduling.html
```

[How to check if an API is CPU-bound?](https://stackoverflow.com/q/3156334/432903)

```
Just run the application for some time on a dedicated machine and check the CPU counters. 

If the app uses 100% of the CPU core it can access, it's CPU bound. 
Otherwise, it spends time on other things like memory allocations and IOs.
```

IO bound

```
A program is I/O bound if it would go faster if the I/O subsystem was faster. 
Which exact I/O system is meant can vary; I typically associate it with disk. 

A program that looks through a huge file(eg. medical records file) for some data will often be I/O bound, 
since the bottleneck is then the reading of the data from disk.

* IO bound processes spend more time doing IO than computations, have many short CPU bursts.
```

- For I/O bound programs, it makes sense to have a thread give up CPU control 
if it is waiting for an I/O operation to complete so that another thread can 
get scheduled on the CPU and utilize CPU cycles.

Thread per request vs [EventLoop](http://berb.github.io/diploma-thesis/original/055_events.html) vs [Event based Actor](https://stackoverflow.com/a/7458958/432903)
----------------------------------

[Why is Node.js single threaded?](http://stackoverflow.com/a/17959746/432903), [What the heck is EventLoop? - The JavaScript Event Loop: Explained](http://blog.carbonfive.com/2013/10/27/the-javascript-event-loop-explained/) sharethis, 2015

https://developer.mozilla.org/en-US/docs/Web/JavaScript/EventLoop

https://en.wikipedia.org/wiki/Event_loop

```
The event loop, message dispatcher, message loop, message pump, or run loop 
is a programming construct that waits for and dispatches events or messages in a program.
```

![](http://blog.carbonfive.com/wp-content/uploads/2013/10/event-loop.png)

| Thread per request                                                                                                                                                 |   EL                                               |
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| The issue with the "one thread per request" model for a server is that they don't scale well for several scenarios compared to the event loop thread model.        | In the Event Loop model, the loop thread selects the next event (I/O finished) to handle. So the thread is always busy (if you program it correctly of course).|
| Typically, in I/O intensive scenarios the requests spend most of the time waiting for I/O to complete.                                                             | The event loop model as all new things seems shiny and the solution for all issues but which model to use will depend on the scenario you need to tackle. If you have an intensive I/O scenario (like a proxy), the event base model will rule, whereas a CPU intensive scenario with a low number of concurrent processes will work best with the thread-based model.|
| During this time, in the "one thread per request" model, the resources linked to the thread (such as memory) are unused and memory is the limiting factor.         | | 
|                          | |
|                            | |

```
In the real world most of the scenarios will be a bit in the middle. 
You will need to balance the real need for scalability with the development 
complexity to find the correct architecture 
(e.g. have an event base front-end that 
delegates to the backend for the CPU intensive tasks. 
The front end will use little resources waiting for the task 
result.)
As with any distributed system it requires some effort to make it 
work.

If you are looking for the silver bullet that will fit with any 
scenario without any effort, you will end up with a bullet in your foot.
```

[In Java, threading is supported at the language level with the synchronized and volatile keywords.](http://stackoverflow.com/a/3306752/432903)

Parallelism
------------

see [../2-data-parallelism_fork_join/README.md](../2-data-parallelism_fork_join/README.md)

[2. Shared memory and distributed memory multiprocessor systems](https://edux.pjwstk.edu.pl/mat/264/lec/main119.html)
--

![](https://edux.pjwstk.edu.pl/mat/264/lec/ark16/Image8182.gif)


https://docs.scala-lang.org/overviews/core/futures.html#the-global-execution-context

- compile first to avoid CPU usage during compilation

Sequential/ [Synchronous](https://en.wikipedia.org/wiki/Synchronous_programming_language)
----------

- Synchronous execution refers to line-by-line execution of code.
- All the operations are handled by single thread (see in jmc or jvisualvm)

```
Thread Name	Thread State	Blocked Count	Blocked Time	Waited Count	Waited Time	Total CPU Usage(%)	Deadlocked	Lock Name	Lock Owner ID	Lock Owner Name	Thread Id	Native	Suspended	Allocated Bytes(bytes)
run-main-0	TIMED_WAITING	3		66		-45,623,600	Not Enabled				60	0	0	-256,236
```

- number of process = 10
- each process takes = 1 secs
- total time = 11 secs

```bash
$ gradle run blocking.SequentialSingleThreadedApp
[Thread-main] data data1 is processed.
[Thread-main] data data2 is processed.
[Thread-main] data data3 is processed.
[Thread-main] data data4 is processed.
[Thread-main] data data5 is processed.
[Thread-main] data data6 is processed.
[Thread-main] data data7 is processed.
[Thread-main] data data8 is processed.
[Thread-main] data data9 is processed.
[Thread-main] data data10 is processed.

numberOfProcesses=10
timeTakenMills=10074 ms
```

for 100 tasks,

```
$ sbt "runMain SequentialSingleThreadedApp"
[info] Running SequentialSingleThreadedApp
[Thread-run-main-0] data data-1 is processed.
[Thread-run-main-0] data data-2 is processed.
[Thread-run-main-0] data data-3 is processed.
[Thread-run-main-0] data data-4 is processed.
[Thread-run-main-0] data data-5 is processed.
[Thread-run-main-0] data data-6 is processed.
[Thread-run-main-0] data data-7 is processed.
[Thread-run-main-0] data data-8 is processed.
[Thread-run-main-0] data data-9 is processed.
[Thread-run-main-0] data data-10 is processed.
[Thread-run-main-0] data data-11 is processed.
[Thread-run-main-0] data data-12 is processed.
[Thread-run-main-0] data data-13 is processed.
[Thread-run-main-0] data data-14 is processed.
[Thread-run-main-0] data data-15 is processed.
[Thread-run-main-0] data data-16 is processed.
[Thread-run-main-0] data data-17 is processed.
[Thread-run-main-0] data data-18 is processed.
[Thread-run-main-0] data data-19 is processed.
[Thread-run-main-0] data data-20 is processed.
[Thread-run-main-0] data data-21 is processed.
[Thread-run-main-0] data data-22 is processed.
[Thread-run-main-0] data data-23 is processed.
[Thread-run-main-0] data data-24 is processed.
[Thread-run-main-0] data data-25 is processed.
[Thread-run-main-0] data data-26 is processed.
[Thread-run-main-0] data data-27 is processed.
[Thread-run-main-0] data data-28 is processed.
[Thread-run-main-0] data data-29 is processed.
[Thread-run-main-0] data data-30 is processed.
[Thread-run-main-0] data data-31 is processed.
[Thread-run-main-0] data data-32 is processed.
[Thread-run-main-0] data data-33 is processed.
[Thread-run-main-0] data data-34 is processed.
[Thread-run-main-0] data data-35 is processed.
[Thread-run-main-0] data data-36 is processed.
[Thread-run-main-0] data data-37 is processed.
[Thread-run-main-0] data data-38 is processed.
[Thread-run-main-0] data data-39 is processed.
[Thread-run-main-0] data data-40 is processed.
[Thread-run-main-0] data data-41 is processed.
[Thread-run-main-0] data data-42 is processed.
[Thread-run-main-0] data data-43 is processed.
[Thread-run-main-0] data data-44 is processed.
[Thread-run-main-0] data data-45 is processed.
[Thread-run-main-0] data data-46 is processed.
[Thread-run-main-0] data data-47 is processed.
[Thread-run-main-0] data data-48 is processed.
[Thread-run-main-0] data data-49 is processed.
[Thread-run-main-0] data data-50 is processed.
[Thread-run-main-0] data data-51 is processed.
[Thread-run-main-0] data data-52 is processed.
[Thread-run-main-0] data data-53 is processed.
[Thread-run-main-0] data data-54 is processed.
[Thread-run-main-0] data data-55 is processed.
[Thread-run-main-0] data data-56 is processed.
[Thread-run-main-0] data data-57 is processed.
[Thread-run-main-0] data data-58 is processed.
[Thread-run-main-0] data data-59 is processed.
[Thread-run-main-0] data data-60 is processed.
[Thread-run-main-0] data data-61 is processed.
[Thread-run-main-0] data data-62 is processed.
[Thread-run-main-0] data data-63 is processed.
[Thread-run-main-0] data data-64 is processed.
[Thread-run-main-0] data data-65 is processed.
[Thread-run-main-0] data data-66 is processed.
[Thread-run-main-0] data data-67 is processed.
[Thread-run-main-0] data data-68 is processed.
[Thread-run-main-0] data data-69 is processed.
[Thread-run-main-0] data data-70 is processed.
[Thread-run-main-0] data data-71 is processed.
[Thread-run-main-0] data data-72 is processed.
[Thread-run-main-0] data data-73 is processed.
[Thread-run-main-0] data data-74 is processed.
[Thread-run-main-0] data data-75 is processed.
[Thread-run-main-0] data data-76 is processed.
[Thread-run-main-0] data data-77 is processed.
[Thread-run-main-0] data data-78 is processed.
[Thread-run-main-0] data data-79 is processed.
[Thread-run-main-0] data data-80 is processed.
[Thread-run-main-0] data data-81 is processed.
[Thread-run-main-0] data data-82 is processed.
[Thread-run-main-0] data data-83 is processed.
[Thread-run-main-0] data data-84 is processed.
[Thread-run-main-0] data data-85 is processed.
[Thread-run-main-0] data data-86 is processed.
[Thread-run-main-0] data data-87 is processed.
[Thread-run-main-0] data data-88 is processed.
[Thread-run-main-0] data data-89 is processed.
[Thread-run-main-0] data data-90 is processed.
[Thread-run-main-0] data data-91 is processed.
[Thread-run-main-0] data data-92 is processed.
[Thread-run-main-0] data data-93 is processed.
[Thread-run-main-0] data data-94 is processed.
[Thread-run-main-0] data data-95 is processed.
[Thread-run-main-0] data data-96 is processed.
[Thread-run-main-0] data data-97 is processed.
[Thread-run-main-0] data data-98 is processed.
[Thread-run-main-0] data data-99 is processed.
[success] Total time: 100 s, completed Apr 1, 2018 2:23:18 AM
```

![](sequential.png)

Parallelism
-----------------------------------------------------------------------------

[](../2-data-parallelism_fork_join/README.md)
