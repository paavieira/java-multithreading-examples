package com.paavieira.multithreading.philosophers;

import java.util.Optional;

public class Fork {

    private final int id;
    private Optional<Philosopher> takenBy = Optional.empty();

    public Fork(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public synchronized boolean isTakenBy(Philosopher philosopher) {
        return takenBy.map(p -> p.equals(philosopher)).orElse(false);
    }

    public synchronized void pickUp(Philosopher philosopher) throws InterruptedException {
        while (!takenBy.isEmpty()) wait();
        takenBy = Optional.of(philosopher);
    }

    public synchronized void putDown(Philosopher philosopher) throws InterruptedException {
        if (isTakenBy(philosopher)) {
            takenBy = Optional.empty();
            notify();
        }
    }

}
