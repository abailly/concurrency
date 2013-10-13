package foldlabs.concurrency.lock;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simple <em>test-and-test-and-set</em> lock. 
 * 
 * @see <b>TAMP</b>, p.145
 */
public class TTASLock implements Lock {
    
    private final AtomicBoolean state = new AtomicBoolean(false);
    
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void lock() {
        while(true) {
            while(state.get()) {}
            if(!state.getAndSet(true))
                return;
        }
    }

    @Override
    public void unlock() {
        state.set(false);
    }
}
