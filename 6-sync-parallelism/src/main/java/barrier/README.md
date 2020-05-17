[Synchronous parallelism](https://goo.gl/mf05u4)
----------------------------------------------------------------------------------
```
In parallel computing, a barrier is a type of synchronization method. 

A barrier (for a group of threads/processes) means any thread/process must stop 
at this point and cannot proceed until all other threads/processes reach 
this barrier.
```

[Parallel Programming With Barrier Synchronization, JVM](http://blogs.sourceallies.com/2012/03/parallel-programming-with-barrier-synchronization/)

[Barrier Synchronization, Rice University](https://cs.anu.edu.au/courses/comp8320/lectures/aux/comp422-Lecture21-Barriers.pdf)

[Barrier Synchronization Pattern, University of Illinois at Urbana-Champaign](http://osl.cs.illinois.edu/media/papers/karmani-2009-barrier_synchronization_pattern.pdf)

[CS4961 Parallel Programming, University Of Utah](http://www.cs.utah.edu/~mhall/cs4961f11/CS4961-L12.pdf)


[JVM CyclicBarrier](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/CyclicBarrier.html)

[Scala: How is Barrier Synchronization implemented?](http://stackoverflow.com/a/5460822/432903)

[JVM concurrency: Countdown latch vs Cyclic barrier](http://stackoverflow.com/a/4168861/432903)

- A `CyclicBarrier` is useful for more complex co-ordination tasks. 
- An example of such a thing would be parallel computation - where multiple subtasks are involved in the computation - kind of like `MapReduce`.

[CountDownLatch vs. Semaphore](http://stackoverflow.com/a/184566/432903)

```scala
//all start simultaneously when the countown reached zero.

val carraceCountdown = new CountDownLatch(1)

for (i <- 0 until 10){
   val racecar = new Thread() {    
      def run()    {
         carraceCountdown.await() //all threads waiting
         println("Vroom!")
      }
   };
   racecar.start()
}
println("Go")
carraceCountdown.countDown()   //all threads start now!
```

Usage
-----

[Download manager](http://java-demos.blogspot.com/2013/10/using-cyclicbarrier-in-java-with-example.html)
