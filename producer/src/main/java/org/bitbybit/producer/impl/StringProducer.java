package org.bitbybit.producer.impl;

import org.bitbybit.producer.api.Producer;

import java.util.Random;

/**
 * Sample producer implementation.
 */
public class StringProducer implements Producer<String> {

    private Random random;

    public StringProducer() {
        random = new Random(System.currentTimeMillis());
    }

    @Override public String produce() {
        return String.valueOf(random.nextInt());
    }
}
