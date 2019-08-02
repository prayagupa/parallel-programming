package non_blocking;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BlockingOperationAsAsync {

    private static final List<Integer> data = IntStream.range(1, 100)
            .mapToObj(x -> x)
            .collect(Collectors.toList());

    public static void main(String[] args) {
        final var start = System.currentTimeMillis();

        var processes = data.stream()
                .map($ -> blockingReadOperation($))
                .collect(Collectors.toList());

        final var cfs = processes.toArray(new CompletableFuture[processes.size()]);
        CompletableFuture.allOf(cfs).thenApply($ -> {
            var sum = processes.stream()
                    .map($_ -> $_.join())
                    .reduce(0, (a, b) -> a + b);

            System.out.println("sum: " + sum + ", time taken: " + (System.currentTimeMillis() - start));
            return sum;
        }).join();

        //sum: 9900, time taken: 7567
    }

    private static CompletableFuture<Integer> blockingReadOperation(int id) {
        return nonBlockingOps(() -> {
            try {
                System.out.println("[Current Thread] " + Thread.currentThread().getName());
                Thread.sleep(500);
                return id * 2;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    private static <A> CompletableFuture<A> nonBlockingOps(Supplier<A> fn) {
        return CompletableFuture.supplyAsync(fn);
    }
}
