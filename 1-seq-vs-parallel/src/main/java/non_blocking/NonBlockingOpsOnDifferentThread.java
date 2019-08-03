package non_blocking;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

/**
 * Do the operations on same thread with .thenApplyAsync
 * [ORIGINAL THREAD]: ForkJoinPool.commonPool-worker-3
 * [THREAD]: ForkJoinPool.commonPool-worker-5
 * [THREAD]: ForkJoinPool.commonPool-worker-5
 */
public class NonBlockingOpsOnDifferentThread {

    private static final ForkJoinPool EXECUTOR = ForkJoinPool.commonPool();

    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("[ORIGINAL THREAD]: " + Thread.currentThread().getName());
            return blockingOps1(2);
        }, EXECUTOR).thenApplyAsync($ -> {
            System.out.println("[THREAD]: " + Thread.currentThread().getName());
            return blockingOps2($);
        }, EXECUTOR).thenApplyAsync($ -> {
            System.out.println("[THREAD]: " + Thread.currentThread().getName());
            return blockingOps2($);
        }, EXECUTOR).join();
    }

    private static int blockingOps1(int i) {
        block(5 * 1000);
        return i * 2;
    }

    private static int blockingOps2(int i) {
        block(6 * 1000);
        return i * 3;
    }

    private static void block(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
