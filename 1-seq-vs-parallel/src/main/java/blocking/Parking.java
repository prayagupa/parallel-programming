package blocking;

import java.util.concurrent.locks.LockSupport;

public class Parking {

    public static void main(String[] args) {

        Thread parkingThread = new Thread(() -> {
            System.out.println("Thread is going to sleep...");
            sleepTwoSeconds();
            System.out.println("Thread is Parking...");

            // this call will return immediately since we have called  LockSupport::unpark
            // before this method is getting called, making the permit available
            long start = System.currentTimeMillis();
            LockSupport.park();
            System.out.println("Thread unparked after " + (System.currentTimeMillis() - start) + " ms");
        });

        parkingThread.start();

        // hopefully this 1 second is enough for "parkingThread" to start
        // _before_ we call un-park
        sleepOneSecond();
        System.out.println("Thread is Un-parking...");

        // making the permit available while the thread is running and has not yet
        // taken this permit, thus "LockSupport.park" will return immediately
        LockSupport.unpark(parkingThread);

    }

    private static void sleepTwoSeconds() {
        try {
            Thread.sleep(1000 * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void sleepOneSecond() {
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
