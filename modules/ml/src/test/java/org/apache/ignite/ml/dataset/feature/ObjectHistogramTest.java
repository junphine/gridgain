/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.ml.dataset.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import org.apache.ignite.ml.math.functions.IgniteFunction;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** */
public class ObjectHistogramTest {
    /** Data first partition. */
    private double[] dataFirstPart = new double[] {0., 0., 0., 0., 1., 1., 1, 2., 2., 3., 4., 5.};
    /** Data second partition. */
    private double[] dataSecondPart = new double[] {0., 1., 0., 1., 0., 1., 0, 1., 0., 1., 0., 5., 6.};

    /** */
    private ObjectHistogram<Double> hist1;
    /** */
    private ObjectHistogram<Double> hist2;

    /**
     *
     */
    @Before
    public void setUp() throws Exception {
        hist1 = new TestHist1();
        hist2 = new TestHist1();

        fillHist(hist1, dataFirstPart);
        fillHist(hist2, dataSecondPart);
    }

    /**
     * @param hist History.
     * @param data Data.
     */
    private void fillHist(ObjectHistogram<Double> hist, double[] data) {
        for (int i = 0; i < data.length; i++)
            hist.addElement(data[i]);
    }

    /**
     *
     */
    @Test
    public void testBuckets() {
        testBuckets(hist1, new int[] {0, 1, 2, 3, 4, 5}, new int[] {4, 3, 2, 1, 1, 1});
        testBuckets(hist2, new int[] {0, 1, 5, 6}, new int[] {6, 5, 1, 1});
    }

    /**
     * @param hist History.
     * @param expBuckets Expected buckets.
     * @param expCounters Expected counters.
     */
    private void testBuckets(ObjectHistogram<Double> hist, int[] expBuckets, int[] expCounters) {
        int size = hist.buckets().size();
        int[] buckets = new int[size];
        int[] counters = new int[size];
        int ptr = 0;
        for (int bucket : hist.buckets()) {
            counters[ptr] = hist.getValue(bucket).get().intValue();
            buckets[ptr++] = bucket;
        }

        assertArrayEquals(expBuckets, buckets);
        assertArrayEquals(expCounters, counters);
    }

    /**
     *
     */
    @Test
    public void testAdd() {
        double val = 100.0;
        hist1.addElement(val);
        Optional<Double> cntr = hist1.getValue(computeBucket(val));

        assertTrue(cntr.isPresent());
        assertEquals(1, cntr.get().intValue());
    }

    /**
     *
     */
    @Test
    public void testAddHist() {
        ObjectHistogram<Double> res = hist1.plus(hist2);
        testBuckets(res, new int[] {0, 1, 2, 3, 4, 5, 6}, new int[] {10, 8, 2, 1, 1, 2, 1});
    }

    /**
     *
     */
    @Test
    public void testDistributionFunction() {
        TreeMap<Integer, Double> distribution = hist1.computeDistributionFunction();

        int[] buckets = new int[distribution.size()];
        double[] sums = new double[distribution.size()];

        int ptr = 0;
        for (int bucket : distribution.keySet()) {
            sums[ptr] = distribution.get(bucket);
            buckets[ptr++] = bucket;
        }

        assertArrayEquals(new int[] {0, 1, 2, 3, 4, 5}, buckets);
        assertArrayEquals(new double[] {4., 7., 9., 10., 11., 12.}, sums, 0.01);
    }

    /** */
    @Test
    public void testOfSum() {
        IgniteFunction<Double, Integer> bucketMap = x -> (int)(Math.ceil(x * 100) % 100);
        IgniteFunction<Double, Double> cntrMap = x -> Math.pow(x, 2);

        ObjectHistogram<Double> forAllHistogram = new TestHist2();
        Random rnd = new Random();
        List<ObjectHistogram<Double>> partitions = new ArrayList<>();
        int cntOfPartitions = rnd.nextInt(100) + 1;
        int sizeOfDataset = rnd.nextInt(10000) + 1;
        for (int i = 0; i < cntOfPartitions; i++)
            partitions.add(new TestHist2());

        for (int i = 0; i < sizeOfDataset; i++) {
            double objVal = rnd.nextDouble();
            forAllHistogram.addElement(objVal);
            partitions.get(rnd.nextInt(partitions.size())).addElement(objVal);
        }

        Optional<ObjectHistogram<Double>> leftSum = partitions.stream().reduce(ObjectHistogram::plus);
        Optional<ObjectHistogram<Double>> rightSum = partitions.stream().reduce((x, y) -> y.plus(x));
        assertTrue(leftSum.isPresent());
        assertTrue(rightSum.isPresent());
        assertTrue(forAllHistogram.isEqualTo(leftSum.get()));
        assertTrue(forAllHistogram.isEqualTo(rightSum.get()));
        assertTrue(leftSum.get().isEqualTo(rightSum.get()));
    }

    /**
     * @param val Value.
     */
    private int computeBucket(Double val) {
        return (int)Math.rint(val);
    }

    /** */
    private static class TestHist1 extends ObjectHistogram<Double> {
        /** Serial version uid. */
        private static final long serialVersionUID = 2397005559193012602L;

        /** {@inheritDoc} */
        @Override public Integer mapToBucket(Double obj) {
            return (int)Math.rint(obj);
        }

        /** {@inheritDoc} */
        @Override public Double mapToCounter(Double obj) {
            return 1.;
        }

        /** {@inheritDoc} */
        @Override public ObjectHistogram<Double> newInstance() {
            return new TestHist1();
        }
    }

    /** */
    private static class TestHist2 extends ObjectHistogram<Double> {
        /** Serial version uid. */
        private static final long serialVersionUID = -2080037140817825107L;

        /** {@inheritDoc} */
        @Override public Integer mapToBucket(Double x) {
            return (int)(Math.ceil(x * 100) % 100);
        }

        /** {@inheritDoc} */
        @Override public Double mapToCounter(Double x) {
            return Math.pow(x, 2);
        }

        /** {@inheritDoc} */
        @Override public ObjectHistogram<Double> newInstance() {
            return new TestHist2();
        }
    }
}
