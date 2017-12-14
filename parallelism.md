
https://dzone.com/articles/promises-and-futures-clojure

Promise
-------

Promise encapsulates a value which  might not be available yet and 
can be delivered exactly once, from any thread, later.

```
user=> (def shipment (promise))
#'user/shipment
user=> (deliver shipment "item-1")
#object[clojure.core$promise$reify__7005 0x7dd5d4c2 {:status :ready, :val "item-1"}]
user=> @shipment
"item-1"
user=> @shipment
"item-1"
```

Future
------

just like promises, futures can only be resolved once and 
dereferencing resolved future has immediate effect.

Future represents background computation, typically in a thread pool while 
promise is just a simple container that can be delivered (filled) by anyone at any point in time.

```clj
(def pickup-task 
  (future (Thread/sleep 10000) 
          (println "pickup item1") 
          "picked up item1"))

;;wait 10 seconds before dereferencing it you'll see "pickup item2"

;; When you dereference it you will block until the result is available.
;;user=> @pickup-task
;;pickup item1
;;"picked up item1"

;; Dereferencing again will return the already calculated value.
;; "picked up item1"
```

same task behavioue in scala

```scala
scala> import scala.concurrent.Future
import scala.concurrent.Future

scala> import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext.Implicits.global

scala> val data = Future { System.currentTimeMillis }
data: scala.concurrent.Future[Long] = Future(Success(1513236085742))

scala> data
res0: scala.concurrent.Future[Long] = Future(Success(1513236085742))

scala> data
res1: scala.concurrent.Future[Long] = Future(Success(1513236085742))
```

more example, 

```
(let [total-price (future (apply + (range 1e7)))]
                  (println "Processing started...")
                  (println "Done: " @total-price))
```

@total-price is blocking and we actually have to wait a little bit to see the "Done: " message 
and computation results. 

```scala
scala> import scala.util.Success
import scala.util.Success

scala> import scala.util.Failure
import scala.util.Failure

scala> val totalSum = Future { Thread.sleep(10000); (1 to 1000).sum }
totalSum: scala.concurrent.Future[Int] = Future(<not completed>)

scala> totalSum.onComplete{case Success(a) => println("Done: " + a) case Failure(f) => println("Failed: " + f)}

scala> Done: 500500
```

task error handling
--

```
user=> (def order-task (future (/ 1 0)))
#'user/order-task
user=> @order-task

ArithmeticException Divide by zero  clojure.lang.Numbers.divide (Numbers.java:158)
```
