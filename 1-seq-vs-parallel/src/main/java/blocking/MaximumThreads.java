package blocking;

/**
 * Total memory:
 * hwmemsize=$(sysctl -n hw.memsize)
 * ramsize=$(expr $hwmemsize / $((1024**3)))
 * echo "System Memory: ${ramsize} GB"
 *
 * On 32Gb,one single thread stack to be 32G
 * java -Xss33554432 1-seq-vs-parallel/src/main/java/blocking/MaximumThreads.java
 * true recursion level was 9799
 * reported recursion level was 1024
 * <p>
 * On 32Gb,one single thread stack to be 16G
 * java -Xss16777216 1-seq-vs-parallel/src/main/java/blocking/MaximumThreads.java
 * true recursion level was 12771
 * reported recursion level was 1024
 * <p>
 * java 1-seq-vs-parallel/src/main/java/blocking/MaximumThreads.java
 * [Thread stack reached the limit]
 * >> true recursion level was 12266
 * >> reported recursion level was 1024
 * ## true recursion level was 14796
 * ## reported recursion level was 1024
 */
public class MaximumThreads {

    private static final int STACK_SIZE_MB = 1 * 1000 * 1000;
    private static int level = 0;

    public static long factorial(int n) {
        level++;
        return n < 2 ? n : n * factorial(n - 1);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("STACK_SIZE_MB: " + STACK_SIZE_MB);
        Thread t = new Thread(null, null, MaximumThreads.class.getSimpleName(), STACK_SIZE_MB) {
            @Override
            public void run() {
                try {
                    level = 0;
                    //32768
                    final int number = 1 << 15;
                    System.out.println(factorial(number));
                } catch (StackOverflowError e) {
                    int stackTraceLevels = e.getStackTrace().length;
                    System.out.println("[Thread stack reached the limit]");
                    System.err.println(">> true recursion level was " + level);
                    System.err.println(">> reported recursion depth was " + stackTraceLevels);
                }
            }
        };
        t.start();
        t.join();
        try {
            level = 0;
            //32768
            final int number = 1 << 15;
            System.out.println(factorial(number));
        } catch (StackOverflowError e) {
            System.err.println(" ## true recursion level was " + level);
            System.err.println(" ## reported recursion level was " + e.getStackTrace().length);
        }
    }

}
