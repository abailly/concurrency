package foldlabs.concurrency.universal;

import org.junit.Test;

import static foldlabs.concurrency.util.Reflections.findMethod;
import static foldlabs.concurrency.universal.Invokable.Request;
import static org.fest.assertions.Assertions.assertThat;

public class NodeTest {

    @Test
    public void max_returns_null_given_null_array() throws Exception {
        assertThat(Node.max(null)).isNull();
    }

    @Test
    public void max_returns_singleton_given_a_array_of_size_1_with_single_element() throws Exception {
        Node node = new Node(toStringOfString());
        assertThat(Node.max(new Node[]{node})).isSameAs(node);
    }

    private Request toStringOfString() {
        return new Request(findMethod(String.class, "toString"), new Object[0]);
    }

    @Test
    public void max_returns_single_non_null_node_given_a_array_of_size_3_with_single_non_null_element() throws Exception {
        Node node = new Node(toStringOfString());
        assertThat(Node.max(new Node[]{null, node, null})).isSameAs(node);
    }

    @Test
    public void max_returns_element_with_greatest_sequence_number_given_an_array_with_2_nodes_having_different_sequences() throws Exception {
        Node max = new Node(toStringOfString());
        max.setSequence(1);

        Node min = new Node(toStringOfString());

        assertThat(Node.max(new Node[]{min,max})).isSameAs(max);
    }

    @Test
    public void max_returns_first_element_with_greatest_sequence_number_given_an_array_with_2_nodes_having_same_sequences() throws Exception {
        Node max = new Node(toStringOfString());
        max.setSequence(1);

        Node min = new Node(toStringOfString());
        min.setSequence(1);
        
        assertThat(Node.max(new Node[]{min, max})).isSameAs(min);
    }


}
