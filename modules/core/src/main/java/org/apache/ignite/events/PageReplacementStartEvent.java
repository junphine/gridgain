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

package org.apache.ignite.events;

import org.apache.ignite.cluster.ClusterNode;

/**
 * Event type indicating that page replacement started in one of the configured data regions.
 *
 * @see EventType#EVT_PAGE_REPLACEMENT_STARTED
 */
public class PageReplacementStartEvent extends EventAdapter {
    /** */
    private static final long serialVersionUID = 0L;

    /** */
    private final String dataRegionName;

    /**
     * @param node Node on which the event was fired.
     * @param msg Optional event message.
     * @param dataRegionName Data region name.
     */
    public PageReplacementStartEvent(ClusterNode node, String msg, String dataRegionName) {
        super(node, msg, EventType.EVT_PAGE_REPLACEMENT_STARTED);

        this.dataRegionName = dataRegionName;
    }

    /**
     * Gets data region name for which page replacement started.
     *
     * @return Data region name.
     */
    public String dataRegionName() {
        return dataRegionName;
    }
}
