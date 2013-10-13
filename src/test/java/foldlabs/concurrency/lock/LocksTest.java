package foldlabs.concurrency.lock;

import foldlabs.concurrency.util.Range;
import foldlabs.concurrency.util.Threads;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;

import static org.fest.assertions.Assertions.assertThat;

public class LocksTest {

    @Before
    public void setup() throws IOException {
        File output = new File("test");
        if (output.exists()) {
            Files.delete(output.toPath());
        }
    }

    @Test
    public void bakery_lock_ensures_mutual_exclusion_between_multiple_threads() throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter("test"))) {

            final BakeryLock lock = new BakeryLock(10);

            Threads<WriteInMutex> threads = new Threads<>(10);
            for (int i = 0; i < 10; i++) {
                threads.add(new WriteInMutex(lock, i, writer, 1_000));
            }

            threads.start();
            threads.join();

            int[] result = readNumbersFrom("test");

            assertThat(result).containsOnly(new Range().range(0, 10_000));
        }
    }

    @Test
    public void ttas_lock_ensures_mutual_exclusion_between_multiple_threads() throws Exception {
        try (PrintWriter writer = new PrintWriter(new FileWriter("test"))) {

            final TTASLock lock = new TTASLock();

            Threads<WriteInMutex> threads = new Threads<>(10);
            for (int i = 0; i < 10; i++) {
                threads.add(new WriteInMutex(lock, i, writer, 1_000));
            }

            threads.start();
            threads.join();

            int[] result = readNumbersFrom("test");

            assertThat(result).containsOnly(new Range().range(0, 10_000));
        }
    }

    private int[] readNumbersFrom(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int[] result = new int[10_000];
            int i = 0;
            while ((line = reader.readLine()) != null) {
                result[i++] = Integer.parseInt(line);
            }
            return result;
        }
    }

}
