package com.paavieira.multithreading.philosophers;

public class Semaphore {

    private int signals = 0;
    private final int upperBound;

    public Semaphore(int upperBound) {
        this.upperBound = upperBound;
    }

    public synchronized void take() throws InterruptedException {
        while (signals == upperBound) wait();
        signals++;
    }

    public synchronized void release() throws InterruptedException {
        while (signals == 0) wait();
        signals--;
        notify();
    }
}
