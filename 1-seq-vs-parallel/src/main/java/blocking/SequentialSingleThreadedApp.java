package blocking;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * [Thread-main] data 0 is processed.
 * [Thread-main] data 1 is processed.
 * [Thread-main] data 2 is processed.
 * [Thread-main] data 3 is processed.
 * [Thread-main] data 4 is processed.
 * [Thread-main] data 5 is processed.
 * [Thread-main] data 6 is processed.
 * [Thread-main] data 7 is processed.
 * [Thread-main] data 8 is processed.
 * [Thread-main] data 9 is processed.
 * numberOfProcesses=10
 * timeTakenMills=10074 ms
 *
 * Process finished with exit code 0
 */
public class SequentialSingleThreadedApp {
    private final static int numberOfProcesses = 10;
    private static final List<Integer> data = IntStream.range(0, numberOfProcesses)
            .mapToObj(x -> x)
            .collect(Collectors.toList());

    public static void main(String[] args) {
        var start = System.currentTimeMillis();
        data.stream()
                .forEach(v -> System.out.println(blockingOperation(v)));

        System.out.println(
                "numberOfProcesses=" + numberOfProcesses + "\n" +
                "timeTakenMills=" + (System.currentTimeMillis() - start + " ms")
        );
    }

    public static String blockingOperation(int input) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "[Thread-" + Thread.currentThread().getName() + "] data " +  input + " is processed.";
    }
}
