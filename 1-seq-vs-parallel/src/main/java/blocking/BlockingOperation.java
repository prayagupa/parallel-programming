package blocking;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BlockingOperation {
    private final static int numberOfProcesses = 100;
    private static final List<Integer> data = IntStream.range(0, numberOfProcesses)
            .mapToObj(x -> x)
            .collect(Collectors.toList());

    public static void main(String[] args) {
        final var start = System.currentTimeMillis();

        var sum = data.stream()
                .map($ -> blockingReadOperation($))
                .reduce(0, (a, b) -> a + b);

        System.out.println(
                "numberOfProcesses: " + numberOfProcesses + ", \n" +
                "sum: "+ sum + ", \n" +
                "time taken: " + (System.currentTimeMillis() - start) + " ms"
        );
//        numberOfProcesses: 100,
//        sum: 9900,
//        time taken: 50298 ms
    }

    private static int blockingReadOperation(int id) {
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
    }
}
