package com.paavieira.multithreading.readwritelock;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ReentrantReadWriteLock implements ReadWriteLock {

    private final Map<Thread, Integer> readingThreads = new HashMap<>();
    private Optional<Thread> writingThread = Optional.empty();

    private int writeAccesses = 0;
    private int writeRequests = 0;

    @Override
    public synchronized void acquireReadLock() throws InterruptedException {
        final Thread currentThread = Thread.currentThread();

        while (!canGrantReadAccess(currentThread)) {
            wait();
        }

        // increment read lock counter
        int accessCount = getReadLockCount(currentThread);
        readingThreads.put(currentThread, accessCount + 1);
    }

    @Override
    public synchronized void releaseReadLock() {
        final Thread currentThread = Thread.currentThread();

        if (!isReader(currentThread)) {
            throw new IllegalMonitorStateException("Calling Thread does not hold a read lock on this ReadWriteLock");
        }

        // decrement read lock counter
        int accessCount = getReadLockCount(currentThread);

        if (accessCount == 1) {
            readingThreads.remove(currentThread);
        } else {
            readingThreads.put(currentThread, (accessCount - 1));
        }

        notifyAll();
    }

    @Override
    public synchronized void acquireWriteLock() throws InterruptedException {
        writeRequests++;

        final Thread currentThread = Thread.currentThread();

        while (!canGrantWriteAccess(currentThread)) {
            wait();
        }

        writeRequests--;
        writeAccesses++;
        writingThread = Optional.of(currentThread);
    }

    @Override
    public synchronized void releaseWriteLock()  {
        final Thread currentThread = Thread.currentThread();

        if (!isWriter(currentThread)) {
            throw new IllegalMonitorStateException("Calling Thread does not" + " hold the write lock on this ReadWriteLock");
        }

        writeAccesses--;

        if (writeAccesses == 0) {
            writingThread = Optional.empty();
        }

        notifyAll();
    }

    private boolean canGrantReadAccess(Thread thread) {
        if (isWriter(thread)) {
            // write to read reentrance: if a thread has a "write" lock, no other threads have a "read" or a "write" lock.
            return true;
        }
        if (hasWriter()) {
            // if another thread has a "writer" lock, no "read" locks can be granted.
            return false;
        }
        if (isReader(thread)) {
            // read reentrance: a thread can be granted a "read" lock if it already has a "read" lock.
            return true;
        }
        if (hasWriteRequests()) {
            // keeping track of the "write" locks requests help us avoid a "writer" starvation.
            // (i.e. if new threads were constantly granted "read" locks, a thread waiting for "write" lock would remain blocked forever)
            return false;
        }
        return true;
    }

    private boolean canGrantWriteAccess(Thread callingThread) {
        if (isLastReader(callingThread)) {
            // read to write reentrance: this allows for a thread that have "read" lock to also obtain "write" lock
            return true;
        }
        if (hasReaders()) {
            // a thread can only obtain a "write" lock if no other threads have the "read" lock.
            return false;
        }
        if (writingThread.isEmpty()) {
            // a thread can only obtain a "write" lock if no other thread has the "write" lock.
            return true;
        }
        if (!isWriter(callingThread)) {
            // write reentrance: a thread can only obtain a "write" lock if no other thread has the "write" lock.
            return false;
        }
        return true;
    }


    private int getReadLockCount(Thread thread) {
        return Optional.ofNullable(readingThreads.get(thread)).orElse(0);
    }


    private boolean hasReaders() {
        return readingThreads.size() > 0;
    }

    private boolean isReader(Thread thread) {
        return Optional.ofNullable(readingThreads.get(thread)).isPresent();
    }

    private boolean isLastReader(Thread thread) {
        return readingThreads.size() == 1 && isReader(thread);
    }

    private boolean hasWriter() {
        return !writingThread.isEmpty();
    }

    private boolean isWriter(Thread callingThread) {
        return writingThread.map(t -> t == callingThread).orElse(false);
    }

    private boolean hasWriteRequests() {
        return this.writeRequests > 0;
    }
}