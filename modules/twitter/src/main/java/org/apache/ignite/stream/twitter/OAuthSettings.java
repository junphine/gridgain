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

package org.apache.ignite.stream.twitter;

import org.jetbrains.annotations.NotNull;

/**
 * OAuth keys holder.
 */
public class OAuthSettings {
    /** */
    private final String consumerKey;

    /** */
    private final String consumerSecret;

    /** */
    private final String accessToken;

    /** */
    private final String accessTokenSecret;

    /**
     * @param consumerKey Consumer key.
     * @param consumerSecret Consumer secret.
     * @param accessToken Access token.
     * @param accessTokenSecret Access secret token.
     */
    public OAuthSettings(
        @NotNull String consumerKey,
        @NotNull String consumerSecret,
        @NotNull String accessToken,
        @NotNull String accessTokenSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
    }

    /**
     * @return Consumer key.
     */
    @NotNull
    public String getConsumerKey() {
        return consumerKey;
    }

    /**
     * @return Consumer secret.
     */
    @NotNull
    public String getConsumerSecret() {
        return consumerSecret;
    }

    /**
     * @return Access token.
     */
    @NotNull
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @return Access token secret.
     */
    @NotNull
    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }
}
