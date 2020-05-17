package locking;

import locking.data.OrderPackage;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ShipPackagesUsingLock {

    Lock listLock = new ReentrantLock();
    Lock stackLock = new ReentrantLock();

    List<OrderPackage> packageList = List.of();
    Stack<OrderPackage> releaseStack = new Stack<OrderPackage>();

    void pushToStackAsync() {

        var removeLastPackageAndPushToStack = new Runnable() {
            public void run() {
                listLock.lock();
                var value = packageList.remove(packageList.size() - 1);
                stackLock.lock();
                releaseStack.push(value);
                System.out.println("removePackageAndPushToStack " + packageList);
                System.out.println("removePackageAndPushToStack " + releaseStack);
                stackLock.unlock();
                listLock.unlock();
            }
        };

        new Thread(removeLastPackageAndPushToStack).start();
    }

    void popFromStackAsync() {
        var popFromStackAndAddToList = new Runnable() {
            public void run() {
                stackLock.lock();
                var value = releaseStack.pop();
                listLock.lock();
                packageList.add(value);
                System.out.println("popFromStackAndAddToList " + packageList);
                System.out.println("popFromStackAndAddToList " + releaseStack);
                listLock.unlock();
                stackLock.unlock();
            }
        };

        new Thread(popFromStackAndAddToList).start();
    }
}
