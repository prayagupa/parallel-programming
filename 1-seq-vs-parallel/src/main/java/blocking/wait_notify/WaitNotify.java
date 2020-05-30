package blocking.wait_notify;

import java.util.concurrent.CompletableFuture;

/**
 * sending: data1
 * sending: data2
 * sending: data3
 * sent: data1
 * sent: data2
 * sent: data3
 * received: data1
 * received: data2
 * received: data3
 * sending: data11
 * sent: data11
 * received: data11
 */
public class WaitNotify {
    private boolean transfer = true;
    private String packet;

    public synchronized void send(String packet) {
        System.out.println("sending: " + packet);
        while (!transfer) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted");
            }
        }
        transfer = false;

        this.packet = packet;
        System.out.println("sent: " + packet);
        notifyAll();
    }

    public synchronized String receive() {
        while (transfer) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted");
            }
        }
        transfer = true;

        notifyAll();
        System.out.println("received: " + packet);
        return packet;
    }

    public static void main(String[] args) {
        var a = CompletableFuture.runAsync(() -> {
            var wn = new WaitNotify();
            wn.send("data1");
            wn.receive();
            wn.send("data11");
            wn.receive();
        });

        var b = CompletableFuture.runAsync(() -> {
            var wn = new WaitNotify();
            wn.send("data2");
            wn.receive();
        });

        var c = CompletableFuture.runAsync(() -> {
            var wn = new WaitNotify();
            wn.send("data3");
            wn.receive();
        });

        CompletableFuture.allOf(a, b, c).join();
    }
}
