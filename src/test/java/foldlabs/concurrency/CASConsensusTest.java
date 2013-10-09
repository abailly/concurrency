package foldlabs.concurrency;

import foldlabs.concurrency.util.Threads;
import org.junit.Test;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;

public class CASConsensusTest {

    public static final int GENERATIONS = 100_000;
    public static final int NUMBER_OF_THREADS = 10;

    @Test
    public void single_thread_always_wins_consensus_with_itself() throws Exception {
        final CASConsensus<Integer> consensus = new CASConsensus<>(1);

        Thread thread = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 10_000_000; i++) {
                    assertThat(consensus.decide(i)).isEqualTo(i);
                }
            }
        };

        thread.start();
        thread.join();
    }


    @Test
    public void multiple_threads_always_agree_on_one_proposed_value() throws Exception {
        final Threads<EnumeratorWithOffset> enumerators = new Threads<>(NUMBER_OF_THREADS);
        final CASConsensus<Integer>[] consensus = new CASConsensus[GENERATIONS];

        for (int i = 0; i < GENERATIONS; i++) {
            consensus[i] = new CASConsensus<>(NUMBER_OF_THREADS);
        }

        for (int offset = 0; offset < NUMBER_OF_THREADS; offset++) {
            enumerators.add(new EnumeratorWithOffset(offset, consensus));
        }

        enumerators.start().join();

        int[] lastValues = null;

        for (EnumeratorWithOffset enumerator : enumerators) {
            if (lastValues != null) {
                assertThat(lastValues).isEqualTo(enumerator.consensusValues);
            }
            lastValues = enumerator.consensusValues;
        }

    }

    private static class EnumeratorWithOffset extends Thread {

        final int[] consensusValues;
        private final int offset;
        private CASConsensus<Integer>[] consensus;

        public EnumeratorWithOffset(int offset, CASConsensus<Integer>[] consensus) {
            this.offset = offset;
            this.consensus = consensus;
            consensusValues = new int[GENERATIONS];
        }

        @Override
        public void run() {
            for (int i = 0; i < GENERATIONS; i++) {
                int value = i * 10 + offset;
                consensusValues[i] = consensus[i].decide(value);

            }
        }


        @Override
        public String toString() {
            return "EnumeratorWithOffset{" +
                    "consensusValues=" + Arrays.toString(consensusValues) +
                    ", offset=" + offset +
                    ", consensus=" + consensus +
                    '}';
        }

    }
}
