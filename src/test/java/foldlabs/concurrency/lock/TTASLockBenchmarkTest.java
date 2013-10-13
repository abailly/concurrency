package foldlabs.concurrency.lock;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import foldlabs.concurrency.util.Threads;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.io.*;

public class TTASLockBenchmarkTest {

    @Rule
    public TestRule benchmark = new BenchmarkRule();

    @Before
    public void setup() {
        File output = new File("test");
        if (output.exists() && !output.delete())
            throw new RuntimeException("failed to delete 'test' file");

    }

    @Test
    public void ttas_lock_with_10_threads() throws Exception {
        runNThreads(10);
    }

    @Test
    public void ttas_lock_with_20_threads() throws Exception {
        runNThreads(20);
    }

    @Test
    public void ttas_lock_with_50_threads() throws Exception {
        runNThreads(50);
    }

    @Test
    public void ttas_lock_with_100_threads() throws Exception {
        runNThreads(100);
    }

    private void runNThreads(int numberOfThreads) throws IOException, InterruptedException {
        final PrintWriter writer = new PrintWriter(new FileWriter("test"));
        int totalRange = 10_000_000;

        final TTASLock lock = new TTASLock();

        Threads<WriteInMutex> threads = new Threads<>(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            threads.add(new WriteInMutex(lock, i, writer, totalRange / numberOfThreads));
        }

        threads.start();
        threads.join();

        writer.close();
    }

}
