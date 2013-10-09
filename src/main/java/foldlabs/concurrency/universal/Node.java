package foldlabs.concurrency.universal;

import foldlabs.concurrency.CASConsensus;
import foldlabs.concurrency.Consensus;

/**
 * A single invocation of a method on an object.
 * <p>
 * Nodes are linked to other {@link Node} objects to form a <em>log</em> of all the operations applied to some
 * sequential object.
 * </p>
 *
 * @see <b>TAMP</b>, p.125
 */
class Node {

    public static final Node EMPTY = new Node();

    private final Invokable.Request request;
    private final Consensus<Node> decideNext;

    // will be set when linking the Node to its next state 
    private Node next;
    private long sequence = 0L;


    Node(Invokable.Request request, int numberOfThreads) {
        this.request = request;
        decideNext = new CASConsensus<>(numberOfThreads);
    }

    private Node() {
        this(null, CASConsensus.DEFAULT_NUMBER);
    }

    public Node(Invokable.Request request) {
        this(request, CASConsensus.DEFAULT_NUMBER);
    }


    /**
     * Returns the {@link Node} object which has maximal <em>sequence number</em> among the given array of nodes.
     *
     * @param nodes the nodes to search for maximum element. Maybe <tt>null</tt>. <tt>null</tt> elements are simply
     *              ignored for comparison.
     * @return the {@link Node} instance in the array which has maximal sequence number.
     */
    public static Node max(Node[] nodes) {
        if (nodes == null) {
            return null;
        }
        long max = -1L;
        Node best = null;

        for (Node node : nodes) {
            if (node != null && node.sequence > max) {
                best = node;
                max = node.sequence;
            }
        }

        return best;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getSequence() {
        return sequence;
    }

    public Node decide(Node prefer) {
        return decideNext.decide(prefer);
    }

    public void linkTo(Node after) {
        this.next = after;
    }

    public Node getNext() {
        return next;
    }

    public Invokable.Request getRequest() {
        return request;
    }
}

