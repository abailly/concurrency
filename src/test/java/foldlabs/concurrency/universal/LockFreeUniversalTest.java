package foldlabs.concurrency.universal;

import foldlabs.concurrency.util.Reflections;
import foldlabs.concurrency.util.Threads;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayDeque;

import static org.fest.assertions.Assertions.assertThat;

public class LockFreeUniversalTest {

    private static final Method add = Reflections.findMethod(ArrayDeque.class, "add");
    private static final Method poll = Reflections.findMethod(ArrayDeque.class, "poll");

    private static final int NUMBER_OF_THREADS = 10;
    public static final int NUMBER_INSERTED = 10_000;

    @Test
    public void is_identical_to_sequential_execution_when_accessed_through_a_single_thread() throws Exception {
        int[] results = new int[1000];
        ArrayDeque<Integer> queue = new ArrayDeque<>();

        GenericInvokable invokable = new GenericInvokable(queue);

        for (int i = 0; i < 1000; i++) {
            add(invokable, i);
        }

        for (int i = 0; i < 1000; i++) {
            results[i] = poll(invokable);
        }

        assertThat(results).isEqualTo(range(0, 1000));

        LockFreeUniversal universal = new LockFreeUniversal(1, new QueueAsInvokableFactory<Integer>());

        for (int i = 0; i < 1000; i++) {
            add(universal, i);
        }

        for (int i = 0; i < 1000; i++) {
            results[i] = poll(universal);
        }

        assertThat(results).isEqualTo(range(0, 1000));
    }

    @Test
    public void linearizes_multiple_threads_access_in_some_order() throws Exception {
        int[] results = new int[NUMBER_INSERTED];
        final int numberPerThread = NUMBER_INSERTED / NUMBER_OF_THREADS;

        final LockFreeUniversal universal = new LockFreeUniversal(NUMBER_OF_THREADS + 1, new QueueAsInvokableFactory<Integer>());

        final Threads enumerators = new Threads(NUMBER_OF_THREADS);

        for (int offset = 0; offset < NUMBER_OF_THREADS; offset++) {
            final int finalOffset = offset;
            enumerators.add(new Thread() {
                public void run() {
                    for (int i = numberPerThread * finalOffset; i < numberPerThread * (finalOffset + 1); i++) {
                        add(universal, i);
                    }
                }
            });
        }

        enumerators.start().join();

        for (int i = 0; i < NUMBER_INSERTED; i++) {
            results[i] = poll(universal);
        }

        assertThat(results).containsOnly(range(0, NUMBER_INSERTED));
    }


    private Integer poll(Invokable invokable) {
        return (Integer) invokable.apply(new Invokable.Request(poll, new Object[0])).result;
    }

    private void add(Invokable invokable, int i) {
        invokable.apply(new Invokable.Request(add, new Object[]{i}));
    }

    private int[] range(int from, int to) {
        int[] range = new int[to - from];
        for (int i = 0; i < to - from; i++) {
            range[i] = i + from;
        }
        return range;
    }

    private static class QueueAsInvokableFactory<T> implements InvokableFactory<QueueAsInvokable<T>> {
        @Override
        public QueueAsInvokable create() {
            return new QueueAsInvokable();
        }
    }

    private static class QueueAsInvokable<T> extends GenericInvokable<ArrayDeque<T>> {

        public QueueAsInvokable() {
            super(new ArrayDeque<T>());
        }

    }
}
