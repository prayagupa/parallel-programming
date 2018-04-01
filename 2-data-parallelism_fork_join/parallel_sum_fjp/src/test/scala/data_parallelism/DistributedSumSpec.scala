//package data_parallelism
//
//import org.scalatest.{FlatSpec, GivenWhenThen}
//
///**
// * Created by prayagupd
// * on 3/26/16.
// */
//
//class DistributedSumSpec extends FlatSpec with GivenWhenThen {
//
//  val api = new DistributionMachine
//
//  "Parallel sum" should "result sequential sum of array when elements are less than 1000" in {
//
//    Given("an array of 10 numbers")
//    val array = Array(1, 2, 3, 4, 5, 6, 7,8, 9, 10)
//
//    When("summed sequentially")
//    val starts = System.currentTimeMillis()
//    val sum = api.process(array)
//    val ends = System.currentTimeMillis()
//
//    Then("sum")
//    println(s"seq took ${ends - starts}ms") // ~9ms, 33ms
//    assert(sum == 55)
//  }
//
//  "Parallel sum" should "result sum of array when elements are greather than 1000" in {
//
//    Given("an array")
//    val array = 1 to 2000 toArray
//
//    When("summed with distributed nodes")
//    val starts = System.currentTimeMillis()
//    val sum = api.process(array)
//    val ends = System.currentTimeMillis()
//
//    Then("sum is equals to sum")
//    println(s"distributed took ${ends - starts}ms") // ~3ms, ~1ms, ~9ms
//    assert(sum == 2001000)
//  }
//}
