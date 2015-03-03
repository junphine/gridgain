/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.processors.affinity;

import org.apache.ignite.internal.util.typedef.internal.*;
import org.apache.ignite.plugin.extensions.communication.*;

import java.io.*;
import java.nio.*;

/**
 *
 */
public class AffinityTopologyVersion implements Comparable<AffinityTopologyVersion>, Externalizable, Message {
    /** */
    public static final AffinityTopologyVersion NONE = new AffinityTopologyVersion(-1, 0);

    /** */
    public static final AffinityTopologyVersion ZERO = new AffinityTopologyVersion(0, 0);

    /** */
    private long topVer;

    /** */
    private int minorTopVer;

    /**
     * Empty constructor required by {@link Externalizable}.
     */
    public AffinityTopologyVersion() {
        // No-op.
    }

    /**
     * @param topVer Version.
     */
    public AffinityTopologyVersion(
        long topVer,
        int minorTopVer
    ) {
        this.topVer = topVer;
        this.minorTopVer = minorTopVer;
    }

    /**
     * @return Topology version.
     */
    public long topologyVersion() {
        return topVer;
    }

    /**
     * @param topVer New topology version.
     */
    public void topologyVersion(long topVer) {
        this.topVer = topVer;
    }

    /**
     *
     */
    public AffinityTopologyVersion previous() {
        // TODO IGNITE-45.
        return new AffinityTopologyVersion(topVer - 1, 0);
    }

    /** {@inheritDoc} */
    @Override public int compareTo(AffinityTopologyVersion o) {
        return Long.compare(topVer, o.topVer);
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object o) {
        return o instanceof AffinityTopologyVersion && topVer == ((AffinityTopologyVersion)o).topVer;
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
        return (int)topVer;
    }

    /** {@inheritDoc} */
    @Override public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(topVer);
        out.writeInt(minorTopVer);
    }

    /** {@inheritDoc} */
    @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        topVer = in.readLong();
        minorTopVer = in.readInt();
    }

    /** {@inheritDoc} */
    @Override public boolean writeTo(ByteBuffer buf, MessageWriter writer) {
        // TODO: implement.
        return false;
    }

    /** {@inheritDoc} */
    @Override public boolean readFrom(ByteBuffer buf, MessageReader reader) {
        // TODO: implement.
        return false;
    }

    /** {@inheritDoc} */
    @Override public byte directType() {
        return 89;
    }

    /** {@inheritDoc} */
    @Override public byte fieldsCount() {
        // TODO: implement.
        return 0;
    }

    /**
     * @param msgWriter Message writer.
     */
    public boolean writeTo(MessageWriter msgWriter) {
        return msgWriter.writeLong("topVer.idx", topVer);
    }

    /**
     * @param msgReader Message reader.
     */
    public static AffinityTopologyVersion readFrom(MessageReader msgReader) {
        long topVer = msgReader.readLong("topVer.idx");

        return new AffinityTopologyVersion(topVer, 0);
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(AffinityTopologyVersion.class, this);
    }
}
