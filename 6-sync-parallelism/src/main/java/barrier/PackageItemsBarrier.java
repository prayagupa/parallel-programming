package barrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class PackageItemsBarrier {

    boolean readyToPack = false;

    int NO_OF_ITEMS = 3;
    CyclicBarrier packageBarrierWaitingForAllItems = new CyclicBarrier(NO_OF_ITEMS);

    public void initiatePacking() throws InterruptedException {
        for (int index = 0; index < NO_OF_ITEMS; index++) {
            String threadName = "Item-" + index;
            var th = new Thread(new ItemConveyor(index, index + ""), threadName);
            th.start();

            conveyTakesTime(index * 200 + 1000);
        }
    }

    private void conveyTakesTime(int timeInMillis) throws InterruptedException {
        Thread.sleep(timeInMillis);
    }

    public class ItemConveyor implements Runnable {
        private int item;
        private String name;

        public ItemConveyor(int item, String name) {
            this.item = item;
            this.name = name;
        }

        @Override public void run() {
            System.out.println("Conveyor ships " + name + " to destination packing lane.");
            try {
                packageBarrierWaitingForAllItems.await(); //uses ReentrantLock internally
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println("package "  + name + " arrived at packing lane.");
            readyToPack = true;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        PackageItemsBarrier packageItemsBarrier = new PackageItemsBarrier();
        packageItemsBarrier.initiatePacking();
        Thread.sleep(3000);
        var completed = packageItemsBarrier.readyToPack;
        System.out.println(completed);
    }
}
