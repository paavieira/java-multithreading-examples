package com.paavieira.multithreading.readwritelock;

public class Main {

    private static final int THREAD_NUMBER = 5;

    public static void main(String[] args) {
        final ReentrantReadWriteLock sharedLock = new ReentrantReadWriteLock();

        // creating workers
        final Thread[] workers = new Thread[THREAD_NUMBER];
        for (int i = 0; i < THREAD_NUMBER; i++) {
            workers[i] = new Thread(new RandomReaderWriter(i, sharedLock));
        }
        System.out.println("Spawned workers: " + THREAD_NUMBER);

        // starting workers
        for (Thread t : workers) {
            t.start();
        }

        // running workers for 30s
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // stopping workers
        System.out.println("Stopping workers...");
        for (Thread t : workers) {
            t.interrupt();
        }
    }
}
