import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BlockingOperation {

    private static final List<Integer> data = IntStream.range(1, 100)
            .mapToObj(x -> x)
            .collect(Collectors.toList());

    public static void main(String[] args) {
        final var start = System.currentTimeMillis();

        var sum = data.stream()
                .map($ -> blockingReadOperation($))
                .reduce(0, (a, b) -> a + b);

        System.out.println("sum: "+ sum + ", time taken: " + (System.currentTimeMillis() - start));
        // sum: 9900, time taken: 49721
    }

    private static int blockingReadOperation(int id) {
        try {
            System.out.println("[Current Thread] " + Thread.currentThread().getName());
            Thread.sleep(500);
            return id * 2;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
