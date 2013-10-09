package foldlabs.concurrency.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class to manipulate threads.
 */
public final class Threads<T extends Thread> implements Iterable<T> {

    private static final AtomicInteger nextID = new AtomicInteger(0);
    
    private static ThreadLocal<Integer> threadId = new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue() {
            return nextID.getAndIncrement();
        }
    };
    
    private final T[] threads;
    private int slot = 0;
    
    public Threads(int numberOfThreads) {
        this.threads = (T[]) new Thread[numberOfThreads];
    }

    /**
     * 
     * @return unique incremental id of current thread.
     */
    public static int id() {
        return threadId.get();
    }

    public void add(T thread) {
        threads[slot++] = thread;
    }

    @Override
    public Iterator<T> iterator() {
        return Arrays.asList(threads).iterator();
    }

    public Threads start() {
        for (Thread thread : threads) {
            thread.start();
        }
        return this;
    }

    public Threads join() throws InterruptedException {
        for (Thread enumerator : this) {
            enumerator.join();
        }
        return this;
    }
}
