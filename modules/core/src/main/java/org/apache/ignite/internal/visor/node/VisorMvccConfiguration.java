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

package org.apache.ignite.internal.visor.node;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.visor.VisorDataTransferObject;

/**
 * Data transfer object for data store configuration.
 */
public class VisorMvccConfiguration extends VisorDataTransferObject {
    /** */
    private static final long serialVersionUID = 0L;

    /** Number of MVCC vacuum cleanup threads. */
    private int mvccVacuumThreadCnt;

    /** Time interval between vacuum runs */
    private long mvccVacuumFreq;

    /**
     * Default constructor.
     */
    public VisorMvccConfiguration() {
        // No-op.
    }

    /**
     * Constructor.
     *
     * @param cfg Ignite configuration.
     */
    public VisorMvccConfiguration(IgniteConfiguration cfg) {
        assert cfg != null;

        mvccVacuumThreadCnt = cfg.getMvccVacuumThreadCount();
        mvccVacuumFreq = cfg.getMvccVacuumFrequency();
    }

    /**
     * @return Number of MVCC vacuum threads.
     */
    public int getMvccVacuumThreadCount() {
        return mvccVacuumThreadCnt;
    }

    /**
     * @return Time interval between MVCC vacuum runs in milliseconds.
     */
    public long getMvccVacuumFrequency() {
        return mvccVacuumFreq;
    }

    /** {@inheritDoc} */
    @Override public byte getProtocolVersion() {
        return V1;
    }

    /** {@inheritDoc} */
    @Override protected void writeExternalData(ObjectOutput out) throws IOException {
        out.writeInt(mvccVacuumThreadCnt);
        out.writeLong(mvccVacuumFreq);
    }

    /** {@inheritDoc} */
    @Override protected void readExternalData(byte protoVer, ObjectInput in) throws IOException, ClassNotFoundException {
        mvccVacuumThreadCnt = in.readInt();
        mvccVacuumFreq = in.readLong();
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(VisorMvccConfiguration.class, this);
    }
}
