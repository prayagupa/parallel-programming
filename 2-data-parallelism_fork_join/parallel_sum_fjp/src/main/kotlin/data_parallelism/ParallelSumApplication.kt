package data_parallelism

object ParallelSumApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        val data = IntRange(0, 100)

        val data2 = (0..1000).toList().toTypedArray()

        val data3 = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)

        val sum = DistributionMachine().process(data2)

        println(sum)
    }

}
