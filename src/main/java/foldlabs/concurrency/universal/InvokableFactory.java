package foldlabs.concurrency.universal;


public interface InvokableFactory<S extends Invokable> {

    /**
     * 
     * @return a fresh instance of {@code S} in its initial state.
     */
    S create();
}
