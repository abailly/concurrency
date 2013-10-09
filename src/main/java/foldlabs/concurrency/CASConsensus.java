package foldlabs.concurrency;

import foldlabs.concurrency.util.Threads;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * An implementation of {@link ConsensusProtocol} that relies on single {@link java.util.concurrent.atomic.AtomicInteger}
 * for synchronization.
 *
 * @see <b>TAMP</b>, p.117
 */
public class CASConsensus<T> extends ConsensusProtocol<T> {
    
    public static final int DEFAULT_NUMBER = Runtime.getRuntime().availableProcessors() * 2;
    private static final int FIRST = -1;

    private AtomicInteger decider = new AtomicInteger(FIRST);

    public CASConsensus(int numberOfThreads) {
        super(numberOfThreads);
    }

    /**
     * Builds a consensus object with a default number of threads.
     */
    public CASConsensus() {
        super(DEFAULT_NUMBER);
    }

    @Override
    public T decide(T proposal) {
        propose(proposal);
        int id = Threads.id();
        if (decider.compareAndSet(FIRST, id)) {
            return proposals[id];
        }

        return proposals[decider.get()];
    }
}
