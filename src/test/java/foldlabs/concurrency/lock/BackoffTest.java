package foldlabs.concurrency.lock;

import org.junit.Test;

import java.util.Random;

import static org.fest.assertions.Assertions.assertThat;

public class BackOffTest {

    @Test
    public void increasesBackOffTimeExponentiallyUntilItReachesMax() throws Exception {
        Random random = new Random(12);
        // produces (on 100): 66, 12,  56, 33, 24, 11, 50, 15, 96, 21

        BackOff backOff = new BackOff(10,80, random);
        
        int sleep = backOff.backOffTime();
        sleep = backOff.backOffTime();
        sleep = backOff.backOffTime();
        sleep = backOff.backOffTime();
        sleep = backOff.backOffTime();
        sleep = backOff.backOffTime();
        sleep = backOff.backOffTime();
        sleep = backOff.backOffTime();
        
        assertThat(sleep).isLessThan(80);
    }
    
}
