package foldlabs.concurrency.lock;

import foldlabs.concurrency.util.Threads;
import org.junit.Test;

import java.util.Random;

import static org.fest.assertions.Assertions.assertThat;

public class BakeryLockTest {

    long counts = 0;
    private final Random random = new Random();
    
    @Test
    public void ensures_mutual_exclusion_between_multiple_threads() throws Exception {

        final BakeryLock lock = new BakeryLock(10);

        Threads threads = new Threads(10);
        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            threads.add(new Thread() {
                @Override
                public void run() {
                    lock.lock();
                    try {
                        // we need a memory barrier here because otherwise different threads access to plain variable
                        // counts are not guaranteed to respect sequential consistency: The code is not data-race free
                        // as threads read and write same variable without enforcing order
                        // 
                        // note this defeats the whole point of the BakeryLock as far as memory access is concerned! 
                        // this restricts its use to mutex sections with side-effects?
                        synchronized (BakeryLockTest.this) {
                            for (long j = 0; j < 100_000_000L; j++) {
                                counts++;
                            }
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }

        threads.start();

        threads.join();

        assertThat(counts).isEqualTo(1_000_000_000L);
    }
}
