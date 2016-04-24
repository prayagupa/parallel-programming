package data_parallelism;

import java.util.concurrent.RecursiveTask;

import static java.util.concurrent.ForkJoinPool.commonPool;

/**
 * Created by prayagupd
 * on 3/26/16.
 */

public class Api {
    static long process(int[] array) {
        return commonPool().invoke(new ParallelSum(array,0,array.length));
    }
}

class ParallelSum extends RecursiveTask<Long> {
    //2.1.2 Process Granularity
    static final int SEQUENTIAL_THRESHOLD = 500;

    int lowIndexState;
    int highIndexState;
    int[] sharedArray;

    ParallelSum(int[] arr, int lo, int hi) {
        sharedArray = arr;
        lowIndexState = lo;
        highIndexState = hi;
    }

    // How does this code work?

    // A Sum object is given an array and a range of that array.
    // The compute method sums the elements in that range.
    // If the range has fewer than SEQUENTIAL_THRESHOLD elements, it uses a simple for-loop like you
    // learned in introductory programming.
    @Override
    protected Long compute() {
        if(differenceOfHighToLow(highIndexState, lowIndexState) <= SEQUENTIAL_THRESHOLD) {
            //doItSequentially
            long sum = 0;
            for(int i= lowIndexState; i < highIndexState; ++i)
                sum += sharedArray[i];
            return sum;
        } else {
            // Otherwise, it creates two Sum objects for problems of half the size.
            // It uses fork to compute the left half in parallel with computing the right half,
            // which this object does itself by calling right.compute(). To get the answer for the left,
            // it calls left.join().
            int midIndex = lowIndexState + (highIndexState - lowIndexState) / 2;
            System.out.println("creating process for (" + lowIndexState + "-" + midIndex +"-" + highIndexState + ")");
            ParallelSum left  = new ParallelSum(sharedArray, lowIndexState, midIndex);
            ParallelSum right = new ParallelSum(sharedArray, midIndex, highIndexState);

            left.fork();

            long rightSum = right.compute();
            long leftSum  = left.join();
            return leftSum + rightSum;
        }
    }

    private int differenceOfHighToLow(int high, int low){
        return high - low;
    }
}
