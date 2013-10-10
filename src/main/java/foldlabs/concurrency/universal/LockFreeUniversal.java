package foldlabs.concurrency.universal;

import foldlabs.concurrency.util.Threads;

/**
 * Lock-free universal construction of a concurrent object from a sequential one.
 * 
 * <p>The underlying sequential object must be deterministic, complete, and side-effect free. This construction is based
 * uses a list of {@link Node} objects to represent the state of the object. When each thread wants to access the object
 * and do some operation, this operation is reified as a {@link Node} instance which is linked to some other node
 * using maximum <em>sequence</em> order and inserted. Then the result of the operation is computed creating a fresh
 * instance of underlying sequential object, traversing the list starting from the {@code tail} and applying each node's
 * operation on the object.</p>
 * <p>This construction is extremely inefficient because:</p>
 * <ol>
 *     <li>It needs to traverse the whole history of the object to get a result,</li>
 *     <li>Each node contains a single {@link foldlabs.concurrency.Consensus} instance which is used once and only once
 *     to synchronize potential conflicts on which operation should go next across threads.</li>
 * </ol>
 * <p>It is however not hard to imagine some easy improvements. For example, the state of sequential object could be
 * <em>cached</em> at each node and assuming the object provides a <tt>clone()</tt> operation, this would prevent 
 * excessive creation of objects and traversal of the complete log for each operation.</p>
 * 
 * @see <b>TAMP</b>, p.128
 */
public class LockFreeUniversal<S extends Invokable> implements Invokable {

    private final int numberOfThreads;
    private final InvokableFactory<S> factory;
    private Node[] head;
    private Node tail;

    public LockFreeUniversal(int numberOfThreads, InvokableFactory<S> factory) {
        this.numberOfThreads = numberOfThreads;
        this.factory = factory;
        tail = new Node(null, numberOfThreads);
        head = new Node[numberOfThreads];
        tail.setSequence(1);
        for (int i = 0; i < numberOfThreads; i++) {
            head[i] = tail;
        }
    }

    @Override
    public Response apply(Request request) {
        int id = Threads.id() % numberOfThreads;
        Node prefer = new Node(request, numberOfThreads);

        while (prefer.getSequence() == 0) {
            Node before = Node.max(head);
            Node after = before.decide(prefer);
            before.linkTo(after);
            after.setSequence(before.getSequence() + 1);
            head[id] = after;
        }

        S s = factory.create();
        Node current = tail.getNext();
        while (current != prefer) {
            s.apply(current.getRequest());
            current = current.getNext();
        }

        return s.apply(current.getRequest());
    }
}
