package foldlabs.concurrency.lock;

import foldlabs.concurrency.util.Threads;

public class BakeryLock implements Lock {

    private final boolean[] flag;
    private final long[] labels;
    private final int numberOfThreads;

    public BakeryLock(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        flag = new boolean[numberOfThreads];
        labels = new long[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            flag[i] = false;
            labels[i] = 0;
        }
    }


    @Override
    public void lock() {
        int index = Threads.id() % numberOfThreads;
        flag[index] = true;
        labels[index] = max(labels) + 1;

        while(thereIsAThreadWithLowerLabel(index)) {};
    }

    private boolean thereIsAThreadWithLowerLabel(int index) {
        for (int i = 0; i < labels.length; i++) {
            if(i != index && compare(i,index) < 0) {
                return false;
            }
        }
        return true;
    }

    private int compare(int threadA, int threadB) {
        long diff = labels[threadA] - labels[threadB];
        return (diff != 0) ? (int) diff : (threadA - threadB);
    }

    private long max(long[] labels) {
        long max = Long.MIN_VALUE;
        for (long label : labels) {
            max = Math.max(label,max);
        }
        return max;
    }

    @Override
    public void unlock() {
        flag[Threads.id() % numberOfThreads] = false;
    }
}
