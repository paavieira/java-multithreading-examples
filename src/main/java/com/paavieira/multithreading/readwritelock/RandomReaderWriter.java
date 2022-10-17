package com.paavieira.multithreading.readwritelock;

import java.util.Random;

public class RandomReaderWriter implements Runnable, Reader, Writer {
    private static final double WRITE_PROB = 0.5;
    private static final Random rand = new Random();
    private final ReentrantReadWriteLock theLock;
    private final int id;

    public RandomReaderWriter(int id, ReentrantReadWriteLock lock) {
        theLock = lock;
        this.id = id;
    }

    @Override
    public void run() {
        message("Started.");
        while(!Thread.currentThread().isInterrupted()) {
            if (rand.nextDouble() <= WRITE_PROB) {
                write();
            } else {
                read();
            }
        }
    }

    @Override
    public void read() {
        try {
            theLock.acquireReadLock();
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        // perform read
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally { theLock.releaseReadLock(); }
        message("Finished reading.");
    }

    @Override
    public void write() {
        try {
            theLock.acquireWriteLock();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        // perform write
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        finally {
            theLock.releaseWriteLock();
        }
        message("Finished writing.");
    }

    private void message(String msg) {
        System.out.println("Worker " + id + ": " + msg);
    }

}