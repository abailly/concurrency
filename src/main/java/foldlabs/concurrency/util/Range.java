package foldlabs.concurrency.util;

public class Range {
    public int[] range(int from, int to) {
        int[] range = new int[to - from];
        for (int i = 0; i < to - from; i++) {
            range[i] = i + from;
        }
        return range;
    }
}
