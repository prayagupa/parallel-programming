package vthreads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks platform threads vs virtual threads across three scenarios:
 *
 * 1. Equal pool size  — pool sized to task count (baseline)
 * 2. Constrained pool — platform pool much smaller than task count (real-world)
 * 3. Massive load     — 100,000 tasks to stress memory and scheduling
 */
public class PthreadVthreadComparison {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Platform Threads vs Virtual Threads Benchmark ===\n");

        equalPoolSize();
        constrainedPool();
        massiveLoad();
    }

    // -------------------------------------------------------------------------
    // 1. Equal pool: platform pool sized to task count — shows baseline parity
    // -------------------------------------------------------------------------
    static void equalPoolSize() throws InterruptedException {
        System.out.println("-- Scenario 1: Equal Pool Size (100 tasks, 500 ms blocking each) --");
        int taskCount = 100;
        long blockingMs = 500;

        long platformMs = runPlatformThreads(taskCount, taskCount, blockingMs);
        long virtualMs  = runVirtualThreads(taskCount, blockingMs);

        printResult("Equal pool", taskCount, platformMs, virtualMs);
    }

    // -------------------------------------------------------------------------
    // 2. Constrained pool: platform pool << task count — virtual threads win big
    // -------------------------------------------------------------------------
    static void constrainedPool() throws InterruptedException {
        System.out.println("-- Scenario 2: Constrained Platform Pool (1,000 tasks, 200 ms blocking, pool=20) --");
        int taskCount  = 1_000;
        int poolSize   = 20;
        long blockingMs = 200;

        long platformMs = runPlatformThreads(taskCount, poolSize, blockingMs);
        long virtualMs  = runVirtualThreads(taskCount, blockingMs);

        printResult("Constrained pool", taskCount, platformMs, virtualMs);
    }

    // -------------------------------------------------------------------------
    // 3. Massive load: 100k tasks — shows memory & scheduling advantage
    // -------------------------------------------------------------------------
    static void massiveLoad() throws InterruptedException {
        System.out.println("-- Scenario 3: Massive Load (100,000 tasks, 50 ms blocking, pool=200) --");
        int taskCount  = 100_000;
        int poolSize   = 200;
        long blockingMs = 50;

        long platformMs = runPlatformThreads(taskCount, poolSize, blockingMs);
        long virtualMs  = runVirtualThreads(taskCount, blockingMs);

        printResult("Massive load", taskCount, platformMs, virtualMs);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    static long runPlatformThreads(int taskCount, int poolSize, long blockingMs)
            throws InterruptedException {
        long start = System.currentTimeMillis();
        try (ExecutorService pool = Executors.newFixedThreadPool(poolSize)) {
            for (int i = 0; i < taskCount; i++) {
                pool.submit(() -> sleep(blockingMs));
            }
        }
        return System.currentTimeMillis() - start;
    }

    static long runVirtualThreads(int taskCount, long blockingMs)
            throws InterruptedException {
        long start = System.currentTimeMillis();
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < taskCount; i++) {
                pool.submit(() -> sleep(blockingMs));
            }
        }
        return System.currentTimeMillis() - start;
    }

    static void printResult(String label, int taskCount, long platformMs, long virtualMs) {
        System.out.printf("  Platform threads : %,6d ms%n", platformMs);
        System.out.printf("  Virtual  threads : %,6d ms%n", virtualMs);
        double speedup = (double) platformMs / virtualMs;
        System.out.printf("  Speedup          : %.1fx faster with virtual threads%n%n", speedup);
    }

    static void sleep(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
