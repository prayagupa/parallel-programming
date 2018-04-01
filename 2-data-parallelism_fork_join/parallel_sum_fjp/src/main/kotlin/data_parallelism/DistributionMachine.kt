package data_parallelism

import java.util.concurrent.RecursiveTask

import java.util.concurrent.ForkJoinPool.commonPool

/**
 * Created by prayagupd
 * on 3/26/16.
 */

class DistributionMachine {

    fun process(array: Array<Int>): Long {
        println("""[${Thread.currentThread().name}]""")
        return commonPool().invoke(DistributedSumTask(array, 0, array.size))
    }

}

internal class DistributedSumTask(var sharedArray: Array<Int>, var lowIndexState: Int, var highIndexState: Int) : RecursiveTask<Long>() {

    /**
     * How does this code work?
     *
     * A Sum object is given an array and a range of that array.
     * The compute method sums the elements in that range.
     * If the range has fewer than SEQUENTIAL_THRESHOLD elements, it uses a simple for-loop like you
     * learned in introductory programming.
     */
    override fun compute(): Long? {

        Thread.sleep(1000)

        //doItSequentially
        if (differenceOfHighToLow(highIndexState, lowIndexState) <= SEQUENTIAL_THRESHOLD) {
            var sum: Long = 0
            for (i in lowIndexState until highIndexState)
                sum += sharedArray[i].toLong()
            return sum
        } else {

            // Otherwise, it creates two Sum objects for problems of half the size.
            //
            // It uses fork to compute the left half in parallel with computing the right half,
            // which this object does itself by calling rightNode.compute().
            //
            // To get the answer for the left, it calls leftNode.join().
            //
            val midIndex = lowIndexState + (highIndexState - lowIndexState) / 2
            println("""[${Thread.currentThread().name}] creating new process for ([$lowIndexState-$midIndex] [$midIndex-$highIndexState])""")

            val leftNodeTask = DistributedSumTask(sharedArray, lowIndexState, midIndex)
            val rightNodeTask = DistributedSumTask(sharedArray, midIndex, highIndexState)

            leftNodeTask.fork() //Arranges to asynch'ly execute this task in the pool

            val rightSum = rightNodeTask.compute()!! //get the right result
            val leftSum = leftNodeTask.join() //get the left result

            return leftSum + rightSum
        }
    }

    private fun differenceOfHighToLow(high: Int, low: Int): Int {
        return high - low
    }

    companion object {
        //2.1.2 Process Granularity
        const val SEQUENTIAL_THRESHOLD = 100
    }
}
