package com.cheater.search.Database;

public class ThreadOperation {

    private static Object lock = new Object();

    public static void run(Runnable r) throws InterruptedException {
        synchronized (lock) {
            Thread t = new Thread(r);
            t.start();
            t.join();
        }
    }

}
