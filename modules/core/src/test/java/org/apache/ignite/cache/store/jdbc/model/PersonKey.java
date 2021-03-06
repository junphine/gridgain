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

package org.apache.ignite.cache.store.jdbc.model;

import java.io.Serializable;

/**
 * PersonKey definition.
 */
public class PersonKey implements Serializable {
    /** */
    private static final long serialVersionUID = 0L;

    /** Value for id. */
    private Integer id;

    /**
     * Empty constructor.
     */
    public PersonKey() {
        // No-op.
    }

    /**
     * Full constructor.
     */
    public PersonKey(
        Integer id
    ) {
        this.id = id;
    }

    /**
     * Gets id.
     *
     * @return Value for id.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id New value for id.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof PersonKey))
            return false;

        PersonKey that = (PersonKey)o;

        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;

        return true;
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
        int res = id != null ? id.hashCode() : 0;

        return res;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return "PersonKey [id=" + id +
            "]";
    }
}
