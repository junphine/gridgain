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

package org.apache.ignite.ml.genetic;

import java.util.ArrayList;
import java.util.List;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.ml.genetic.parameter.GAGridConstants;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.LoggerResource;
import org.apache.ignite.transactions.Transaction;

/**
 * Responsible for performing fitness evaluation on an individual chromosome
 */
public class FitnessJob extends ComputeJobAdapter {
    /**
     * Chromosome primary Key
     */
    private Long key;

    /** Ignite instance */
    @IgniteInstanceResource
    private Ignite ignite = null;

    /** Ignite logger */
    @LoggerResource
    private IgniteLogger log = null;

    /** IFitnessFunction */
    private IFitnessFunction fitnessFuncton;

    /**
     * @param key Chromosome primary Key
     * @param fitnessFunction Fitness function defined by developer
     */
    public FitnessJob(Long key, IFitnessFunction fitnessFunction) {
        this.key = key;
        this.fitnessFuncton = fitnessFunction;
    }

    /** {@inheritDoc} */
    @Override public Double execute() throws IgniteException {

        IgniteCache<Long, Chromosome> populationCache = ignite.cache(GAGridConstants.POPULATION_CACHE);

        IgniteCache<Long, Gene> geneCache = ignite.cache(GAGridConstants.GENE_CACHE);

        Chromosome chromosome = populationCache.localPeek(key);

        long[] geneKeys = chromosome.getGenes();

        List<Gene> genes = new ArrayList<Gene>();

        for (int i = 0; i < geneKeys.length; i++) {
            long aKey = geneKeys[i];
            Gene aGene = geneCache.localPeek(aKey);
            genes.add(aGene);
        }

        Double val = fitnessFuncton.evaluate(genes);

        chromosome.setFitnessScore(val);

        Transaction tx = ignite.transactions().txStart();

        populationCache.put(chromosome.id(), chromosome);

        tx.commit();

        return val;
    }

}
