package com.paavieira.multithreading.readwritelock;

public interface ReadWriteLock {
    void acquireReadLock() throws InterruptedException;
    void acquireWriteLock() throws InterruptedException;
    void releaseReadLock();
    void releaseWriteLock();
}
