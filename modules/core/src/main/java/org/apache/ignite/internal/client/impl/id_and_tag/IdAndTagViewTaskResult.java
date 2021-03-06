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

package org.apache.ignite.internal.client.impl.id_and_tag;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;
import org.apache.ignite.internal.dto.IgniteDataTransferObject;

/**
 *
 */
public class IdAndTagViewTaskResult extends IgniteDataTransferObject {
    /** */
    private static final long serialVersionUID = 0L;

    /** */
    private UUID id;

    /** */
    private String tag;

    /** Default constructor. */
    public IdAndTagViewTaskResult() {
        // No-op.
    }

    /**
     * @param id Cluster ID.
     * @param tag Cluster tag.
     */
    public IdAndTagViewTaskResult(UUID id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    /** {@inheritDoc} */
    @Override protected void writeExternalData(ObjectOutput out) throws IOException {
        out.writeObject(id);
        out.writeObject(tag);
    }

    /** {@inheritDoc} */
    @Override protected void readExternalData(byte protoVer, ObjectInput in) throws IOException, ClassNotFoundException {
        id = (UUID)in.readObject();
        tag = (String)in.readObject();
    }

    /** */
    public UUID id() {
        return id;
    }

    /** */
    public String tag() {
        return tag;
    }
}
