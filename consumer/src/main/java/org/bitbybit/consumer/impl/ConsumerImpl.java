package org.bitbybit.consumer.impl;

import org.bitbybit.consumer.api.Consumer;
import org.bitbybit.producer.api.Producer;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample consumer implementation.
 */
public class ConsumerImpl implements Consumer {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerImpl.class);

    private ServiceTracker<Producer, Producer> producerTracker;

    @Override public Object consume() {
        return producerTracker.getService().produce();
    }

    public void init() {
        LOG.info("Initializing ServiceTracker");
        producerTracker = new ServiceTracker<>(
                FrameworkUtil.getBundle(ConsumerImpl.class).getBundleContext(),
                Producer.class,
                null);
        producerTracker.open();
    }

    public void destroy() {
        LOG.info("Closing ServiceTracker");
        producerTracker.close();
    }
}

