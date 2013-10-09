package foldlabs.concurrency;

/**
 * A consensus for multiple threads to decide on  single value.
 *
 * <p>
 *     Consensus objects are not assumed to be reusable for multiple proposals.
 * </p>
 * @param <T>
 */
public interface Consensus<T> {

    /**
     * Let a bunch of {@link Thread} running concurrently decide over some <tt>value</tt> given various {@code proposal}s.
     * <p>
     * A consensus is a kind of <em>barrier</em> where concurrently running threads meet and must decide on some
     * common value. Basically, the first thread that arrives on the barrier wins and imposes its proposal over the 
     * lagging threads arriving later on the barrier.
     * </p>
     *
     * @param proposal the proposed value for the currently running thread.
     * @return the actual value as agreed with other threads.
     */
    T decide(T proposal);
}
