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

package org.apache.ignite.ml.selection.scoring.evaluator.aggregator;

import org.apache.ignite.ml.IgniteModel;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.selection.scoring.evaluator.context.EmptyContext;
import org.apache.ignite.ml.structures.LabeledVector;

/**
 * Class represents statistics aggregator for regression estimation.
 */
public class RegressionMetricStatsAggregator implements MetricStatsAggregator<Double, EmptyContext<Double>, RegressionMetricStatsAggregator> {
    /** Serial version uid. */
    private static final long serialVersionUID = -2459352313996869235L;

    /**
     * Number of examples in dataset.
     */
    private long N = 0;

    /**
     * Absolute error.
     */
    private double absoluteError = Double.NaN;

    /**
     * Resudual sum of squares.
     */
    private double rss = Double.NaN;

    /**
     * Sum of labels.
     */
    private double sumOfYs = Double.NaN;

    /**
     * Sum of squared labels.
     */
    private double sumOfSquaredYs = Double.NaN;

    /**
     * Creates an instance of RegressionMetricStatsAggregator.
     */
    public RegressionMetricStatsAggregator() {
    }

    /**
     * Creates an instance of RegressionMetricStatsAggregator.
     *
     * @param n Number of examples in dataset.
     * @param absoluteError Absolute error.
     * @param rss Rss.
     * @param sumOfYs Sum of labels.
     * @param sumOfSquaredYs Sum of squared labels.
     */
    public RegressionMetricStatsAggregator(long n, double absoluteError, double rss, double sumOfYs,
        double sumOfSquaredYs) {
        N = n;
        this.absoluteError = absoluteError;
        this.rss = rss;
        this.sumOfYs = sumOfYs;
        this.sumOfSquaredYs = sumOfSquaredYs;
    }

    /** {@inheritDoc} */
    @Override public void aggregate(IgniteModel<Vector, Double> model, LabeledVector<Double> vector) {
        N += 1;
        Double prediction = model.predict(vector.features());
        Double truth = vector.label();
        double error = truth - prediction;

        absoluteError = sum(Math.abs(error), absoluteError);
        rss = sum(Math.pow(error, 2), rss);
        sumOfYs = sum(truth, sumOfYs);
        sumOfSquaredYs = sum(Math.pow(truth, 2), sumOfSquaredYs);
    }

    /** {@inheritDoc} */
    @Override public RegressionMetricStatsAggregator mergeWith(RegressionMetricStatsAggregator other) {
        long n = this.N + other.N;
        double absoluteError = sum(this.absoluteError, other.absoluteError);
        double squaredError = sum(this.rss, other.rss);
        double sumOfYs = sum(this.sumOfYs, other.sumOfYs);
        double sumOfSquaredYs = sum(this.sumOfSquaredYs, other.sumOfSquaredYs);

        return new RegressionMetricStatsAggregator(n, absoluteError, squaredError, sumOfYs, sumOfSquaredYs);
    }

    /** {@inheritDoc} */
    @Override public EmptyContext createUnitializedContext() {
        return new EmptyContext();
    }

    /** {@inheritDoc} */
    @Override public void initByContext(EmptyContext context) {

    }

    /**
     * Returns mean absolute error.
     *
     * @return Mean absolute error.
     */
    public double getMAE() {
        if (Double.isNaN(absoluteError))
            return Double.NaN;

        return absoluteError / Math.max(N, 1);
    }

    /**
     * Returns mean squared error.
     *
     * @return Mean squared error.
     */
    public double getMSE() {
        return rss / Math.max(N, 1);
    }

    /**
     * Returns sum of squared errors.
     *
     * @return Sum of squared errors
     */
    public double ysRss() {
        return ysVariance() * Math.max(N, 1);
    }

    /**
     * Returns label variance.
     *
     * @return Label variance.
     */
    public double ysVariance() {
        if (Double.isNaN(sumOfSquaredYs))
            return Double.NaN;

        return (sumOfSquaredYs / Math.max(N, 1) - Math.pow(sumOfYs / Math.max(N, 1), 2));
    }

    /**
     * Returns RSS.
     *
     * @return RSS.
     */
    public double getRss() {
        return rss;
    }

    /**
     * Returns sum of given values considering NaNs.
     *
     * @param v1 First value.
     * @param v2 Second value.
     * @return v1 + v2.
     */
    private double sum(double v1, double v2) {
        if (Double.isNaN(v1))
            return v2;
        else if (Double.isNaN(v2))
            return v1;
        else
            return v1 + v2;
    }
}
