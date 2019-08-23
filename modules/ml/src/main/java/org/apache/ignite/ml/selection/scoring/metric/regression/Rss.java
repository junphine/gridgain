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

package org.apache.ignite.ml.selection.scoring.metric.regression;

import org.apache.ignite.ml.selection.scoring.evaluator.aggregator.RegressionMetricStatsAggregator;
import org.apache.ignite.ml.selection.scoring.evaluator.context.EmptyContext;
import org.apache.ignite.ml.selection.scoring.metric.Metric;
import org.apache.ignite.ml.selection.scoring.metric.MetricName;

/**
 * Class for RSS metric (residual sum of squares).
 */
public class Rss implements Metric<Double, EmptyContext<Double>, RegressionMetricStatsAggregator> {
    /** Serial version uid. */
    private static final long serialVersionUID = -8963319864310149685L;

    /** Value. */
    private double value = Double.NaN;

    /** {@inheritDoc} */
    @Override public RegressionMetricStatsAggregator makeAggregator() {
        return new RegressionMetricStatsAggregator();
    }

    /** {@inheritDoc} */
    @Override
    public Rss initBy(RegressionMetricStatsAggregator aggr) {
        value  = aggr.getRss();
        return this;
    }

    /** {@inheritDoc} */
    @Override public double value() {
        return value;
    }

    /** {@inheritDoc} */
    @Override public MetricName name() {
        return MetricName.RSS;
    }
}
