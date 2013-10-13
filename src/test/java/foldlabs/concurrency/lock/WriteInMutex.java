package foldlabs.concurrency.lock;

import java.io.PrintWriter;

class WriteInMutex extends Thread {
    private final Lock lock;
    private final int offset;
    private final PrintWriter writer;
    private long elapsed;
    private int range;

    public WriteInMutex(Lock lock, int offset, PrintWriter writer, int range) {
        this.lock = lock;
        this.offset = offset;
        this.writer = writer;
        this.range = range;
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        lock.lock();
        try {
            // we need a memory barrier here because otherwise different threads access to plain variable
            // counts are not guaranteed to respect sequential consistency: The code is not data-race free
            // as threads read and write same variable without enforcing order. 
            // 
            // note this defeats the whole point of the BakeryLock as far as memory access is concerned as
            // we would need to put this code inside a synchronized() section, hence we use a side-effect to
            // ensure we can observe mutual exclusion. IIRC side-effects act just like memory-barriers and 
            // are expected to happens-before other side-effects
            for (long j = this.range * offset; j < this.range * (offset + 1); j++) {
                writer.println(j);
            }
            writer.flush();
        } finally {
            lock.unlock();
            this.elapsed = System.nanoTime() - start;
        }
    }
}
