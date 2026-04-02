package vthreads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VirtualThreadExample {

    private static final int BLOCKING_PERIOD_MS = 500;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Virtual Threads Demo (Java 21) ===\n");

        basicVirtualThread();
        virtualThreadPerTask();
        massiveConcurrency();
        System.out.println("(For platform vs virtual thread comparison, run ThreadComparison.java)");
    }

    // 1. Create a single virtual thread directly
    static void basicVirtualThread() throws InterruptedException {
        System.out.println("-- 1. Basic Virtual Thread --");

        Thread vThread = Thread.ofVirtual()
                .name("my-virtual-thread")
                .start(() -> {
                    System.out.println("Running in: " + Thread.currentThread());
                    System.out.println("Is virtual: " + Thread.currentThread().isVirtual());
                });

        vThread.join();
        System.out.println();
    }

    // 2. Virtual-thread-per-task executor (recommended pattern)
    static void virtualThreadPerTask() throws InterruptedException {
        System.out.println("-- 2. Virtual Thread Per Task Executor --");

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 5; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.printf("Task %d running on %s%n", taskId, Thread.currentThread());
                    simulateBlockingIO(100);
                    System.out.printf("Task %d done%n", taskId);
                });
            }
        } // auto-shuts down and awaits termination
        System.out.println();
    }

    // 3. Massive concurrency — 100k virtual threads
    static void massiveConcurrency() throws InterruptedException {
        System.out.println("-- 3. Massive Concurrency: 100,000 Virtual Threads --");
        int count = 100_000;
        long start = System.currentTimeMillis();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < count; i++) {
                executor.submit(() -> simulateBlockingIO(50));
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("Completed %,d virtual threads in %dms%n", count, elapsed);
    }

    // Simulates a blocking I/O operation (e.g., DB call, HTTP request)
    static void simulateBlockingIO(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
