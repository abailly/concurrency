package foldlabs.concurrency.lock;

import foldlabs.concurrency.util.Range;
import foldlabs.concurrency.util.Threads;
import org.junit.Test;

import java.io.*;

import static org.fest.assertions.Assertions.assertThat;

public class BakeryLockTest {

    @Test
    public void ensures_mutual_exclusion_between_multiple_threads() throws Exception {
        final PrintWriter writer = new PrintWriter(new FileWriter("test"));

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
                        // note this defeats the whole point of the BakeryLock as far as memory access is concerned as
                        // we would need to put this code inside a synchronized() section, hence we use a side-effect to
                        // ensure we can observe mutual exclusion
                        for (long j = 1_000 * finalI; j < 1_000 * (finalI + 1); j++) {
                            writer.println(j);
                        }
                        writer.flush();
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }

        threads.start();

        threads.join();

        writer.close();

        int[] result = readNumbers();

        assertThat(result).containsOnly(new Range().range(0, 10_000));
    }

    private int[] readNumbers() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("test"));
        String line;
        int[] result = new int[10_000];
        int i = 0;
        while ((line = reader.readLine()) != null) {
            result[i++] = Integer.parseInt(line);
        }
        return result;
    }
}
