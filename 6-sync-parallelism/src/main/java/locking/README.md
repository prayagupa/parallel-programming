[Reentrant MutEx](https://en.wikipedia.org/wiki/Reentrant_mutex)/ `Recursive MutEx`
----------------------------------------------------------------------------------

```
Reentrant mutex (Recursive MutEx, Recursive lock) is particular type of MUTual EXclusion (mutex)
device that may be locked multiple times by the same process/thread, 
without causing a deadlock.
```

[MutEx vs Semaphore](http://stackoverflow.com/questions/4039899/when-should-we-use-mutex-and-when-should-we-use-semaphore)
----------------------

1. `MutEx`
--------

```
A MutEx is a mutual exclusion semaphore, a special variant of a semaphore that only allows
one locker at a time and whose ownership restrictions may be more stringent than a normal semaphore.

In other words, it's equivalent to a normal counting semaphore with a count of one and the requirement
that it can only be released by the same thread that locked it.
```

```
eg.

Imagine that there are some albums to sell (item inventory).
When any people buy the albums at the same time: each person is a thread to buy albums.

Obviously we need to use the MutEx to protect the albums because it is the shared resource.
```


[JVM Lock Objects](https://docs.oracle.com/javase/tutorial/essential/concurrency/newlocks.html)

```
Lock objects work very much like the implicit locks used by synchronized code. 
As with implicit locks, only one thread can own a Lock object at a time.
```

[JVM ReentrantLock](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/locks/ReentrantLock.html)

```
A reentrant Mutual Exclusion Lock with the same basic behavior and semantics
as the implicit monitor lock accessed using synchronized methods and statements,
but with extended capabilities.
```

[In JVM, Why use a ReentrantLock if one can use synchronized(x)?](http://stackoverflow.com/a/11821900/432903)

```
A ReEntrantLock is unstructured, unlike synchronized constructs -- 
i.e. you don't need to use a block structure for locking and can even hold a lock across methods
```

[ReentrantLock and the Dining Philosophers](https://dzone.com/articles/reentrantlock-and-dining-philo)

[Simplifying ReadWriteLock with Java 8 and lambdas](http://www.nurkiewicz.com/2014/03/simplifying-readwritelock-with-java-8.html)

[Java 8 StampedLocks vs. ReadWriteLocks and Synchronized](http://blog.takipi.com/java-8-stampedlocks-vs-readwritelocks-and-synchronized/)

2. Semaphore
------------

```
A semaphore, on the other hand, has a count and can be locked by that many lockers concurrently. 
And it may not have a requirement that it be released by the same thread that claimed it 
(but, if not, you have to carefully track who currently has responsibility for it, much like allocated memory).

So, if you have a number of instances of a resource (say three tape drives), you could use a semaphore 
with a count of 3. Note that this doesn't tell you which of those tape drives you have, 
just that you have a certain number.

Also with semaphores, it's possible for a single locker to lock multiple instances of a resource, 
such as for a tape-to-tape copy. If you have one resource (say a memory location that you don't want to corrupt), 
a mutex is more suitable.
```

https://blog.feabhas.com/2009/09/mutex-vs-semaphores-%E2%80%93-part-1-semaphores/

[Mutex vs Semaphore in summary](http://stackoverflow.com/a/40282/432903)
--------

```
MutEx: exclusive-member access to a resource

Semaphore: n-member access to a resource
```


[MongoDB net.maxIncomingConnections](https://docs.mongodb.com/manual/reference/configuration-options/#net.maxIncomingConnections)

[How should I set up mongodb cluster to handle 20K+ simultaneous](http://stackoverflow.com/a/7867693/432903)

[mongodb & max connections](http://stackoverflow.com/a/8439729/432903) - https://docs.mongodb.com/manual/reference/program/mongod/#bin.mongod

[mongodb 65,536 simultaneous incoming connecitons](https://docs.mongodb.com/manual/reference/configuration-options/#net.maxIncomingConnections)

[cassandra native_transport_max_concurrent_connections = -1 (infinity)](http://docs.datastax.com/en/cassandra/2.1/cassandra/configuration/configCassandra_yaml_r.html?scroll=reference_ds_qfg_n1r_1k__native_transport_max_threads)

[Java8 Semaphores](http://winterbe.com/posts/2015/04/30/java8-concurrency-tutorial-synchronized-locks-examples/)

```scala
val semaphore = new Semaphore(5)
val permit = semaphore.tryAcquire(1, TimeUnit.SECONDS)
semaphore.release()
```

[Java Concurrency Part 1 – Semaphores](http://www.obsidianscheduler.com/blog/java-concurrency-part-1-semaphores/)
