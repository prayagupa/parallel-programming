package data_parallelism

import org.scalatest.FlatSpec

/**
 * Created by prayagupd
 * on 3/26/16.
 */

class ParalleSumSpec extends FlatSpec {
  "Parallel sum" should "result sequential sum of array when elements are less than 1000" in {
    val array = Array(1, 2, 3, 4, 5, 6, 7,8, 9, 10)
    assert(55 == Api.process(array))
  }

  "Parallel sum" should "result sum of array when elements are greather than 1000" in {
    val array = 1 to 2000 toArray

    assert(2001000 == Api.process(array))
  }
}
