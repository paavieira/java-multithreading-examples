package com.paavieira.multithreading.philosophers;

public class Philosopher implements Runnable {

    private final String name;
    private final Fork leftFork;
    private final Fork rightFork;
    private final Semaphore semaphore;

    private int contemplated = 0;
    private int eaten = 0;

    public Philosopher(String name, Fork leftFork, Fork rightFork, Semaphore semaphore) {
        this.name = name;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // thinking
                doAction("THINKING");
                contemplated++;

                try {
                    semaphore.take();
                    leftFork.pickUp(this);
                    doAction("Picked up left fork (F%d).".formatted(leftFork.getId()));

                    try {
                        rightFork.pickUp(this);
                        semaphore.release();
                        doAction("Picked up right fork (F%d).".formatted(rightFork.getId()));
                        doAction("EATING");
                        eaten++;
                    } finally {
                        if (rightFork.isTakenBy(this)) {
                            rightFork.putDown(this);
                            doAction("Put down right fork (F%d).".formatted(rightFork.getId()));
                        }
                    }
                } finally {
                    if (leftFork.isTakenBy(this)) {
                        leftFork.putDown(this);
                        doAction("Put down left fork (F%d).".formatted(leftFork.getId()));
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    public String getName() {
        return name;
    }

    public int contemplated() {
        return contemplated;
    }

    public int eaten() {
        return eaten;
    }

    private void doAction(String action) throws InterruptedException {
        System.out.println(System.nanoTime() + ": " + Thread.currentThread().getName() + " " + action);
        Thread.sleep(((int) (Math.random() * 100)));
    }

}