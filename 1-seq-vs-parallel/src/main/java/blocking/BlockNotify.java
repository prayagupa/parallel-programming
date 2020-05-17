package blocking;

public class BlockNotify {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Thread BlockNotify: " + Thread.currentThread().getName());
        BlockingQueue<String> q = new BlockingQueue<>(5);
        q.write("1");
        q.write("2");
        q.write("3");
        q.write("4");
        q.write("5");

        q.read();
        q.write("6");
        q.read();
        q.read();
        q.read();
        q.read();
        q.read();
        q.read();
    }
}
