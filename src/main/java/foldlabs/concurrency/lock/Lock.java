package foldlabs.concurrency.lock;

/**
 * Basic lock interface to ensure exclusive access to mutual sections.
 * 
 * @see <b>TAMP</b>, ch.2, p.23
 */
public interface Lock {

    /**
     * Current thread requests exclusive ownership of lock.
     */
    void lock();

    /**
     * Current thread releases ownership of locks.
     */
    void unlock();
}
