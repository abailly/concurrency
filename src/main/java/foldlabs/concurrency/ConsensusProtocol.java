package foldlabs.concurrency;

import foldlabs.concurrency.util.Threads;

/**
 * A generic <em>wait-free</em> consensus protocol.
 * <p>
 *     Each {@link ConsensusProtocol} object is dedicated to allow a specific {@code numberOfThreads}, fixed at build time,
 *     to decide on a proposed value. Given this number is fixed 
 * </p>
 * @see <b>TAMP</b>, p.106
 */
public abstract class ConsensusProtocol<T> implements Consensus<T> {

    protected final T[] proposals;

    public ConsensusProtocol(int numberOfThreads) {
        proposals = (T[]) new Object[numberOfThreads];
    }
    
    protected void propose(T value) {
        proposals[Threads.id() % proposals.length] = value;
    }
}
