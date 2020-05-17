package blocking;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<A> {
    private Queue<A> queue = new LinkedList<A>();
    private int capacity;
    private Lock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public void write(A element) throws InterruptedException {
        System.out.println("Thread write: locking " + Thread.currentThread().getName() + " : " + element);
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();
                System.out.println("Thread write: waiting " + Thread.currentThread().getName());
            }

            queue.add(element);
            notEmpty.signal();
        } finally {
            System.out.println("Thread write: unlocking " + Thread.currentThread().getName());
            lock.unlock();
        }
    }

    public A read() throws InterruptedException {
        System.out.println("Thread read: locking " + Thread.currentThread().getName());
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
                System.out.println("Thread read: awaiting" + Thread.currentThread().getName());
            }

            A item = queue.remove();
            notFull.signal();
            return item;
        } finally {
            System.out.println("Thread read: waiting " + Thread.currentThread().getName());
            lock.unlock();
        }
    }
}
