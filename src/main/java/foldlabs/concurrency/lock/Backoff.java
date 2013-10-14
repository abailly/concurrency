package foldlabs.concurrency.lock;

import java.util.Random;

/**
 * Adaptive backoff strategy to limit the impact of threads contending for acquiring same resource.
 *
 * @see <b>TAMP</b>, p.148
 */
public class BackOff {

    private final int max;
    private final Random random;

    private int limit;

    public BackOff(int min, int max) {
        this(min, max, new Random());
    }

    BackOff(int min, int max, Random random) {
        this.max = max;
        this.limit = min;
        this.random = random;
    }

    public int backOffTime() {
        int delay = random.nextInt(limit);
        limit = Math.min(max, 2 * limit);
        return delay;
    }
}
