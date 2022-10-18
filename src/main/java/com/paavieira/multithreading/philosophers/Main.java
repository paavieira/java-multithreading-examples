package com.paavieira.multithreading.philosophers;

public class Main {

    private final Thread[] threads = new Thread[5];
    private final Philosopher[] philosophers = new Philosopher[5];
    private final Fork[] forks = new Fork[5];
    private final Semaphore semaphore = new Semaphore(4);

    public Main() {

        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Fork(i);
        }

        for (int i = 0; i < philosophers.length; i++) {
            final String name = "Philosopher " + (i + 1);
            final Fork leftFork = forks[i];
            final Fork rightFork = forks[(i + 1) % forks.length];
            philosophers[i] = new Philosopher(name, leftFork, rightFork, semaphore);
            final Thread t = new Thread(philosophers[i], name);
            threads[i] = t;
        }

    }

    private void startAll() {
        for (Thread t: threads) {
            t.start();
        }
    }

    private void stopAll() {
        for (Thread t: threads) {
            t.interrupt();
        }
    }

    private void sleep(int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void printResults() {
        for (Philosopher p: philosophers) {
            System.out.println(System.nanoTime() + ": %s contemplated %d times and ate %d times.".formatted(p.getName(), p.eaten(), p.contemplated()));
        }
    }

    public static void main(String[] args) throws Exception {
        final Main main = new Main();
        main.startAll();
        main.sleep(10000);
        main.stopAll();
        main.sleep(100);
        main.printResults();
    }


}