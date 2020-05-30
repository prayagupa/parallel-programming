package blocking;

/**
 * How to stop a java thread gracefully?, http://stackoverflow.com/a/3194618/432903
 */
public class GracefulInterruptionExample {
    public static void main(String[] args) {
        var processor = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    boolean amIinterrupted = Thread.currentThread().isInterrupted();
                    if (!!amIinterrupted) {
                        break;
                    }
                    // do stuff
                    System.out.println("Doing work");
                }
            }
        });
        processor.start();

        // Sleep a second, and then interrupt
        try {
            System.out.println("sleeping");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Processor is interrupted");
        }
        System.out.println("requesting interrupt");
        processor.interrupt();
        System.out.println("done");
    }
}
