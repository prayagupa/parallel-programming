package locking;

import java.util.concurrent.Semaphore;

public class DatabaseConnectionsUsingSemaphore {

    private Semaphore readLock = new Semaphore(10);
    private Semaphore writeLock = new Semaphore(1);

    public static DatabaseConnectionsUsingSemaphore INSTANCE = new DatabaseConnectionsUsingSemaphore();

    public void getWriteLock() throws InterruptedException {
        System.out.println("write lock");
        writeLock.acquire();
    }

    public void releaseWriteLock() {
        System.out.println("release write lock");
        writeLock.release();
    }

    public void getReadLock() throws InterruptedException {
        readLock.acquire();
    }

    public void releaseReadLock() {
        readLock.release();
    }

    public static void main(String[] args) throws InterruptedException {
        INSTANCE.getWriteLock();

        INSTANCE.releaseWriteLock();
    }
}
