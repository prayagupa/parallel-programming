package non_blocking;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BlockingOperationAsAsync {

    private final static int numberOfProcesses = 100;
    public static final ForkJoinPool FORK_JOIN_POOL = ForkJoinPool.commonPool();

    private static final List<Integer> data = IntStream.range(0, numberOfProcesses)
            .mapToObj(x -> x)
            .collect(Collectors.toList());

    public static void main(String[] args) {
        final var start = System.currentTimeMillis();

        var asyncProcesses = data.stream()
                .map($ -> blockingReadOperation($))
                .collect(Collectors.toList());

        final var tasks = asyncProcesses.toArray(new CompletableFuture[asyncProcesses.size()]);
        CompletableFuture.allOf(tasks).thenApply(unused -> {
            var sum = asyncProcesses.stream()
                    .map(task -> task.join())
                    .reduce(0, (a, b) -> a + b);

            System.out.println(
                    "numberOfProcesses: " + numberOfProcesses + ", \n" +
                            "sum: " + sum + ", \n" +
                            "time taken: " + (System.currentTimeMillis() - start) + " ms"
            );

            return sum;
        }).join();

        //numberOfProcesses: 100,
        //sum: 9900,
        //time taken: 7583 ms

    }

    /**
     * blocking calls to HTTP, files, database
     */
    private static CompletableFuture<Integer> blockingReadOperation(int id) {
        return nonBlockingOps(() -> {
            try {
                System.out.println(
                        "[Current Thread] " + Thread.currentThread().getName() + " : " +
                                "processing " + id
                );
                Thread.sleep(500);
                return id * 2;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    private static <A> CompletableFuture<A> nonBlockingOps(Supplier<A> fn) {
        return CompletableFuture.supplyAsync(fn, FORK_JOIN_POOL);
    }
}
